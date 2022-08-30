package com.m2r.codegen.parser.templatedefold;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.templatedef.Method;

import java.util.ArrayList;
import java.util.List;

public class FileContent {

    private List<Method> methods = new ArrayList<>();

    private List<Block> blocks = new ArrayList<>();

    private ElContext context;

    public List<Block> getBlocks() {
        return blocks;
    }

    public Block getBlockByLine(int line) {
        return blocks.stream().filter(it -> it.getLineStart() == line).findFirst().orElse(null);
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public void setContext(ElContext context) {
        this.context = context;
    }

    public ElContext getContext() {
        return context;
    }

}
