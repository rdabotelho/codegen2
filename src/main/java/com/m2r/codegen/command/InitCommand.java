package com.m2r.codegen.command;

import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.DirFileUtils;
import com.m2r.codegen.utils.TemplateRepo;
import picocli.CommandLine;

import java.io.File;

@CommandLine.Command(name = "init")
public class InitCommand implements Runnable {

    @CommandLine.Parameters(index = "0", defaultValue = "")
    private String gitUrl = "";

    @CommandLine.Parameters(index = "1", defaultValue = "master")
    private String gitBranch;

    @Override
    public void run() {
        if (DirFileUtils.CODEGEN_DIR.exists()) {
            ConsoleUtils.printError("Codegen project already initialized!");
            return;
        }
        if (!gitUrl.equals("")) {
            File dest = new File(DirFileUtils.HOME_DIR, ".codegen");
            TemplateRepo.cloneBranch(gitUrl, gitBranch, dest);
        }
        else {
            DirFileUtils.getScritsDir().mkdirs();
            DirFileUtils.getTemplatesDir().mkdirs();
        }
        ConsoleUtils.printSuccess("Codegen project initialized!");
    }

}
