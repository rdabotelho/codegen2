package com.m2r.codegen.parser.template.actions;

public class NoIteratorAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
    }

    @Override
    public void process(ActionState state) throws Exception {
        processChildrenMethods(state, state.getLevel() + 1, state.getIndex(), state.getSize());
    }

}
