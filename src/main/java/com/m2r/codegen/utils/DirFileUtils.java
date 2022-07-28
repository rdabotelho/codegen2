package com.m2r.codegen.utils;

import java.io.File;
import java.io.PrintWriter;

public class DirFileUtils {

    public static File HOME_DIR = new File(System.getProperty("user.dir"));
    public static File CODEGEN_DIR = new File(HOME_DIR, ".codegen.sh");

    public static File getScritsDir() {
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
