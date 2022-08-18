package com.m2r.codegen.parser.template.actions;

public class MaskAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() != 1)
            throw new RuntimeException("Mask method required 1 parameter:\n" +
                    "- value");
    }

    @Override
    public void process(ActionState state) throws Exception {
        String mask = state.getMethod().getParameter(0).getValue();
        String newValue = String.format(mask, state.getContent().toString());
        state.getContent().setLength(0);
        state.getContent().append(newValue);
    }

}
