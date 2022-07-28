package com.m2r.codegen.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUtils {

    public static void println(String msg) {
        System.out.println(msg);
    }

    public static void printSuccess(String msg) {
        println("[success] " + msg);
    }

    public static void printError(String msg) {
        println("[error] " + msg);
    }

    public static void printUninitializedError() {
        printError("Codegen project uninitialized!\nUse: codegen.sh init");
    }

    public static String printAndReadOption(String msg) {
        return readLine(msg);
    }

    public static String readLine(String msg) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String name = null;
        try {
            System.out.print(msg);
            return reader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
