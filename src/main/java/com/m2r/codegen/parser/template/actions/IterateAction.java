package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.el.ElExpr;
import com.m2r.codegen.parser.template.Param;
import java.util.List;

public class IterateAction implements MethodAction {

    @Override
    public void validate(ActionState state) throws RuntimeException {
        if (state.getMethod().getParameters().size() < 2)
            throw new RuntimeException("Iterate method required at least 2 parameters: \n" +
                    "- iterator\n- itemVar") ;
    }

    @Override
    public void process(ActionState state) throws Exception {
        Param iteratorExpr = state.getMethod().getParameter(0);
        Param itemVar = state.getMethod().getParameter(1);
        List<?> list = (List<?>) ElExpr.stringToObject(state.getMethod().getContext(), iteratorExpr.getValue());
        if (list != null) {
            for (int i=0; i<list.size(); i++) {
                Object item = list.get(i);
                MethodAction noIteratorAction = new NoIteratorAction();
                ActionState subState = new ActionState(state.getBlock(), state.getMethod(), 2, state.getBlock().getContent(), i, list.size());
                subState.getMethod().getContext().inheritContext(state.getMethod().getContext());
                subState.getMethod().getContext().put(itemVar.getValue(), item);
                noIteratorAction.process(subState);
                state.getContent().append(subState.getContent().toString());
            }
        }

    }

}
