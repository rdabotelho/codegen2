package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.templatedef.Method;
import com.m2r.codegen.parser.templatedefold.Block;
import java.util.List;
import java.util.stream.Collectors;

public class BlockAction implements MethodAction {

    @Override
    public void validate(Method method) throws RuntimeException {
        if (method.getParameters().size() < 3)
            throw new RuntimeException("Block method required 2 or 3 parameter: \n" +
                    "- startLine\n- endLine\n- logicalOperator (default AND)") ;
    }

    @Override
    public void process(ActionState state) throws Exception {
        Method method = state.getMethod();
        method.getMethods().stream().filter(it -> it.isBlock()).forEach(it -> {
            it.restoreBlock();
            it.backupBlock();
        });
        for (Method subMethod : method.getMethods()) {
            if (subMethod.isBlock()) {
                subMethod.getBlock().getContext().inheritContext(method.getBlock().getContext());
            }
            subMethod.process(state);
        }
        List<Block> blocks = method.getMethods().stream()
                .filter(it -> it.isBlock())
                .map(it -> it.getBlock())
                .collect(Collectors.toList());
        if (!blocks.isEmpty()) {
            method.getBlock().getContent().clean();
            blocks.forEach(it -> method.getBlock().getContent().append(it.getContent().toString()));
        }
    }

}
