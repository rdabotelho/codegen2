package com.m2r.codegen.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertiesManager {

    private List<Property> properties = new ArrayList<>();

    public void  load(File file) throws Exception {
        if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
            Yaml yaml = new Yaml();
            FileInputStream inputStream = new FileInputStream(file);
            Map<String, Object> prop = yaml.load(inputStream);
            properties.clear();
            prop.entrySet().stream().forEach(it -> {
                Map<String, Object> map = (Map<String,Object>) it.getValue();
                String name = it.getKey();
                String label = (String) map.get("label");
                String value = (String) map.get("value");
                Boolean required = (Boolean) map.get("required");
                String regex = (String) map.get("regex");
                Property property = new Property(name, value, label, required != null ? required : false);
                property.setRegex(regex);
                properties.add(property);
            });
        }
        else {
            java.util.Properties prop = new java.util.Properties();
            prop.load(new FileInputStream(file));
            properties = prop.entrySet().stream()
                    .map(it -> new Property((String) it.getKey(), (String) it.getValue(), (String) it.getKey(), true))
                    .collect(Collectors.toList());
        }
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void store() throws IOException {
        java.util.Properties prop = new java.util.Properties();
        properties.forEach(it -> prop.put(it.getName(), it.getValue()));
        prop.store(new FileOutputStream(DirFileUtils.getConfigProperties()), null);
    }

}
