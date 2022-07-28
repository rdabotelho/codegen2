package com.m2r.codegen.parser.modeling;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParamValue {

    private boolean isArray = true;
    private List<StringWrapper> values = new ArrayList<>();

    public ParamValue(boolean isArray) {
        this.isArray = isArray;
    }

    public ParamValue(StringWrapper value) {
        this.isArray = false;
        setValue(value);
    }

    public List<StringWrapper> getValues() {
        return values;
    }

    public void setValues(List<StringWrapper> values) {
        this.values = values;
    }

    public void setValue(StringWrapper value) {
        values.clear();
        values.add(value);
    }

    public StringWrapper getValue() {
        return getValue("");
    }

    public StringWrapper getValue(String def) {
        if (!isArray) {
            if (values.size() == 1) {
                return values.get(0);
            }
            else {
                return StringWrapper.of(def);
            }
        }
        else {
            return StringWrapper.of(values.stream().map(it -> it.toString()).collect(Collectors.joining(",")));
        }
    }

    public boolean hasValue(String value) {
        for (StringWrapper str : values) {
            if (str.getValue() != null && str.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return getValue().toString();
    }

}
