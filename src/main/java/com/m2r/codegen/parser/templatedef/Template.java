package com.m2r.codegen.parser.templatedef;

import com.m2r.mdsl.model.Domain;

import java.io.File;

public class Template {

    public static interface GenerationEvent {
        boolean on(Template template, Domain domain, File file);
    }

    private String name;
    private String scope;
    private String directory;
    private String fileName;
    private String outputDir;
    private String outputFileName;
    private String createIf = "true";

    private GenerationEvent startEvent;
    private GenerationEvent endEvent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public GenerationEvent getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(GenerationEvent startEvent) {
        this.startEvent = startEvent;
    }

    public GenerationEvent getEndEvent() {
        return endEvent;
    }

    public void setEndEvent(GenerationEvent endEvent) {
        this.endEvent = endEvent;
    }

    public String getCreateIf() {
        return createIf;
    }

    public void setCreateIf(String createIf) {
        this.createIf = createIf;
    }

}
