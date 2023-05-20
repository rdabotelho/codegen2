package com.m2r.codegen.parser.modeling;

import java.util.HashMap;
import java.util.Map;

public class DomainAttribute {

    private Domain parent;
    private StringWrapper type;
    private StringWrapper name;
    private Map<String, ParamValue> params = new HashMap<>();
    private Domain typeDomain;

    public DomainAttribute(Domain parent) {
        this.parent = parent;
    }

    public StringWrapper getType() {
        return type;
    }

    public void setType(StringWrapper type) {
        this.type = type;
    }

    public StringWrapper getName() {
        return name;
    }

    public void setName(StringWrapper name) {
        this.name = name;
    }

    public Map<String, ParamValue> getParams() {
        return params;
    }

    public void setParams(Map<String, ParamValue> params) {
        this.params = params;
    }

    public boolean hasParam(String key) {
        return getParams().get(key) != null;
    }

    public ParamValue getParam(String key) {
        return getParam(key, false);
    }

    public ParamValue getParam(String key, Boolean isArray) {
        ParamValue str = getParams().get(key);
        return str == null ? new ParamValue(isArray) : str;
    }

    public boolean isBasic() {
        return BasicType.isValid(type.toString());
    }

    public boolean isEntity() {
        return hasTypeDomain() && !getTypeDomain().isEnum() && !isList();
    }

    public boolean isList() {
        return type.toString().startsWith("List<");
    }

    public boolean isEnum() {
        return hasTypeDomain() && getTypeDomain().isEnum();
    }

    public boolean isOneToOne() {
        ParamValue param = getParam("manyToOne");
        return hasTypeDomain() && !isEnum() && !isList() && !(param != null && param.toString().equals("true"));
    }

    public boolean isManyToOne() {
        ParamValue param = getParam("manyToOne");
        return hasTypeDomain() && !isList() && param != null && param.toString().equals("true");
    }

    public boolean isOneToMany() {
        ParamValue param = getParam("manyToMany");
        return isList() && !(param != null && param.toString().equals("true"));
    }

    public boolean isManyToMany() {
        ParamValue param = getParam("manyToMany");
        return isList() && param != null && param.toString().equals("true");
    }

    public StringWrapper getItemType() {
        if (isList()) {
            String value = type.toString();
            value = value.substring(5,value.length()-1);
            return StringWrapper.of(value);
        }
        return StringWrapper.of("");
    }

    public Domain getParent() {
        return parent;
    }

    public void setParent(Domain parent) {
        this.parent = parent;
    }

    public Domain getTypeDomain() {
        return typeDomain;
    }

    public void setTypeDomain(Domain typeDomain) {
        this.typeDomain = typeDomain;
    }

    public boolean hasTypeDomain() {
        return typeDomain != null;
    }
    @Override
    public String toString() {
        return "DomainAttribute{" +
                "type=" + type +
                ", name=" + name +
                '}';
    }

}
