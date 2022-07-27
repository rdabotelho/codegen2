package com.m2r.codegencli.parser.template.actions;

import com.m2r.codegencli.parser.el.ElExpr;
import com.m2r.codegencli.parser.template.Method;
import com.m2r.codegencli.parser.templatedef.BlockContent;

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
        String oldText = method.getParameters().get(0);
        String newText = ElExpr.stringToObject(method.getContext(), method.getParameters().get(1), "").toString();
        String line = content.toString();
        content.setLength(0);
        content.append(line.replaceAll(oldText, newText));
    }

}
