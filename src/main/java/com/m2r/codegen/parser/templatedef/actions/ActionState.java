package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.templatedef.Method;

public class ActionState {
    private Method method;
    private int index;
    private int size;
    private LogicalOperator logicalOperator = LogicalOperator.AND;
    private boolean logicState = true;

    private int showIfCount = 1;

    private ActionState(Method method, int index, int size) {
        this.method = method;
        this.index = index;
        this.size = size;
    }

    public static ActionState create(Method method, int index, int size) {
        return new ActionState(method, index, size);
    }

    public static ActionState create(Method method) {
        return new ActionState(method, 0, 0);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
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

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public void setLogicalOperator(LogicalOperator logicalOperator, int showIfCount) {
        this.logicalOperator = logicalOperator;
        this.showIfCount = showIfCount;
    }

    public void setLogicState(boolean logicState) {
        this.logicState = logicState;
    }

    public boolean isLogicState() {
        return logicState;
    }

    public boolean chainLogicOperation(boolean value) {
        boolean lastState = logicState;
        switch (logicalOperator) {
            case AND:
                logicState = lastState && value;
                break;
            case OR:
                logicState = lastState || value;
                break;
            case XOR:
                logicState = lastState ^ value;
                break;
            default:
                logicState = value;
        }
        showIfCount--;
        return logicState;
    }

    public void setShowIfCount(int showIfCount) {
        this.showIfCount = showIfCount;
    }

    public boolean isLastShowIf() {
        return this.showIfCount < 1;
    }

    public void updateLogicStatusFrom(ActionState state) {
        this.logicalOperator = state.logicalOperator;
        this.logicState = state.logicState;
        this.showIfCount = state.showIfCount;
    }

}
