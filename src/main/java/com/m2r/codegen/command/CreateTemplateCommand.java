package com.m2r.codegen.command;

import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.DirFileUtils;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;

@CommandLine.Command(name = "create-template")
public class CreateTemplateCommand implements Runnable {

    @CommandLine.Parameters(index = "0")
    private String name;

    @Override
    public void run() {
        if (!DirFileUtils.CODEGEN_DIR.exists()) {
            ConsoleUtils.printUninitializedError();
            return;
        }

        DirFileUtils.createFile(DirFileUtils.getTemplatesDir(), name, "package com.m2r.example.entity;\n\n" +
            "public class Entity {\n" +
            "\tprivate String name;\n" +
            "\tpublic String getName() {\n" +
            "\t\treturn this.name;\n" +
            "\t}\n" +
            "\tpublic void setName(String name) {\n" +
            "\t\tthis.name = name;\n" +
            "\t}\n" +
            "}");
        ConsoleUtils.printSuccess("Template file '"+name+"' created!");

        name = FilenameUtils.getBaseName(name) + ".df";
        DirFileUtils.createFile(DirFileUtils.getTemplatesDir(), name, "template {\n" +
            "\tsourceFile: 'entity.java'\n" +
            "\ttargetFile: 'src/main/java/com/m2r/example/entity/${domain.name}.java'\n" +
            "\tconsider: 'entity'\n" +
            "\tblock(3, 3) {\n" +
            "\t\treplace('Entity', domain.name)\n" +
            "\t}\n" +
            "\tblock(4, 4) {\n" +
            "\t\titerate(domain.attributes, item) {\n" +
            "\t\t\treplace('String', item.type)\n" +
            "\t\t\treplace('name', item.name)\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tblock(5, 7) {\n" +
            "\t\titerate(domain.attributes, item) {\n" +
            "\t\t\treplace('String', item.type)\n" +
            "\t\t\treplace('name', item.name)\n" +
            "\t\t\treplace('Name', item.name.pascalCase)\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tblock(8, 10) {\n" +
            "\t\titerate(domain.attributes, item) {\n" +
            "\t\t\treplace('String', item.type)\n" +
            "\t\t\treplace('name', item.name)\n" +
            "\t\t\treplace('Name', item.name.pascalCase)\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}\n");
        ConsoleUtils.printSuccess("Template file definition '"+name+"' created!");

    }

}
