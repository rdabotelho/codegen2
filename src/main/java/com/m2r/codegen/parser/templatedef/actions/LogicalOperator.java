package com.m2r.codegen.parser.templatedef.actions;

import java.util.Arrays;

public enum LogicalOperator {
    AND, OR, XOR;

    public static LogicalOperator of(String operator) {
        return Arrays.stream(LogicalOperator.values())
                .filter(it -> it.name().toLowerCase().equals(operator.toLowerCase()))
                .findFirst()
                .orElse(LogicalOperator.AND);
    }

}
