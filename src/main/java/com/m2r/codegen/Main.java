package com.m2r.codegen;

import com.m2r.codegen.command.*;
import com.m2r.codegen.utils.ConsoleUtils;
import picocli.CommandLine;

@CommandLine.Command(
    subcommands = {
            InitCommand.class,
            CreateTemplateCommand.class,
            CreateModelCommand.class,
            GenerateCommand.class,
            ShiftCommand.class
    }
)
public class Main implements Runnable {

    public static void main(String[] args) throws Exception {
        CommandLine.run(new Main(), args);
    }

    @Override
    public void run() {
        ConsoleUtils.println("Use:");
        ConsoleUtils.println("\t- init: Initialize a codegen project");
        ConsoleUtils.println("\t- create-template: Create a new template file");
        ConsoleUtils.println("\t- create-model: Create a new model file");
        ConsoleUtils.println("\t- generate: Generate files");
        ConsoleUtils.println("\t- shift: Shift blocks automatically");
    }

}
