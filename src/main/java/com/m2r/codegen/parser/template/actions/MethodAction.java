package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.templatedef.BlockContent;

public interface MethodAction {

    void validate(Method method) throws RuntimeException;

    void process(BlockContent block, Method method, StringBuilder content) throws Exception;

}
