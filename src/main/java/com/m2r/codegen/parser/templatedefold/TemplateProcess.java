package com.m2r.codegen.parser.templatedefold;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.templatedef.Method;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateProcess {

    private Method root;
    private ElContext context = new ElContext();

    public ElContext getContext() {
        return context;
    }

    public void setContext(ElContext context) {
        this.context = context;
    }

    public Method getRoot() {
        return root;
    }

    public void setRoot(Method root) {
        this.root = root;
    }

    public void process(Writer writer) throws Exception {
        root.getBlock().getContext().inheritContext(context);
        root.process();
        List<Method> methods = root.getMethods().stream().filter(it -> it.isBlock()).collect(Collectors.toList());
        for (Method method : methods) {
            writer.write(method.getBlock().getContent().toString());
        }
    }

}
