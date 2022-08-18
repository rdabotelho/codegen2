package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.DefinedMethod;
import com.m2r.codegen.parser.template.Method;

public class BlockAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() != 2)
            throw new RuntimeException("Block method required 2 parameters (startLine and endLine)") ;
    }

    @Override
    public void process(ActionState state) throws Exception {
        for (Method subMethod : state.getMethod().getMethods()) {
            DefinedMethod definedMethod = DefinedMethod.findDefinedMethod(subMethod.getName());
            if (definedMethod == null) {
                throw new RuntimeException("Method '" + subMethod.getName() + "' undefined!");
            }
            if (subMethod.getName().equals("iterate")) {
                ActionState subState = new ActionState(state.getBlock(), subMethod, 1, new StringBuilder());
                subState.getMethod().getContext().inheritContext(state.getMethod().getContext());
                definedMethod.getAction().validate(subState);
                definedMethod.getAction().process(subState);
                state.getContent().setLength(0);
                state.getContent().append(subState.getContent());
            }
            else {
                MethodAction noIteratorAction = new NoIteratorAction();
                noIteratorAction.process(state);
            }
        }
    }

}
