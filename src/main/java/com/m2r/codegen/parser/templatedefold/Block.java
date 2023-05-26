package com.m2r.codegen.parser.templatedefold;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.templatedef.Method;
import com.m2r.codegen.parser.templatedef.Param;
import com.m2r.codegen.parser.templatedef.actions.LogicalOperator;

import java.util.List;

public class Block {

    private boolean dynamic = false;
    private int lineStart = 1;
    private int lineEnd = 1;

    private LogicalOperator logicalOperator = LogicalOperator.AND;

    private Content content = new Content();

    private Content backup;

    private ElContext context = new ElContext();

    private Method method;

    private Block(Method method) {
        this.lineStart = method.getParameter(0).getIntegerValue();
        this.lineEnd = method.getParameter(1).getIntegerValue();
        this.logicalOperator = LogicalOperator.of(method.getParameter(2, new Param(LogicalOperator.AND.name())).getValue());
        this.method = method;
        this.dynamic = method != null;
    }

    public static Block create(Method method) {
        return new Block(method);
    }

    public static Block createWithoutMethod(Method parent, Integer lineStart, Integer lineEnd, LogicalOperator logicalOperator) {
        Method m = Method.create(parent, "block");
        m.getParameters().add(new Param(lineStart.toString()));
        m.getParameters().add(new Param(lineEnd.toString()));
        m.getParameters().add(new Param(logicalOperator.name()));
        return Block.create(m);
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public int getLineStart() {
        return lineStart;
    }

    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
    }

    public int getLineEnd() {
        return lineEnd;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public void setLineEnd(int lineEnd) {
        this.lineEnd = lineEnd;
    }

    public ElContext getContext() {
        return context;
    }

    public void setContext(ElContext context) {
        this.context = context;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public List<Method> getMethods() {
        return method.getMethods();
    }

    public void backup() {
        this.backup = Content.of(this.content);
    }

    public void restore() {
        if (backup != null) {
            this.content.substitute(backup);
        }
    }

}
