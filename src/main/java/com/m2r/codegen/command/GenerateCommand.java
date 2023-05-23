package com.m2r.codegen.command;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.el.ElExpr;
import com.m2r.codegen.parser.templatedef.*;
import com.m2r.codegen.parser.templatedefold.TemplateParser;
import com.m2r.codegen.parser.templatedefold.TemplateProcess;
import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.DirFileUtils;
import com.m2r.mdsl.model.Domain;
import com.m2r.mdsl.model.DomainList;
import com.m2r.mdsl.model.ParamValue;
import com.m2r.mdsl.parser.ModelParser;
import picocli.CommandLine;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
                TemplateDef template = parseTemplate(templateFile.getName());
                generate(domainList, template, filesGenerated);
                filesGenerated.stream().forEach(it -> ConsoleUtils.printSuccess(String.format("File \u001B[33m'%s'\u001B[0m generated!", it.getName())));
            }
            catch (Exception e) {
                e.printStackTrace();
                ConsoleUtils.printError(e.getMessage());
                return;
            }
        }

    }

    private DomainList parseScript(String fileName) throws Exception {
        Reader reader = new FileReader(new File(DirFileUtils.getModelingDir(), fileName));
        try {
            return ModelParser.parse(reader);
        }
        finally {
            reader.close();
        }
    }

    private void generate(DomainList domainList, TemplateDef templateDef, List<File> filesGenerated) throws Exception {
        Attribute sourceFile = templateDef.getAttributeByName("sourceFile");
        if (sourceFile == null)
            throw new RuntimeException("sourceFile attribute required!");
        Attribute targetFile = templateDef.getAttributeByName("targetFile");
        if (targetFile == null)
            throw new RuntimeException("targetFile attribute required!");

        // Scope singleton
        if (templateDef.scope("singleton")) {
            ElContext context = new ElContext();
            context.loadFromPropertiesFile();
            context.put("context", domainList);

            File templateFile = new File(DirFileUtils.getTemplatesDir(), ElExpr.resolve(context, sourceFile.getValue()));
            TemplateProcess processor = parseTemplateDef(templateDef, templateFile);
            processor.getContext().inheritContext(context);

            File file = new File(DirFileUtils.getHomeDir(),  ElExpr.resolve(context, targetFile.getValue()));
            if (file.exists() && !force) {
                String option = ConsoleUtils.printAndReadOption("Override \u001B[92m'" + file.getName() + "'\u001B[0m file (N/y): ");
                if (!option.equalsIgnoreCase("y")) {
                    return;
                }
            }

            file.getParentFile().mkdirs();
            Writer writer = new FileWriter(file);
            processor.process(writer);
            writer.close();
            filesGenerated.add(file);

            return;
        }

        // Scope domain (entity ad enum)
        for (Domain domain : domainList.getDomains()) {
            if (!templateDef.scope(domain.getType().toString())) {
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
            context.loadFromPropertiesFile();
            context.put("domain", domain);

            File templateFile = new File(DirFileUtils.getTemplatesDir(), ElExpr.resolve(context, sourceFile.getValue()));
            TemplateProcess processor = parseTemplateDef(templateDef, templateFile);
            processor.setContext(context);

            File file = new File(DirFileUtils.getHomeDir(),  ElExpr.resolve(context, targetFile.getValue()));
            if (file.exists() && !force) {
                String option = ConsoleUtils.printAndReadOption("Override \u001B[92m'" + file.getName() + "'\u001B[0m file (N/y): ");
                if (!option.equalsIgnoreCase("y")) {
                    continue;
                }
            }

            file.getParentFile().mkdirs();
            Writer writer = new FileWriter(file);
            processor.process(writer);
            writer.close();
            filesGenerated.add(file);
        }
    }

    public static TemplateDef parseTemplate(String fileName) throws Exception {
        Reader reader = new FileReader(new File(DirFileUtils.getTemplatesDir(), fileName));
        try {
            return TemplateDefParser.parse(reader);
        }
        finally {
            reader.close();
        }
    }

    public static TemplateProcess parseTemplateDef(TemplateDef templateDef, File templateFile) throws Exception {
        Reader reader = new FileReader(templateFile);
        try {
            return TemplateParser.parse(templateDef, reader);
        }
        finally {
            reader.close();
        }
    }

}
