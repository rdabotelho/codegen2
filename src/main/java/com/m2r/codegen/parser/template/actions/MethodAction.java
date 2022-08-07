package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.DefinedMethod;
import com.m2r.codegen.parser.template.Method;

public interface MethodAction {

    default void processChildrenMethods(ActionState state, int level, int index, int size) throws Exception {
        for (Method childMethod : state.getMethod().getMethods()) {
            DefinedMethod definedMethod = DefinedMethod.findDefinedMethod(childMethod.getName());
            if (definedMethod == null) {
                throw new RuntimeException("Method '" + childMethod.getName() + "' undefined!");
            }
            ActionState subState = new ActionState(state.getBlock(), childMethod, level, state.getContent(), index, size);
            subState.getMethod().getContext().inheritContext(state.getMethod().getContext());
            definedMethod.getAction().validate(subState);
            definedMethod.getAction().process(subState);
            state.getContent().setLength(0);
            state.getContent().append(subState.getContent());
        }

    }

    void validate(ActionState state) throws RuntimeException;

    void process(ActionState state) throws Exception;

}
