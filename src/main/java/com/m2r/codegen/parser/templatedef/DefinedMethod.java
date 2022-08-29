package com.m2r.codegen.parser.templatedef;

import com.m2r.codegen.parser.templatedef.actions.*;
import java.util.Arrays;

public enum DefinedMethod {

    BLOCK("block", new BlockAction()),
    ITERATE("iterate", new IterateAction()),
    REPLACE("replace", new ReplaceAction()),
    SHOW_IF("showIf", new ShowIfAction()),
    REPLACE_IF("replaceIf", new ReplaceIfAction()),
    DELIMITER("delimiter", new DelimiterAction()),
    MASK("mask", new MaskAction());

    private String name;
    private MethodAction action;

    DefinedMethod(String name, MethodAction action) {
        this.name = name;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public MethodAction getAction() {
        return action;
    }

    public static DefinedMethod findDefinedMethod(String name) {
        return Arrays.stream(DefinedMethod.values())
                .filter(it -> it.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}
