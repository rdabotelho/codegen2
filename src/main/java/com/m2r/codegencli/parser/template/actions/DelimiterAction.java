package com.m2r.codegencli.parser.template.actions;

import com.m2r.codegencli.parser.template.DefinedMethod;
import com.m2r.codegencli.parser.template.Method;
import com.m2r.codegencli.parser.templatedef.BlockContent;

public class DelimiterAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() < 3)
            throw new RuntimeException("Iterate method required 3 parameters: \n" +
                    "- prefix\n- divider\n- suffix") ;
    }

    @Override
    public void process(BlockContent block, Method method, StringBuilder content) throws Exception {
    }

}
