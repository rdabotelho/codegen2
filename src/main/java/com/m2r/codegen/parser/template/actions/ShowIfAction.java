package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.Param;

public class ShowIfAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() < 3)
            throw new RuntimeException("ShowIf method required 3 parameter: \n" +
                    "- method\n- attribute\n- value") ;
    }

    @Override
    public void process(ActionState state) throws Exception {
        Param methodParam = state.getMethod().getParameter(0);
        String attribute = state.getMethod().getParameter(1).getValue();
        String value = state.getMethod().getParameter(2).getValue();
        state.getMethod().getContext().put("param1", attribute);
        state.getMethod().getContext().put("param2", value);
        boolean show = methodParam.resolveValueToBoolean(state.getMethod().getContext(), false);
        if (!show) {
            state.getContent().setLength(0);
        }
    }

}
