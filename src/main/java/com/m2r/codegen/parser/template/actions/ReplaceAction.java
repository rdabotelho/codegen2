package com.m2r.codegen.parser.template.actions;

public class ReplaceAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() != 2)
            throw new RuntimeException("Replace method required 4 parameters:\n" +
                    "- oldText\n- newText");
    }

    @Override
    public void process(ActionState state) throws Exception {
        String oldText = state.getMethod().getParameter(0).resolveValueToString(state.getMethod().getContext(), "");
        StringBuilder newText = new StringBuilder(state.getMethod().getParameter(1).resolveValueToString(state.getMethod().getContext(), ""));
        String line = state.getContent().toString();
        ActionState subAction = new ActionState(state.getBlock(), state.getMethod(),state.getLevel() + 1,  newText);
        processChildrenMethods(subAction, state.getLevel() + 1, 0, 0);
        state.getContent().setLength(0);
        state.getContent().append(line.replaceAll(oldText, subAction.getContent().toString()));
    }

}
