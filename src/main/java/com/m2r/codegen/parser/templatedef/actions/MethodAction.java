package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.templatedef.Method;

public interface MethodAction {

    void validate(Method method) throws RuntimeException;

    void process(ActionState state) throws Exception;

}