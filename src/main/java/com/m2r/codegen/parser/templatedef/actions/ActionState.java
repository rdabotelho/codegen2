package com.m2r.codegen.parser.templatedef.actions;

import com.m2r.codegen.parser.templatedef.Method;

public class ActionState {
    private Method method;
    private int index;
    private int size;

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

}
