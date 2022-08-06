package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.templatedef.BlockContent;

public class MaskAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() != 1)
            throw new RuntimeException("Mask method required 1 parameter:\n" +
                    "- value");
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
    }

}
