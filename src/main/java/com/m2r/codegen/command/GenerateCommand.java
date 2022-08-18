package com.m2r.codegen.command;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.el.ElExpr;
import com.m2r.codegen.parser.modeling.Domain;
import com.m2r.codegen.parser.modeling.DomainList;
import com.m2r.codegen.parser.modeling.ModelingParser;
import com.m2r.codegen.parser.modeling.ParamValue;
import com.m2r.codegen.parser.template.*;
import com.m2r.codegen.parser.template.actions.ActionState;
import com.m2r.codegen.parser.template.actions.MethodAction;
import com.m2r.codegen.parser.templatedef.BlockContent;
import com.m2r.codegen.parser.templatedef.FileContent;
import com.m2r.codegen.parser.templatedef.TemplateDefParser;
import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.DirFileUtils;
import picocli.CommandLine;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CommandLine.Command(name = "generate")
public class GenerateCommand implements Runnable {

    @CommandLine.Parameters(index = "0")
    private String modelFile;

    @CommandLine.Option(names = {"-f", "--force"}, description = "Force override")
    boolean force;

    @Override
    public void run() {
        if (!DirFileUtils.getCodegenDir().exists()) {
            ConsoleUtils.printUninitializedError();
            return;
        }

        File[] templateFiles = DirFileUtils.getTemplatesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".df");
            }
        });

        for (File templateFile : templateFiles) {
            List<File> filesGenerated = new ArrayList<>();
            try {
                DomainList domainList = parseScript(modelFile);
                Template template = parseTemplate(templateFile.getName());
                generate(domainList, template, filesGenerated);
                filesGenerated.stream().forEach(it -> ConsoleUtils.printSuccess(String.format("File '%s' generated!", it.getName())));
            }
            catch (Exception e) {
                ConsoleUtils.printError(e.getMessage());
                return;
            }
        }

    }

    private DomainList parseScript(String fileName) throws Exception {
        Reader reader = new FileReader(new File(DirFileUtils.getModelingDir(), fileName));
        return ModelingParser.parse(reader);
    }

    private void generate(DomainList domainList, Template templateDef, List<File> filesGenerated) throws Exception {
        Attribute sourceFile = templateDef.getAttributeByName("sourceFile");
        if (sourceFile == null)
            throw new RuntimeException("sourceFile attribute required!");
        Attribute targetFile = templateDef.getAttributeByName("targetFile");
        if (targetFile == null)
            throw new RuntimeException("targetFile attribute required!");

        for (Domain domain : domainList.getDomains()) {
            if (!templateDef.consider(domain.getType().toString())) {
                continue;
            }
            ParamValue consider = domain.getParam("exclude");
            if (consider != null) {
                String[] sourceFileParts = sourceFile.getValue().split("\\.");
                if (consider.hasValue(sourceFile.getValue()) || consider.hasValue(sourceFileParts[0])) {
                    continue;
                }
            }

            ElContext context = new ElContext();
            context.put("domain", domain);

            File templateFile = new File(DirFileUtils.getTemplatesDir(), ElExpr.resolve(context, sourceFile.getValue()));
            FileContent contentFile = parseTemplateDef(templateDef, templateFile);

            File configFile = new File(DirFileUtils.getCodegenDir(), "config.properties");
            Properties configProperties = new Properties();
            configProperties.load(new FileInputStream(configFile));
            configProperties.forEach((key, value) -> context.put(key.toString(), value));
            contentFile.setContext(context);

            File file = new File(DirFileUtils.getHomeDir(),  ElExpr.resolve(context, targetFile.getValue()));
            if (file.exists() && !force) {
                String option = ConsoleUtils.printAndReadOption("Override '" + file.getName() + "' file (N/y): ");
                if (!option.equalsIgnoreCase("y")) {
                    continue;
                }
            }

            file.getParentFile().mkdirs();
            Writer writer = new FileWriter(file);
            processBlocks(contentFile, writer);
            writer.close();
            filesGenerated.add(file);
        }
    }

    public static Template parseTemplate(String fileName) throws Exception {
        Reader reader = new FileReader(new File(DirFileUtils.getTemplatesDir(), fileName));
        return TemplateParser.parse(reader);
    }

    public static FileContent parseTemplateDef(Template templateDef, File templateFile) throws Exception {
        Reader reader = new FileReader(templateFile);
        return TemplateDefParser.parse(templateDef, reader);
    }

    public static void processBlocks(FileContent fileContent, Writer writer) throws Exception {
        for (BlockContent block : fileContent.getBlocks()) {
            if (block.isDynamic()) {
                block.setContext(fileContent.getContext());
                block.getMethod().getContext().inheritContext(block.getContext());
                ActionState state = new ActionState(block, block.getMethod(), 0, block.getContent());
                MethodAction blockAction = DefinedMethod.BLOCK.getAction();
                blockAction.validate(state);
                blockAction.process(state);
                writer.write(state.getContent().toString());
            }
            else {
                writer.write(block.getContent().toString());
            }
        }
    }

}
