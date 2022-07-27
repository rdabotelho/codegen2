package com.m2r.codegencli.parser.el;

import com.m2r.codegencli.parser.script.Domain;

import java.util.HashMap;
import java.util.Map;

public class ElContext {

    private Map<String,Object> context = new HashMap<>();

    public void put(String name, Object object) {
        this.context.put(name, object);
    }

    public Object get(String name) {
        return this.context.get(name);
    }

    public void inheritContext(ElContext parent) {
        context.putAll(parent.context);
    }

}
