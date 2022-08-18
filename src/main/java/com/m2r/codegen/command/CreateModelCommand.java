package com.m2r.codegen.command;

import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.DirFileUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "create-model")
public class CreateModelCommand implements Runnable {

    @CommandLine.Parameters(index = "0")
    private String name;

    @Override
    public void run() {
        if (!DirFileUtils.getCodegenDir().exists()) {
            ConsoleUtils.printUninitializedError();
            return;
        }

        DirFileUtils.createFile(DirFileUtils.getModelingDir(), name, "entity HelloWorld {\n" +
                "\tString message\n" +
                "}");
        ConsoleUtils.printSuccess("Model file '"+name+"' created!");

    }

}
