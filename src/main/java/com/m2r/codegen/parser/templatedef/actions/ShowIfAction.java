package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.templatedef.DefinedMethod;
import com.m2r.codegen.parser.templatedef.Method;
import com.m2r.codegen.parser.templatedef.Param;

import java.util.List;
import java.util.stream.Collectors;

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
        method.setLogicState(methodParam.resolveValueToBoolean(method.getBlock().getContext(), false));
        method.getBlock().getContent().setVisible(true);
        if (hide(state)) {
            method.getBlock().getContent().clean();
            method.getBlock().getContent().setVisible(false);
        }
    }

    private void processTwoParams(ActionState state) throws Exception {
        Method method = state.getMethod();
        Param methodParam = method.getParameter(0);
        String value = method.getParameter(1).getValue();
        method.getBlock().getContext().put("param1", value);
        method.setLogicState(methodParam.resolveValueToBoolean(method.getBlock().getContext(), false));
        method.getBlock().getContent().setVisible(true);
        if (hide(state)) {
            method.getBlock().getContent().clean();
            method.getBlock().getContent().setVisible(false);
        }
    }

    private boolean hide(ActionState state) {
        return isLast(state) && !getLogicalResult(state);
    }

    private boolean isLast(ActionState state) {
        List<Method> methods = state.getMethod().getParent().getMethods().stream()
                .filter(it -> it.getName().equals(DefinedMethod.SHOW_IF.getName()))
                .collect(Collectors.toList());
        Method last = methods.size() > 0 ? methods.get(methods.size() - 1) : null;
        return last == state.getMethod();
    }

    private boolean getLogicalResult(ActionState state) {
        List<Method> methods = state.getMethod().getParent().getMethods().stream()
                .filter(it -> it.getName().equals(DefinedMethod.SHOW_IF.getName()))
                .collect(Collectors.toList());
        LogicalOperator operator = state.getMethod().getBlock().getLogicalOperator();
        boolean result = LogicalOperator.AND.equals(operator);
        for (Method method : methods) {
            switch (operator) {
                case AND:
                    result = result && method.isLogicState();
                    break;
                case OR:
                    result = result || method.isLogicState();
                    break;
                case XOR:
                    result = result ^ method.isLogicState();
                    break;
            }
        }
        return result;
    }

}
