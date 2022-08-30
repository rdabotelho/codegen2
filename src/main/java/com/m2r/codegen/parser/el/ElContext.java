package com.m2r.codegen.parser.el;

import com.m2r.codegen.utils.DirFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

    public void loadFromPropertiesFile() throws Exception {
        File configFile = new File(DirFileUtils.getCodegenDir(), "config.properties");
        Properties configProperties = new Properties();
        configProperties.load(new FileInputStream(configFile));
        configProperties.forEach((key, value) -> context.put(key.toString(), value));
    }

}
