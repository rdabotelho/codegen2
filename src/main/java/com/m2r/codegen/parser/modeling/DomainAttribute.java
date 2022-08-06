package com.m2r.codegen.parser.modeling;

import java.util.HashMap;
import java.util.Map;
public class DomainAttribute {

    private Domain parent;
    private StringWrapper type;
    private StringWrapper name;
    private Map<String, ParamValue> params = new HashMap<>();
    private boolean first = false;
    private boolean last = false;
    private Domain typeDomain;
    private boolean main = false;
    private String mappedBy;

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

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isFirst() {
        return first;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isLast() {
        return last;
    }

    public String ifFirst(String text, String def) {
        return isFirst() ? text : def;
    }

    public String ifLast(String text, String def) {
        return isLast() ? text : def;
    }

    public String ifParam(String name, String value, String text, String def) {
        ParamValue param = getParam(name);
        return param != null && param.getValue().toString().equals(value) ? text :  def;
    }

    public boolean isString() {
        return "String".equalsIgnoreCase(type.toString());
    }

    public boolean isInteger() {
        return "Integer".equalsIgnoreCase(type.toString());
    }

    public boolean isLong() {
        return "Long".equalsIgnoreCase(type.toString());
    }

    public boolean isDouble() {
        return "Double".equalsIgnoreCase(type.toString());
    }

    public boolean isFloat() {
        return "Float".equalsIgnoreCase(type.toString());
    }

    public boolean isBigDecimal() {
        return "BigDecimal".equalsIgnoreCase(type.toString());
    }

    public boolean isLocalDate() {
        return "LocalDate".equalsIgnoreCase(type.toString());
    }

    public boolean isLocalDateTime() {
        return "LocalDateTime".equalsIgnoreCase(type.toString());
    }

    public boolean isBoolean() {
        return "Boolean".equalsIgnoreCase(type.toString());
    }

    public boolean isList() {
        return type.toString().startsWith("List<");
    }

    public boolean isMap() {
        return type.toString().startsWith("Map<");
    }

    public boolean isEnum() {
        return hasTypeDomain() && getTypeDomain().isEnum();
    }

    public boolean isEntity() {
        return hasTypeDomain() && !getTypeDomain().isEnum() && !isList();
    }

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
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

    public boolean isBasicType() {
        return isString() || isNumber() || isInteger() || isLong() || isFloat() || isDouble() || isBoolean() ||
                isBigDecimal() || isLocalDate() || isLocalDateTime() || isDate() || isDateTime();
    }

    public boolean isNumber() {
        return "Number".equalsIgnoreCase(type.toString());
    }

    public boolean isDate() {
        return "Date".equalsIgnoreCase(type.toString());
    }

    public boolean isDateTime() {
        return "DateTime".equalsIgnoreCase(type.toString());
    }

    /* Tipo de entradas conforme o tipo de atributo */

    public boolean isBooleanInput() {
        return isBoolean();
    }

    public boolean isIntegerInput() {
        return isInteger() || isLong();
    }

    public boolean isDoubleInput() {
        return isFloat() || isDouble() || isBigDecimal();
    }

    public boolean isDateTimeInput() {
        return isLocalDateTime();
    }

    public boolean isDateInput() {
        return isLocalDate();
    }

    public boolean isStringInput() {
        return isString();
    }

    public boolean isEntityInput() {
        return hasTypeDomain() && getTypeDomain().isClass();
    }

    public boolean isEnumInput() {
        return hasTypeDomain() && getTypeDomain().isEnum();
    }

    public boolean isListInput() {
        return isList();
    }

    /* Atributos padrao do dominio */

    public StringWrapper getLabel() {
        return getParam("label").getValue();
    }

    public String ifLength(String text, String def) {
        return hasParam("length") ? text : def;
    }

    public StringWrapper getLength() {
        return getParam("length").getValue();
    }

    public StringWrapper getTransient() {
        return getParam("transient").getValue("false");
    }

    public boolean isTransient() {
        return getTransient().toString().equalsIgnoreCase("true");
    }

    public String ifTransient(String text, String def) {
        return isTransient() ? text : def;
    }

    public StringWrapper getComposition() {
        return getParam("composition").getValue("false");
    }

    public boolean isComposition() {
        return getComposition().toString().equalsIgnoreCase("true");
    }

    public String ifComposition(String text, String def) {
        return isComposition() ? text : def;
    }

    public StringWrapper getMask() {
        return getParam("mask").getValue("");
    }

    public boolean hasMask() {
        return !getMask().toString().equals("");
    }

    public String ifMask(String text, String def) {
        return hasMask() ? text : def;
    }

    public StringWrapper getManyToMany() {
        return getParam("manyToMany").getValue("false");
    }

    public boolean isManyToMany() {
        return getManyToMany().toString().equalsIgnoreCase("true");
    }

    public String ifManyToMany(String text, String def) {
        return isManyToMany() ? text : def;
    }

    public StringWrapper getManyToOne() {
        return getParam("manyToOne").getValue("false");
    }

    public boolean isManyToOne() {
        return getManyToOne().toString().equalsIgnoreCase("true");
    }

    public String ifManyToOne(String text, String def) {
        return isManyToOne() ? text : def;
    }

    public StringWrapper getSection() {
        return getParam("section").getValue("0");
    }

    public StringWrapper getInput() {
        StringWrapper input = getParam("input").getValue("");
        if (input.getValue().equals("")) {
            if (isListInput() || isEntity()) {
                input.setValue("select-eager");
            }
            else if (isEnum()) {
                input.setValue("select-dropdown");
            }
        }
        return input;
    }

    public StringWrapper getMultiple() {
        return getParam("multiple").getValue("false");
    }

    public StringWrapper getId() {
        return getParam("id").getValue();
    }

    public StringWrapper getAttributeName() {
        return getParam("name").getValue();
    }

    public StringWrapper getDescription() {
        return getParam("description").getValue();
    }

    public boolean isStandardInput() {
        return getInput().getValue().equalsIgnoreCase("standard");
    }

    public boolean isTextareaInput() {
        return getInput().getValue().equalsIgnoreCase("textarea");
    }

    public boolean isSelectDropdownInput() {
        return getInput().getValue().equalsIgnoreCase("select-dropdown");
    }

    public boolean isSelectEagerInput() {
        return getInput().getValue().equalsIgnoreCase("select-eager");
    }

    public boolean isSelectLazyInput() {
        return getInput().getValue().equalsIgnoreCase("select-lazy");
    }

    public boolean isMultiple() {
        return getMultiple().equals(isList() ? "true" : "false");
    }

    @Override
    public String toString() {
        return "DomainAttribute{" +
                "type=" + type +
                ", name=" + name +
                '}';
    }

}
