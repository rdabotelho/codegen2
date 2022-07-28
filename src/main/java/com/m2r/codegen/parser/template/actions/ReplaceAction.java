package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.templatedef.BlockContent;

public class ReplaceAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() != 2)
            throw new RuntimeException("Replace method required 4 parameters:\n" +
                    "- oldText\n- newText");
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
        validate(method);
        String oldText = method.getParameters().get(0).resolveValueToString(method.getContext(), "");
        String newText = method.getParameters().get(1).resolveValueToString(method.getContext(), "");
        String line = content.toString();
        content.setLength(0);
        content.append(line.replaceAll(oldText, newText));
    }

}
