package com.m2r.codegen.parser.template;

import com.m2r.codegen.parser.el.ElContext;

import java.util.ArrayList;
import java.util.List;

public class Method {

    private String name;
    private List<Param> parameters = new ArrayList<>();
    private List<Method> methods = new ArrayList<>();
    private ElContext context = new ElContext();

    private int pos;

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

    public ElContext getContext() {
        return context;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Param getParameter(int index) {
        return parameters.size() > index ? parameters.get(index) : null;
    }

    public Param getParameter(int index, Param def) {
        return parameters.size() > index ? parameters.get(index) : def;
    }

    public Method getMethodByType(DefinedMethod type) {
        return methods.stream().filter(it -> it.getName().equals(type.getName())).findFirst().orElse(null);
    }

    public String getMethodFlagParameter(DefinedMethod definedMethod, int index) {
        Method method = this.getMethodByType(definedMethod);
        if (method != null) {
            definedMethod.getAction().validate(method);
            return method.getParameter(index).getValue();
        }
        return null;
    }

}
