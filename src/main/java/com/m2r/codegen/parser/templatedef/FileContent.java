package com.m2r.codegen.parser.templatedef;

import com.m2r.codegen.parser.el.ElContext;

import java.util.ArrayList;
import java.util.List;

public class FileContent {

    private List<BlockContent> blocks = new ArrayList<>();

    private ElContext context;

    public List<BlockContent> getBlocks() {
        return blocks;
    }

    public BlockContent getBlockByLine(int line) {
        return blocks.stream().filter(it -> it.getLineStart() == line).findFirst().orElse(null);
    }

    public void setBlocks(List<BlockContent> blocks) {
        this.blocks = blocks;
    }

    public void setContext(ElContext context) {
        this.context = context;
    }

    public ElContext getContext() {
        return context;
    }

}
