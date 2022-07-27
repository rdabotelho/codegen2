package com.m2r.codegen.command;

import com.m2r.codegen.utils.ConsoleUtils;
import com.m2r.codegen.utils.DirFileUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandLine.Command(name = "shift")
public class ShiftCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Template definition file name")
    private String templateDef;

    @CommandLine.Parameters(index = "1", description = "Line to start the shift")
    private String lineFrom;

    @CommandLine.Parameters(index = "2", description = "Count line to shift")
    private String lineCount;

    @Override
    public void run() {
        if (!DirFileUtils.CODEGEN_DIR.exists()) {
            ConsoleUtils.printUninitializedError();
            return;
        }

        try {
            int linefromValue = Integer.parseInt(lineFrom);
            int lineCountValue = Integer.parseInt(lineCount.replace("'", ""));

            Pattern regex = Pattern.compile("block\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
            File file = new File(DirFileUtils.getTemplatesDir(), templateDef);
            Scanner scanner = new Scanner(file);
            StringBuilder buffer = new StringBuilder();
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher m = regex.matcher(line);
                if (m.find()) {
                    int startBefore = Integer.parseInt(m.group(1));
                    if (startBefore >= linefromValue) {
                        int endBefore = Integer.parseInt(m.group(2));
                        int startAfter = startBefore + lineCountValue;
                        int endAfter = endBefore + lineCountValue;
                        String lineBefore = line.replace(String.valueOf(startBefore), "#")
                                .replace(String.valueOf(endBefore), "&")
                                .replace("#", String.valueOf(startAfter))
                                .replace("&", String.valueOf(endAfter));
                        buffer.append(lineBefore+"\n");
                        continue;
                    }
                }
                buffer.append(line+"\n");
            }
            scanner.close();
            DirFileUtils.createFile(DirFileUtils.getTemplatesDir(), templateDef, buffer.toString());
            ConsoleUtils.printSuccess("Template file definition '"+templateDef+"' updated!");
        }
        catch (Exception e) {
            ConsoleUtils.printError(e.getMessage());
        }
    }

}
