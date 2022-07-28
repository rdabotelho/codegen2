package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.DefinedMethod;
import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.templatedef.BlockContent;

public class NoIteratorAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
        StringBuilder subContent = new StringBuilder(block.getContent().toString());
        for (Method subMethod : method.getMethods()) {
            boolean show = true;

            if (subMethod.getName().equals(DefinedMethod.SHOW_IF.getName())) {
                method.getContext().put("param1", subMethod.getParameter(1, null).getValue());
                method.getContext().put("param2", subMethod.getParameter(2, null).getValue());
                show = subMethod.getParameter(0).resolveValueToBoolean(method.getContext(), false);
            }

            if (show) {
                DefinedMethod.processMethod(block, method.getContext(), subMethod, subContent);
            }
            else {
                subContent.setLength(0);
            }
        }
        content.setLength(0);
        content.append(subContent);
    }

}
