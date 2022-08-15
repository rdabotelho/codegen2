package com.m2r.codegen.parser.modeling;

import java.util.Arrays;

public enum BasicType {

    STRING("String"),
    INTEGER("Integer"),
    LONG("Long"),
    DOUBLE("Double"),
    FLOAT("Float"),
    BOOLEAN("Boolean"),
    NUMBER("Number"),
    DATE("Date"),
    DATETIME("DateTime");

    private String value;

    BasicType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BasicType parse(String value) {
        return Arrays.stream(values()).filter(it -> it.getValue().equalsIgnoreCase(value)).findFirst().orElse(null);
    }

    public static boolean isValid(String value) {
        return parse(value) != null;
    }

}
