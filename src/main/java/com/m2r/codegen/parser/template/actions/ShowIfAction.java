package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.Param;

public class ShowIfAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() < 2)
            throw new RuntimeException("ShowIf method required 3 parameter: \n" +
                    "- method\n- attribute (optional)\n- value") ;
    }

    @Override
    public void process(ActionState state) throws Exception {
        if (state.getMethod().getParameters().size() == 2) {
            processTwoParams(state);
            return;
        }
        if (state.getMethod().getParameters().size() == 3) {
            processThreeParams(state);
        }
    }

    private void processThreeParams(ActionState state) throws Exception {
        Param methodParam = state.getMethod().getParameter(0);
        String attribute = state.getMethod().getParameter(1).getValue();
        String value = state.getMethod().getParameter(2).getValue();
        state.getMethod().getContext().put("param1", attribute);
        state.getMethod().getContext().put("param2", value);
        boolean show = methodParam.resolveValueToBoolean(state.getMethod().getContext(), false);
        if (!show) {
            state.getContent().setLength(0);
            state.setShow(false);
        }
    }

    private void processTwoParams(ActionState state) throws Exception {
        Param methodParam = state.getMethod().getParameter(0);
        String value = state.getMethod().getParameter(1).getValue();
        state.getMethod().getContext().put("param1", value);
        boolean show = methodParam.resolveValueToBoolean(state.getMethod().getContext(), false);
        if (!show) {
            state.getContent().setLength(0);
            state.setShow(false);
        }
    }

}
