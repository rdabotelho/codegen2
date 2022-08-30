package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.templatedef.Method;
import com.m2r.codegen.parser.templatedefold.Content;

public class ReplaceAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() != 2)
            throw new RuntimeException("Replace method required 4 parameters:\n" +
                    "- oldText\n- newText");
    }

    @Override
    public void process(ActionState state) throws Exception {
        Method method = state.getMethod();
        String oldText = method.getParameter(0).resolveValueToString(method.getBlock().getContext(), "");
        String newText = method.getParameter(1).resolveValueToString(method.getBlock().getContext(), "");
        newText = processChildren(state, newText);
        method.getBlock().getContent().replace(oldText, newText);
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
