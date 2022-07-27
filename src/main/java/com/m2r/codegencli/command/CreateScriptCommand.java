package com.m2r.codegencli.command;

import com.m2r.codegencli.utils.ConsoleUtils;
import com.m2r.codegencli.utils.DirFileUtils;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "create-script")
public class CreateScriptCommand implements Runnable {

    @CommandLine.Parameters(index = "0")
    private String name;

    @Override
    public void run() {
        if (!DirFileUtils.CODEGEN_DIR.exists()) {
            ConsoleUtils.printUninitializedError();
            return;
        }

        DirFileUtils.createFile(DirFileUtils.getScritsDir(), name, "model Person {\n" +
            "\tInteger id\n" +
            "\tString name\n" +
            "\tInteger age\n" +
            "\tFloat weight\n" +
            "\tDate birthday\n" +
            "\tCity city\n" +
            "\tGenderEnum gender\n" +
            "}\n\n" +
            "model City (label: 'Location') {\n" +
            "\tInteger id\n" +
            "\tString name (length: '50')\n" +
            "\tString state\n" +
            "}\n\n" +
            "enum GenderEnum {\n" +
            "\tMALE\n" +
            "\tFEMALE\n" +
            "}\n");
        ConsoleUtils.printSuccess("Script file '"+name+"' created!");

    }

}
