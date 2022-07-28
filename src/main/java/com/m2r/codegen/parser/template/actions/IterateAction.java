package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.el.ElExpr;
import com.m2r.codegen.parser.template.DefinedMethod;
import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.template.Param;
import com.m2r.codegen.parser.templatedef.BlockContent;

import java.util.List;

public class IterateAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() < 2)
            throw new RuntimeException("Iterate method required at least 2 parameters: \n" +
                    "- iterator\n- itemVar") ;
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
        validate(method);
        Param iteratorExpr = method.getParameters().get(0);
        Param itemVar = method.getParameters().get(1);

        List<?> list = (List<?>) ElExpr.stringToObject(block.getContext(), iteratorExpr.getValue());
        if (list != null) {
            method.getContext().inheritContext(block.getContext());
            for (int i=0; i<list.size(); i++) {
                Object item = list.get(i);
                method.getContext().put(itemVar.getValue(), item);
                MethodAction action = new NoIteratorAction();
                method.getContext().inheritContext(method.getContext());

                StringBuilder buffer = new StringBuilder(content.toString());

                Method delimiterMethod = method.getMethodByType(DefinedMethod.DELIMITER);
                if (delimiterMethod != null && i == 0) {
                    buffer.append(delimiterMethod.getParameter(0));
                }

                action.process(block, method, buffer);

                if (delimiterMethod != null) {
                    if (i == (list.size() - 1)) {
                        buffer.insert(buffer.length() - System.lineSeparator().length(), delimiterMethod.getParameter(2));
                    }
                    else {
                        buffer.insert(buffer.length() - System.lineSeparator().length(), delimiterMethod.getParameter(1));
                    }
                }

                content.append(buffer.toString());
            }
        }

    }

}
