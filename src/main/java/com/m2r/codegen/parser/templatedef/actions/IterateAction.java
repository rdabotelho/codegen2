package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.el.ElExpr;
import com.m2r.codegen.parser.templatedef.DefinedMethod;
import com.m2r.codegen.parser.templatedef.Method;
import com.m2r.codegen.parser.templatedef.Param;
import com.m2r.codegen.parser.templatedefold.Block;
import com.m2r.codegen.parser.templatedefold.Content;
import java.util.List;
import java.util.stream.Collectors;

public class IterateAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() < 2)
            throw new RuntimeException("Iterate method required at least 2 parameters: \n" +
                    "- collection\n- itemVar");
        boolean hasCase = method.getMethods().stream().filter(it -> it.getName().equals("case")).findFirst().orElse(null) != null;
        if (hasCase) {
            boolean hasNoCase = method.getMethods().stream().filter(it -> !it.getName().equals("case")).findFirst().orElse(null) != null;
            if (hasNoCase) {
                throw new RuntimeException("Case method cannot be used with other methods in an iterate method");
            }
        }
    }

    @Override
    public void process(ActionState state) throws Exception {
        Method method = state.getMethod();
        Block block = method.getBlock();
        ElContext context = block.getContext();
        Param iteratorExpr = method.getParameter(0);
        Param itemVar = method.getParameter(1);
        List<?> list = (List<?>) ElExpr.stringToObject(context, iteratorExpr.getValue());
        Content buffer = Content.empty();
        if (list != null) {
            Method itBlock = method.getMethods().get(0);
            itBlock.backupBlock();
            for (int i=0; i<list.size(); i++) {
                itBlock.restoreBlock();
                Object item = list.get(i);
                context.put(itemVar.getValue(), item);
                itBlock.getBlock().getContext().inheritContext(context);
                if (!showContent(itBlock)) {
                    continue;
                }
                itBlock.process(i, list.size());
                buffer.append(itBlock.getBlock().getContent());
            }
        }
        block.getContent().substitute(buffer);
    }

    private boolean showContent(Method itBlock) throws Exception {
        List<Method> showIflist = itBlock.getMethods().stream()
                .filter(it -> it.getName().equals(DefinedMethod.SHOW_IF.getName()))
                .collect(Collectors.toList());
        for (Method method : showIflist) {
            method.process();
            if (!method.getBlock().getContent().isVisible()) {
                return false;
            }
        }
        return true;
    }

}
