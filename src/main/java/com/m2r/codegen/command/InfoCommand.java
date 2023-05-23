package com.m2r.codegen.command;

import com.m2r.codegen.utils.ConsoleUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "-v")
public class InfoCommand implements Runnable {

    @Override
    public void run() {
        String version = "2.1.0";
        ConsoleUtils.println("\u001B[92mCodegen\u001B[0m command line interface (CLI)\nVersion: \u001B[93m" + version + "\u001B[0m");
    }

}
