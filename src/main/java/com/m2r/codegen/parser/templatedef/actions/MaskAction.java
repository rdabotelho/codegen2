package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.templatedef.Method;

public class MaskAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() != 1)
            throw new RuntimeException("Mask method required 1 parameter:\n" +
                    "- value");
    }

    @Override
    public void process(ActionState state) throws Exception {
        String mask = state.getMethod().getParameter(0).getValue();
        state.getMethod().getBlock().getContent().format(mask, state.getMethod().getBlock().getContent().toString());
    }

}
