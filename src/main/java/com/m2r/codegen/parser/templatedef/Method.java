package com.m2r.codegen.parser.templatedef;

import com.m2r.codegen.parser.templatedef.actions.ActionState;
import com.m2r.codegen.parser.templatedef.actions.LogicalOperator;
import com.m2r.codegen.parser.templatedefold.Block;
import java.util.ArrayList;
import java.util.List;

public class Method {

    private Method parent;
    private String name;
    private List<Param> parameters = new ArrayList<>();
    private List<Method> methods = new ArrayList<>();
    private int pos;

    private boolean logicState = true;

    private Method(Method parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public static Method create(Method parent, String name) {
        return new Method(parent, name);
    }

    public static Method createRoot() {
        Method method = Method.create(null, "block");
        method.getParameters().add(new Param("1"));
        method.getParameters().add(new Param("0"));
        return method;
    }

    public static Method createBorder(Method parent, Integer startLine, Integer endLine, LogicalOperator logicalOperator) {
        Method method = Method.create(parent, "block");
        method.getParameters().add(new Param(startLine.toString()));
        method.getParameters().add(new Param(endLine.toString()));
        method.getParameters().add(new Param(logicalOperator.name()));
        method.setBlock(Block.create(method));
        return method;
    }

    private Block block;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Param> getParameters() {
        return parameters;
    }

    public void setParameters(List<Param> parameters) {
        this.parameters = parameters;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setLogicState(boolean logicState) {
        this.logicState = logicState;
    }

    public boolean isLogicState() {
        return logicState;
    }

    public Param getParameter(int index) {
        return parameters.size() > index ? parameters.get(index) : null;
    }

    public Param getParameter(int index, Param def) {
        return parameters.size() > index ? parameters.get(index) : def;
    }

    public Method getParent() {
        return parent;
    }

    public void setParent(Method parent) {
        this.parent = parent;
    }

    public boolean isBlock() {
        return this.name.equals("block");
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        if (block != null) {
            return block;
        }
        return parent != null ? parent.getBlock() : null;
    }

    public void process() throws Exception {
        this.process(0, 0);
    }

    public void process(int index, int size) throws Exception {
        DefinedMethod definedMethod = DefinedMethod.findDefinedMethod(name);
        if (definedMethod == null) {
            throw new RuntimeException("Method '" + name + "' undefined!");
        }
        ActionState newState = ActionState.create(this, index, size);
        definedMethod.getAction().process(newState);
    }

    public void process(ActionState state) throws Exception {
        DefinedMethod definedMethod = DefinedMethod.findDefinedMethod(name);
        if (definedMethod == null) {
            throw new RuntimeException("Method '" + name + "' undefined!");
        }
        ActionState newState = ActionState.create(this, state.getIndex(), state.getSize());
        definedMethod.getAction().process(newState);
    }

    public void backupBlock() {
        Block block = this.getBlock();
        if (block !=null) {
            block.backup();
        }
    }

    public void restoreBlock() {
        Block block = this.getBlock();
        if (block !=null) {
            block.restore();
        }
    }

}
