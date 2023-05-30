package com.m2r.codegen.utils;

public class Property {

    private String name;

    private String value;
    private String label;
    private boolean required;

    private String regex;

    public Property(String name, String value, String label, boolean required) {
        this.name = name;
        this.value = value;
        this.label = label;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

}
