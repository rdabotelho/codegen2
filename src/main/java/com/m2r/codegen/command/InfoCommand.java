package com.m2r.codegen.command;

import com.m2r.codegen.utils.ConsoleUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "-v")
public class InfoCommand implements Runnable {

    @Override
    public void run() {
        ConsoleUtils.println("Codegen command line interface (CLI)\nVersion: 2.0.2");
    }

}
