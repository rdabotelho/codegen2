package com.m2r.codegen.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUtils {

    public static void println(String msg) {
        System.out.println(msg);
    }

    public static void printSuccess(String msg) {
        println("\u001B[92m[success]\u001B[0m " + msg);
    }

    public static void printError(String msg) {
        println("\u001B[32m[error]\u001B[0m " + msg);
    }

    public static void printUninitializedError() {
        printError("Codegen project uninitialized!\nUse: codegen init");
    }

    public static String printAndReadOption(String msg) {
        return readLine(msg);
    }

    public static String readLine(String msg) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String name = null;
        try {
            System.out.print("\u001B[92m?\u001B[33m " + msg + ": \u001B[0m");
            return reader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
