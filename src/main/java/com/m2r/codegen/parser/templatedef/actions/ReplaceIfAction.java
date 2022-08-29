package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.templatedef.Method;
import com.m2r.codegen.parser.templatedef.Param;
import com.m2r.codegen.parser.templatedefold.Block;
import com.m2r.codegen.parser.templatedefold.Content;

public class ReplaceIfAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() < 4)
            throw new RuntimeException("ReplaceIf method required at least 4 parameters:\n" +
                    "- oldText\n- newText\n- method\n- value\n- elseValue (optional)");
    }

    @Override
    public void process(ActionState state) throws Exception {
        String oldText = state.getMethod().getParameter(0).resolveValueToString(state.getMethod().getBlock().getContext(), "");
        String newText = state.getMethod().getParameter(1).resolveValueToString(state.getMethod().getBlock().getContext(), "");
        Param methodParam = state.getMethod().getParameter(2);
        Param valueParam = state.getMethod().getParameter(3);
        Param elseValue = state.getMethod().getParameter(4);

        state.getMethod().getBlock().getContext().put("param1", valueParam.getValue());
        boolean show = methodParam.resolveValueToBoolean(state.getMethod().getBlock().getContext(), false);
        if (show) {
            newText = processChildren(state, newText);
            state.getMethod().getBlock().getContent().replace(oldText, newText);
        }
        else if (elseValue != null) {
            state.getMethod().getBlock().getContent().replace(oldText, elseValue.getValue());
        }
    }

    private String processChildren(ActionState state, String newText) throws Exception {
        Content content = state.getMethod().getBlock().getContent();
        String backup = content.toString();
        state.getMethod().getBlock().getContent().substitute(newText);
        for (Method subMethod : state.getMethod().getMethods()) {
            subMethod.process();
        }
        String result = content.toString();
        content.substitute(backup);
        return result;
    }

}
