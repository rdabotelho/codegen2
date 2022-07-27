package com.m2r.codegencli.parser.template.actions;

import com.m2r.codegencli.parser.el.ElExpr;
import com.m2r.codegencli.parser.template.Method;
import com.m2r.codegencli.parser.templatedef.BlockContent;

public class ReplaceIfAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() != 4)
            throw new RuntimeException("Replace method required 4 parameters:\n" +
                    "- oldText\n- newText\n- method\n- value");
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
        validate(method);
        String oldText = method.getParameters().get(0);
        String newText = ElExpr.stringToObject(method.getContext(), method.getParameters().get(1), "").toString();

        String valueParam = method.getParameters().get(3);
        method.getContext().put("param1", valueParam);
        String methodParam = ElExpr.stringToObject(method.getContext(), method.getParameters().get(2), "").toString();

        boolean show = Boolean.valueOf(ElExpr.stringToObject(method.getContext(), methodParam, "false").toString());

        if (show) {
            String line = content.toString();
            content.setLength(0);
            content.append(line.replaceAll(oldText, newText));
        }
    }

}
