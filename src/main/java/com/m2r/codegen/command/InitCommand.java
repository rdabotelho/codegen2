package com.m2r.codegen.command;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.templatedef.Attribute;
import com.m2r.codegen.parser.templatedef.TemplateDef;
import com.m2r.codegen.parser.templatedef.TemplateDefParser;
import com.m2r.codegen.parser.templatedefold.TemplateProcess;
import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.DirFileUtils;
import com.m2r.codegen.utils.TemplateRepo;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;
import java.io.*;
import java.util.Arrays;
import java.util.Properties;

@CommandLine.Command(name = "init")
public class InitCommand implements Runnable {

    @CommandLine.Parameters(index = "0", defaultValue = "")
    private String gitUrl = "";

    @CommandLine.Parameters(index = "1", defaultValue = "master")
    private String gitBranch;

    @CommandLine.Option(names = { "-p", "--properties" }, description = "the archive file")
    private File properties;

    @Override
    public void run() {
        if (DirFileUtils.getCodegenDir().exists()) {
            ConsoleUtils.printError("Codegen project already initialized!");
            return;
        }
        if (!gitUrl.equals("")) {
            if (properties != null && !properties.exists()) {
                ConsoleUtils.printError("Properties file not found: " + properties.getName() + "!");
                return;
            }
            initCodegenFromGit(properties);
        }
        else {
            initCodegen();
        }
        ConsoleUtils.printSuccess("Codegen project initialized!");
    }

    private void initCodegen() {
        DirFileUtils.getModelingDir().mkdirs();
        DirFileUtils.getTemplatesDir().mkdirs();
        DirFileUtils.createFile(DirFileUtils.getCodegenDir(), "config.properties", "PROJECT_NAME=HelloWorld");
    }

    private void initCodegenFromGit(File userPropertiesFile) {
        try {

            boolean isEmpty = DirFileUtils.getHomeDir().toPath().toFile().listFiles(new FilterNotHidden()).length == 0;

            if (isEmpty) {
                FileUtils.cleanDirectory(DirFileUtils.getHomeDir());
                cloneCodegenProject();
                Properties configProperties = new Properties();
                File targetConfigFile = new File(DirFileUtils.getCodegenDir(), "config.properties");
                File configFile = userPropertiesFile != null ? userPropertiesFile : targetConfigFile;
                configProperties.load(new FileInputStream(configFile));
                if (userPropertiesFile == null) {
                    configProperties.forEach((key, value) -> {
                        String newValue = ConsoleUtils.printAndReadOption( key + " [" + value + "]");
                        configProperties.put(key, newValue.isEmpty() ? value : newValue);
                    });
                }
                configProperties.store(new FileOutputStream(targetConfigFile), null);
                copyBaseFilesToHome(DirFileUtils.getTempDir(), DirFileUtils.getHomeDir());
            }
            else {
                cloneCodegenProject();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error copying base directory: " + e.getMessage(), e);
        }
        finally {
            if (DirFileUtils.getTempDir().exists()) {
                new Thread(() -> {
                        try {
                            FileUtils.forceDelete(DirFileUtils.getTempDir());
                        }
                        catch (Exception e) {
                        }
                    }
                ).start();
            }
        }
    }

    private void cloneCodegenProject() throws Exception {
        TemplateRepo.cloneBranch(gitUrl, gitBranch, DirFileUtils.getTempDir());
        if (!isGitRepositoryValid(DirFileUtils.getTempDir())) {
            throw new RuntimeException("It is not a gencode valid git repository!");
        }
        FileUtils.copyDirectoryToDirectory(new File(DirFileUtils.getTempDir(), ".codegen"), DirFileUtils.getHomeDir());
    }

    private void copyBaseFilesToHome(File sourceDir, File destDir) throws Exception {
        File[] files = sourceDir.listFiles();
        Arrays.sort(files, (a, b) -> isDefinitionFile(a) ? -1 : 0);
        for (File source : files) {
            if (source.isHidden()) {
                continue;
            }
            if (isDefinitionFile(source)) {
                processDefinitionFile(source);
                continue;
            }
            if (source.isDirectory()) {
                File dir = new File(destDir, source.getName());
                dir.mkdirs();
                copyBaseFilesToHome(source, dir);
            }
            else {
                FileUtils.copyFileToDirectory(source, destDir, true);
            }
        }
    }

    private void processDefinitionFile(File templateDefFile) throws Exception {
        Reader reader = null;
        Writer writer = null;
        try {
            reader = new FileReader(templateDefFile);
            TemplateDef templateDef = TemplateDefParser.parse(reader);
            Attribute sourceFile = templateDef.getAttributeByName("sourceFile");
            File templateFile = new File(templateDefFile.getParentFile(), sourceFile.getValue());
            TemplateProcess processor = GenerateCommand.parseTemplateDef(templateDef, templateFile);

            ElContext context = new ElContext();
            context.loadFromPropertiesFile();
            processor.getContext().inheritContext(context);

            writer = new FileWriter(templateFile);
            processor.process(writer);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    private boolean isDefinitionFile(File file) {
        return file.getName().startsWith("_") && file.getName().endsWith(".df");
    }

    private boolean isGitRepositoryValid(File dir) {
       File gencodeDir = Arrays.stream(dir.listFiles()).filter(it -> it.getName().equals(".codegen")).findFirst().orElse(null);
       if (gencodeDir == null) {
           return false;
       }
       return true;
    }

    class FilterNotHidden implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return !pathname.isHidden();
        }
    }

}
