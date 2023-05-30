package com.m2r.codegen.command;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.el.ElExpr;
import com.m2r.codegen.parser.templatedef.Attribute;
import com.m2r.codegen.parser.templatedef.TemplateDef;
import com.m2r.codegen.parser.templatedef.TemplateDefParser;
import com.m2r.codegen.parser.templatedefold.TemplateProcess;
import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.DirFileUtils;
import com.m2r.codegen.utils.PropertiesManager;
import com.m2r.codegen.utils.TemplateRepo;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@CommandLine.Command(name = "init")
public class InitCommand implements Runnable {

    @CommandLine.Parameters(index = "0", defaultValue = "")
    private String gitUrl = "";

    @CommandLine.Parameters(index = "1", defaultValue = "master")
    private String gitBranch;

    @CommandLine.Option(names = { "-p", "--properties" }, description = "Properties file")
    private File properties;

    private Map<String, TemplateDef> templatesProcessed = new HashMap<>();

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

                File configFile = null;
                if (userPropertiesFile != null) {
                    configFile = userPropertiesFile;
                }
                else {
                    configFile = DirFileUtils.getConfigProperties();
                    if (!configFile.exists()) {
                        configFile = new File(DirFileUtils.getCodegenDir(), "config.yaml");
                    }
                    if (!configFile.exists()) {
                        configFile = new File(DirFileUtils.getCodegenDir(), "config.yml");
                    }
                }

                PropertiesManager propertiesManager = new PropertiesManager();
                if (configFile.exists()) {
                    propertiesManager.load(configFile);
                }

                if (userPropertiesFile == null) {
                    propertiesManager.getProperties().stream().forEach(property -> {
                        while (true) {
                            String def = property.getValue();
                            String newValue = ConsoleUtils.printAndReadOption(property.getLabel(), def);
                            if (!newValue.isBlank()) {
                                if (property.getRegex() != null && !property.getRegex().isBlank()) {
                                    boolean valid = newValue.matches(property.getRegex());
                                    if (!valid) {
                                        ConsoleUtils.printError(property.getLabel() + " invalid!");
                                        continue;
                                    }
                                }
                                property.setValue(newValue);
                                break;
                            }
                            else {
                                if (def != null && !def.isBlank()) {
                                    property.setValue(def);
                                    break;
                                }
                                else if (property.isRequired()) {
                                    ConsoleUtils.printError(property.getLabel() + " is required!");
                                    continue;
                                }
                            }
                        }
                    });
                }

                propertiesManager.store();

                templatesProcessed.clear();
                copyBaseFilesToHome(DirFileUtils.getTempDir(), DirFileUtils.getHomeDir());
                removeAllEmptyDirs();
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
                TemplateDef templateDef = templatesProcessed.get(source.getPath());
                if (templateDef == null) {
                    FileUtils.copyFileToDirectory(source, destDir, true);
                }
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
            if (sourceFile == null)
                throw new RuntimeException("sourceFile attribute required!");
            Attribute targetFile = templateDef.getAttributeByName("targetFile");
            if (targetFile == null)
                throw new RuntimeException("targetFile attribute required!");

            File templateFile = new File(templateDefFile.getParentFile(), sourceFile.getValue());
            TemplateProcess processor = GenerateCommand.parseTemplateDef(templateDef, templateFile);

            ElContext context = new ElContext();
            context.loadFromPropertiesFile();
            processor.getContext().inheritContext(context);

            File templateFileTarget = new File(DirFileUtils.getHomeDir(),  ElExpr.resolve(context, targetFile.getValue()));
            if (!templateFileTarget.getParentFile().exists()) {
                templateFileTarget.getParentFile().mkdirs();
            }

            writer = new FileWriter(templateFileTarget);
            processor.process(writer);

            templatesProcessed.put(templateFile.getPath(), templateDef);
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

    public void removeAllEmptyDirs() {
        Path directoryPath = DirFileUtils.getHomeDir().toPath();
        try {
            Files.walkFileTree(directoryPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (Files.isDirectory(dir) && isEmptyDirectory(dir)) {
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmptyDirectory(Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
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
