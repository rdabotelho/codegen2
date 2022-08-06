package com.m2r.codegen.parser.template;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.template.actions.*;
import com.m2r.codegen.parser.templatedef.BlockContent;

import java.util.Arrays;

public enum DefinedMethod {

    BLOCK("block", new BlockAction(), false),
    ITERATE("iterate", new IterateAction(), false),
    REPLACE("replace", new ReplaceAction(), false),
    SHOW_IF("showIf", new ShowIfAction(), true),
    REPLACE_IF("replaceIf", new ReplaceIfAction(), false),
    DELIMITER("delimiter", new DelimiterAction(), true),
    MASK("mask", new MaskAction(), true);

    private String name;
    private MethodAction action;

    private boolean flag;

    DefinedMethod(String name, MethodAction action, boolean flag) {
        this.name = name;
        this.action = action;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public MethodAction getAction() {
        return action;
    }

    public boolean isFlag() {
        return flag;
    }

    public static DefinedMethod findDefinedMethod(String name) {
        return Arrays.stream(DefinedMethod.values())
                .filter(it -> it.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    public static void processMethod(BlockContent block, ElContext parentContext, Method method, StringBuilder content) throws Exception {
        DefinedMethod definedMethod = DefinedMethod.findDefinedMethod(method.getName());
        if (definedMethod == null) {
            throw new RuntimeException("Method '" + method.getName() + "' undefined!");
        }
        MethodAction action = definedMethod.getAction();
        method.getContext().inheritContext(parentContext);
        action.process(block, method, content);
    }

}
