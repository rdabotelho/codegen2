package com.m2r.codegen.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.UUID;

public class DirFileUtils {

    public static final String CODEGEN_FOLDER = ".codegen";

    public static final String GIT_FOLDER = ".git";

    //private static File HOME_DIR = new File(System.getProperty("user.dir")+"/temp");
    private static File HOME_DIR = new File(System.getProperty("user.dir"));

    private static File CODEGEN_DIR = new File(HOME_DIR, CODEGEN_FOLDER);

    private static File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());

    public static File getHomeDir() {
        return HOME_DIR;
    }

    public static File getCodegenDir() {
        return CODEGEN_DIR;
    }

    public static File getConfigProperties() {
        return new File(getCodegenDir(), "config.properties");
    }

    public static File getTempDir() {
        return TEMP_DIR;
    }

    public static File getModelingDir() {
        return new File(CODEGEN_DIR, "modeling");
    }

    public static File getTemplatesDir() {
        return new File(CODEGEN_DIR, "templates");
    }

    public static void createFile(File dir,  String name, String content) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(dir, name), "UTF-8");
            writer.print(content);
            writer.close();
        }
        catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }
}
