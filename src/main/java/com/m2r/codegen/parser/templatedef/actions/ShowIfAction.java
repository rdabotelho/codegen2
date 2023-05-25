package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.templatedef.Method;
import com.m2r.codegen.parser.templatedef.Param;

public class ShowIfAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() < 2)
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
        Method method = state.getMethod();
        Param methodParam = method.getParameter(0);
        String attribute = method.getParameter(1).getValue();
        String value = method.getParameter(2).getValue();
        method.getBlock().getContext().put("param1", attribute);
        method.getBlock().getContext().put("param2", value);
        boolean show = state.chainLogicOperation(methodParam.resolveValueToBoolean(method.getBlock().getContext(), false));
        method.getBlock().getContent().setVisible(show);
        if (!show) {
            method.getBlock().getContent().clean();
        }
    }

    private void processTwoParams(ActionState state) throws Exception {
        Method method = state.getMethod();
        Param methodParam = method.getParameter(0);
        String value = method.getParameter(1).getValue();
        method.getBlock().getContext().put("param1", value);
        boolean show = state.chainLogicOperation(methodParam.resolveValueToBoolean(method.getBlock().getContext(), false));
        method.getBlock().getContent().setVisible(show);
        if (!show && state.isLastShowIf()) {
            method.getBlock().getContent().clean();
            method.getBlock().getContent().setVisible(false);
        }
    }

}
