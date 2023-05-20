package com.m2r.codegen;

import com.m2r.codegen.command.*;
import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.GraalvmUtils;

import picocli.CommandLine;

@CommandLine.Command(
    subcommands = {
            InitCommand.class,
            CreateTemplateCommand.class,
            CreateModelCommand.class,
            GenerateCommand.class,
            ShiftCommand.class,
            InfoCommand.class
    }
)
public class Main implements Runnable {

    public static void main(String[] args) {
        CommandLine.run(new Main(), args);

        //GraalvmUtils.scanAllMethods();
    }

    @Override
    public void run() {
        ConsoleUtils.println("\nCommands:\n");
        ConsoleUtils.println(" - \u001B[94minit:\u001B[0m Initialize um codegen project");
        ConsoleUtils.println(" - \u001B[94mcreate-template:\u001B[0m Create a new template file");
        ConsoleUtils.println(" - \u001B[94mcreate-model:\u001B[0m Create a new modeling file");
        ConsoleUtils.println(" - \u001B[94mgenerate:\u001B[0m Generate files based on templates");
        ConsoleUtils.println(" - \u001B[94mshift:\u001B[0m Shift blocks automatically in template definition files\n");
    }

}
