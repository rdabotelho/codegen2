package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.DefinedMethod;
import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.templatedef.BlockContent;

public class BlockAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() != 2)
            throw new RuntimeException("Block method required 2 parameters (startLine and endLine)") ;
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
        for (Method subMethod : method.getMethods()) {
            StringBuilder buffer = new StringBuilder();
            if (subMethod.getName().equals("iterate")) {
                DefinedMethod.processMethod(block, method.getContext(), subMethod, content);
            }
            else {
                MethodAction action = new NoIteratorAction();
                method.getContext().inheritContext(method.getContext());
                action.process(block, method, content);
            }
            content.append(buffer.toString());
        }
    }

}
