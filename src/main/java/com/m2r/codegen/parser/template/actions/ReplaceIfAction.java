package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.Param;

public class ReplaceIfAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() < 4)
            throw new RuntimeException("ReplaceIf method required at least 4 parameters:\n" +
                    "- oldText\n- newText\n- method\n- value\n- elseValue (optional)");
    }

    @Override
    public void process(ActionState state) throws Exception {
        String oldText = state.getMethod().getParameter(0).resolveValueToString(state.getMethod().getContext(), "");
        StringBuilder newText = new StringBuilder(state.getMethod().getParameter(1).resolveValueToString(state.getMethod().getContext(), ""));
        Param methodParam = state.getMethod().getParameter(2);
        Param valueParam = state.getMethod().getParameter(3);
        Param elseValue = state.getMethod().getParameter(4);

        state.getMethod().getContext().put("param1", valueParam.getValue());
        boolean show = methodParam.resolveValueToBoolean(state.getMethod().getContext(), false);
        if (show) {
            String line = state.getContent().toString();
            ActionState subAction = new ActionState(state.getBlock(), state.getMethod(),state.getLevel() + 1,  newText);
            processChildrenMethods(subAction, state.getLevel() + 1, 0, 0);
            state.getContent().setLength(0);
            state.getContent().append(line.replaceAll(oldText, subAction.getContent().toString()));
        }
        else if (elseValue != null) {
            String line = state.getContent().toString();
            state.getContent().setLength(0);
            state.getContent().append(line.replaceAll(oldText, elseValue.resolveValueToString(state.getMethod().getContext(),"")));
        }
    }

}
