package com.m2r.codegen.parser.templatedef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateDef {

    private List<Attribute> attributes = new ArrayList<>();
    private List<Method> methods = new ArrayList<>();

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public Method findMethodByParam(int index, String value) {
        return getMethods().stream().filter(it -> it.getParameter(1, new Param()).equals(value)).findFirst().orElse(null);
    }

    public Attribute getAttributeByName(String name) {
        return attributes.stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean scope(String type) {
        Attribute scopeAttr = getAttributeByName("scope");
        if (scopeAttr == null) {
            return true;
        }
        String[] types = scopeAttr.getValue().split(",");
        return Arrays.stream(types)
                .filter(it -> it.trim().equals(type))
                .findFirst()
                .orElse(null) != null;
    }

    public List<Method> getBlockMethods() {
        return this.methods.stream().filter(it -> it.isBlock()).collect(Collectors.toList());
    }

}
