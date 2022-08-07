package com.m2r.codegen.parser.template.actions;

import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.templatedef.BlockContent;

public class ActionState {

    private BlockContent block;
    private Method method;
    private StringBuilder content;
    private int index;
    private int size;
    private int level;

    public ActionState(BlockContent blockContent, Method method, int level, StringBuilder content) {
        this(blockContent, method, level, content, 0, 0);
    }

    public ActionState(BlockContent blockContent, Method method, int level, StringBuilder content, int index, int size) {
        this.block = blockContent;
        this.method = method;
        this.level = level;
        this.content = new StringBuilder(content.toString());
        this.index = index;
        this.size = size;
    }

    public BlockContent getBlock() {
        return block;
    }

    public void setBlock(BlockContent block) {
        this.block = block;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public StringBuilder getContent() {
        return content;
    }

    public void setContent(StringBuilder content) {
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
