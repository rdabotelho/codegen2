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

    public ParamValue getParam(String key, boolean isArray) {
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
        return hasTypeDomain() && !isEnum() && !isList() && !existParamEquals("manyToOne", "true");
    }

    public boolean isOneToMany() {
        return isList() && !existParamEquals("manyToMany", "true");
    }

    public boolean isManyToOne() {
        return hasTypeDomain() && !isList() && existParamEquals("manyToOne", "true");
    }

    public boolean isManyToMany() {
        return isList() && existParamEquals("manyToMany", "true");
    }

    private boolean existParamEquals(String paramName, String value) {
        ParamValue param = getParam(paramName);
        return param != null && param.toString().equals(value);
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
