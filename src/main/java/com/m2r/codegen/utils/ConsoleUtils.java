package com.m2r.codegen.utils;

import java.io.*;

public class ConsoleUtils {

    public static void println(String msg) {
        System.out.println(msg);
    }

    public static void printSuccess(String msg) {
        println("\u001B[92m[success]\u001B[0m " + msg);
    }

    public static void printError(String msg) {
        println("\u001B[91m[error]\u001B[0m " + msg);
    }

    public static void printUninitializedError() {
        printError("Codegen project uninitialized!\nUse: codegen init");
    }

    public static String printAndReadOption(String msg, String def) {
        return readLine(msg, def);
    }

    public static String printAndReadOption(String msg) {
        return readLine(msg, null);
    }

    public static String readLine(String msg) {
        return readLine(msg, null);
    }

    public static String readLine(String msg, String def) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            if (def != null) {
                System.out.print("\u001B[92m?\u001B[33m " + msg + "\u001B[0m \u001B[90m(" + def + "): \u001B[0m");
            }
            else {
                System.out.print("\u001B[92m?\u001B[33m " + msg + ": \u001B[0m");
            }
            return reader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
