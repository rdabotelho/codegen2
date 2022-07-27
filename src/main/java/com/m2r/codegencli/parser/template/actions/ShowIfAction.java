package com.m2r.codegencli.parser.template.actions;

import com.m2r.codegencli.parser.template.Method;
import com.m2r.codegencli.parser.templatedef.BlockContent;

public class ShowIfAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() < 3)
            throw new RuntimeException("Iterate method required at least 1 parameter: \n" +
                    "- method\n- attribute\n- value") ;
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
    }

}
