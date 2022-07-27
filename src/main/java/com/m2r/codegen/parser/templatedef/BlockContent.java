package com.m2r.codegen.parser.templatedef;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.template.Method;

public class BlockContent {

    private boolean dynamic = false;
    private int lineStart = 1;
    private int lineEnd = 1;
    private StringBuilder content = new StringBuilder();

    private ElContext context = new ElContext();

    private Method method;

    public BlockContent() {
    }

    public BlockContent(int lineStart, int lineEnd, Method method) {
        this.dynamic = dynamic;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.method = method;
        this.dynamic = method != null;
    }

    public StringBuilder getContent() {
        return content;
    }

    public void setContent(StringBuilder content) {
        this.content = content;
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

}
