package com.m2r.codegen.parser.modeling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Domain {

    private DomainList parent;
    private StringWrapper type;
    private StringWrapper name;
    private List<DomainAttribute> attributes = new ArrayList<>();
    private Map<String, ParamValue> params = new HashMap<>();
    private Domain compositionOwner;

    public Domain(DomainList parent) {
        this.parent = parent;
    }

    public DomainAttribute addAttribute(String type, String name) {
        DomainAttribute da = new DomainAttribute(this);
        da.setType(StringWrapper.of(type));
        da.setName(StringWrapper.of(name));
        this.getAttributes().add(da);
        return da;
    }

    public boolean hasParam(String key) {
        return getParams().get(key) != null;
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

    public List<DomainAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DomainAttribute> attributes) {
        this.attributes = attributes;
    }

    public Map<String, ParamValue> getParams() {
        return params;
    }

    public String ifParam(String name, String value, String text, String def) {
        ParamValue param = getParam(name);
        return param != null && param.getValue().toString().equals(value) ? text :  def;
    }

    public void setParams(Map<String, ParamValue> params) {
        this.params = params;
    }

    public Domain getCompositionOwner() {
        return compositionOwner;
    }

    public void setCompositionOwner(Domain compositionOwner) {
        this.compositionOwner = compositionOwner;
    }

    public boolean hasCompositionOwner() {
        return getCompositionOwner() != null;
    }

    public ParamValue getParam(String key) {
        return getParam(key, false);
    }

    public ParamValue getParam(String key, boolean isArray) {
        ParamValue str = getParams().get(key);
        return str == null ? new ParamValue(isArray) : str;
    }

    public List<StringWrapper> getGenerations() {
        return getParam("generations", true).getValues();
    }

    public StringWrapper getFormName() {
        ParamValue param = this.getParam("formName");
        return param != null ? param.getValue() : getName();
    }

    public boolean isEnum() {
        return "enum".equals(getType().toString());
    }

    public boolean isClass() {
        return "class".equals(getType().toString());
    }

    public void finallyProcess() {
        if (attributes.size() > 0) {
            attributes.get(0).setFirst(true);
            attributes.get(attributes.size() - 1).setLast(true);
        }
    }

    public DomainAttribute getAttributeByName(String name) {
        for (DomainAttribute attr : attributes) {
            if (attr.getName().equals(name)) {
                return attr;
            }
        }
        return null;
    }

    public void setParent(DomainList parent) {
        this.parent = parent;
    }

    public DomainList getParent() {
        return parent;
    }

    /* Atributos padrao do dominio */

    public StringWrapper getLabel() {
        return this.getParam("label").getValue();
    }

    public StringWrapper getAttrTitle() {
        StringWrapper def = StringWrapper.of("");
        if (getAttributes().size() > 0) def = getAttributes().get(0).getName();
        return this.getParam("attrTitle").getValue(def.toString());
    }

    public DomainAttribute getAttrTitleValue() {
        return  getAttributeByName(getAttrTitle().toString());
    }

    public StringWrapper getAttrSubtitle() {
        return this.getParam("attrSubtitle").getValue();
    }

    public DomainAttribute getAttrSubtitleValue() {
        return  getAttributeByName(getAttrSubtitle().toString());
    }

    public List<StringWrapper> getSections() {
        return this.getParam("sections", true).getValues();
    }

    public List<StringWrapper> getSearches() {
        List<StringWrapper> result = this.getParam("searches", true).getValues();
        if (result.isEmpty()) result.add(getAttrTitle());
        return result;
    }

    public List<StringWrapper> getFilteres() {
        List<StringWrapper> result = this.getParam("filteres", true).getValues();
        if (result.isEmpty()) result.add(getAttrTitle());
        return result;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "type=" + type +
                ", name=" + name +
                '}';
    }

}
