package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.DefinedMethod;
import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.template.Param;
import com.m2r.codegen.parser.templatedef.BlockContent;

public class ReplaceIfAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() < 4)
            throw new RuntimeException("ReplaceIf method required at least 4 parameters:\n" +
                    "- oldText\n- newText\n- method\n- value\n- elseValue (optional)");
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
        validate(method);

        String mask = method.getMethodFlagParameter(DefinedMethod.MASK, 0);

        String oldText = method.getParameters().get(0).resolveValueToString(method.getContext(),"");
        String newText = method.getParameters().get(1).resolveValueToString(method.getContext(),"", mask);
        Param methodParam = method.getParameters().get(2);
        Param valueParam = method.getParameters().get(3);
        Param elseValue = method.getParameter(4);

        method.getContext().put("param1", valueParam.getValue());

        boolean show = methodParam.resolveValueToBoolean(method.getContext(), false);

        if (show) {
            String line = content.toString();
            content.setLength(0);
            content.append(line.replaceAll(oldText, newText));
        }
        else if (elseValue != null) {
            String line = content.toString();
            content.setLength(0);
            content.append(line.replaceAll(oldText, elseValue.resolveValueToString(method.getContext(),"")));
        }
    }

}
