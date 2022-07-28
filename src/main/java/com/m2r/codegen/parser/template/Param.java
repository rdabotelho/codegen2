package com.m2r.codegen.parser.template;

import com.m2r.codegen.parser.el.ElContext;
import com.m2r.codegen.parser.el.ElExpr;

public class Param {

    private boolean expr;
    private String value;

    public Param() {
        this("");
    }

    public Param(String value) {
        this.value = value;
        this.expr = false;
    }

    public Param(String value, boolean isExpr) {
        this.value = value;
        this.expr = isExpr;
    }

    public Boolean resolveValueToBoolean(ElContext context, Boolean def) throws Exception {
        return Boolean.valueOf(resolveValue(context, def).toString());
    }

    public String resolveValueToString(ElContext context, String def) throws Exception {
        return resolveValue(context, def).toString();
    }

    public Object resolveValue(ElContext context, Object def) throws Exception {
        return this.isExpr() ? ElExpr.stringToObject(context, this.getValue(), def) : this.getValue();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
