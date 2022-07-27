package com.m2r.codegencli.parser.template.actions;

import com.m2r.codegencli.parser.template.Method;
import com.m2r.codegencli.parser.templatedef.BlockContent;

public interface MethodAction {

    void validate(Method method) throws RuntimeException;

    void process(BlockContent block, Method method, StringBuilder content) throws Exception;

}
