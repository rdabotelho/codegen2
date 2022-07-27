package com.m2r.codegencli.command;

import com.m2r.codegencli.parser.el.ElContext;
import com.m2r.codegencli.parser.el.ElExpr;
import com.m2r.codegencli.parser.script.Domain;
import com.m2r.codegencli.parser.script.DomainList;
import com.m2r.codegencli.parser.script.ScriptParser;
import com.m2r.codegencli.parser.template.*;
import com.m2r.codegencli.parser.templatedef.BlockContent;
import com.m2r.codegencli.parser.templatedef.FileContent;
import com.m2r.codegencli.parser.templatedef.TemplateDefParser;
import com.m2r.codegencli.utils.ConsoleUtils;
import com.m2r.codegencli.utils.DirFileUtils;
import picocli.CommandLine;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(name = "generate")
public class GenerateCommand implements Runnable {

    @CommandLine.Parameters(index = "0")
    private String modelFile;

    @CommandLine.Option(names = {"-f", "--force"}, description = "Force override")
    private String force = null;
    @Override
    public void run() {
        if (!DirFileUtils.CODEGEN_DIR.exists()) {
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
        Reader reader = new FileReader(new File(DirFileUtils.getScritsDir(), fileName));
        return ScriptParser.parse(reader);
    }

    private Template parseTemplate(String fileName) throws Exception {
        Reader reader = new FileReader(new File(DirFileUtils.getTemplatesDir(), fileName));
        return TemplateParser.parse(reader);
    }

    private FileContent parseTemplateDef(Template template, File file) throws Exception {
        Reader reader = new FileReader(file);
        return TemplateDefParser.parse(template, reader);
    }

    private void generate(DomainList domainList, Template template, List<File> filesGenerated) throws Exception {
        Attribute sourceFile = template.getAttributeByName("sourceFile");
        if (sourceFile == null)
            throw new RuntimeException("sourceFile attribute required!");
        Attribute targetFile = template.getAttributeByName("targetFile");
        if (targetFile == null)
            throw new RuntimeException("targetFile attribute required!");
        boolean canForce = this.force != null;

        for (Domain domain : domainList.getDomains()) {
            if (!template.consider(domain.getType().toString())) {
                continue;
            }

            ElContext context = new ElContext();
            context.put("domain", domain);

            File templateFile = new File(DirFileUtils.getTemplatesDir(), ElExpr.resolve(context, sourceFile.getValue()));
            FileContent contentFile = parseTemplateDef(template, templateFile);
            contentFile.setContext(context);

            File file = new File(DirFileUtils.HOME_DIR,  ElExpr.resolve(context, targetFile.getValue()));
            if (file.exists() && !canForce) {
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

    private void processBlocks(FileContent fileContent, Writer writer) throws Exception {
        for (BlockContent block : fileContent.getBlocks()) {
            if (block.isDynamic()) {
                block.setContext(fileContent.getContext());
                StringBuilder content = new StringBuilder();
                DefinedMethod.processMethod(block, block.getContext(), block.getMethod(), content);
                writer.write(content.toString());
            }
            else {
                writer.write(block.getContent().toString());
            }
        }
    }

}
