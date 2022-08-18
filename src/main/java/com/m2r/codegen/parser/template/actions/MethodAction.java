package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.DefinedMethod;
import com.m2r.codegen.parser.template.Method;

public interface MethodAction {

    default void processChildrenMethods(ActionState state, int level, int index, int size) throws Exception {
        boolean hasCase = false;
        if (state.getMethod().getName().equals("iterate")) {
            hasCase = state.getMethod().getMethods().stream().filter(it -> it.getName().equals("case")).findFirst().orElse(null) != null;
        }

        StringBuilder caseBuffer = new StringBuilder();
        for (Method childMethod : state.getMethod().getMethods()) {
            DefinedMethod definedMethod = DefinedMethod.findDefinedMethod(childMethod.getName());
            if (definedMethod == null) {
                throw new RuntimeException("Method '" + childMethod.getName() + "' undefined!");
            }
            ActionState subState = new ActionState(state.getBlock(), childMethod, level, state.getContent(), index, size);
            subState.getMethod().getContext().inheritContext(state.getMethod().getContext());
            definedMethod.getAction().validate(subState);
            definedMethod.getAction().process(subState);

            state.setShow(subState.isShow());
            if (hasCase) {
                caseBuffer.append(subState.getContent());
            }
            else {
                state.getContent().setLength(0);
                state.getContent().append(subState.getContent());
            }
        }

        if (hasCase) {
            state.getContent().setLength(0);
            state.getContent().append(caseBuffer.toString());
        }
    }

    void validate(ActionState state) throws RuntimeException;

    void process(ActionState state) throws Exception;

}
