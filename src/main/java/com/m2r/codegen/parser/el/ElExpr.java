package com.m2r.codegen.parser.el;

import com.m2r.codegen.parser.modeling.StringWrapper;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ElExpr {

    public static String resolve(ElContext context, String expr) {
        Reader reader = new StringReader(expr);
        Writer writer = new StringWriter();
        try {
            ElParser.parse(reader, writer, (id) -> {
                try {
                    return stringToObject(context, id, "").toString();
                } catch (Exception e) {
                    return "";
                }
            });
            return writer.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object stringToObject(ElContext context, String exp, Object def) throws Exception {
        Object result = stringToObject(context, exp);
        return result != null ? result : def;
    }

    public static Object stringToObject(ElContext context, String exp) throws Exception {
        String[] path = exp.split("\\.");
        Object parent = null;
        for (String attribute : path) {
            if (parent == null) {
                parent = context.get(attribute);
                if (parent == null) {
                    return null;
                }
                continue;
            }
            parent = getValue(parent, attribute, context);
            if (parent == null) {
                return null;
            }
        }
        return parent;
    }

    private static Object getValue(Object parent, String attributeName, ElContext context) throws Exception {
        StringWrapper an = StringWrapper.of(attributeName);
        Method[] methods =  parent.getClass().getDeclaredMethods();
        Method method = Arrays.stream(methods)
                .filter(it -> {
                    return it.getName().equals(String.format("get%s", an.toPascalCase())) ||
                            it.getName().equals(String.format("is%s", an.toPascalCase())) ||
                            it.getName().equals(String.format("to%s", an.toPascalCase()));
                })
                .findFirst()
                .orElse(null);
        return method != null ? method.invoke(parent) : resolveAlternativeMethod(parent, attributeName, context);
    }

    private static Object resolveAlternativeMethod(Object parent, String attributeName, ElContext context) throws Exception {
        Object result = null;
        switch (attributeName) {
            case "contains":
                result = contains(parent, attributeName, context);
                break;
            case "noContains":
                result = !contains(parent, attributeName, context);
                break;
            case "equals":
                result = equals(parent, attributeName, context);
                break;
            case "notEquals":
                result = !equals(parent, attributeName, context);
        }

        if (result == null) {
            Method getParamMethod = parent.getClass().getDeclaredMethod("getParam", String.class);
            if (getParamMethod != null) {
                return getParamMethod.invoke(parent, attributeName);
            }
        }

        return result;
    }

    private static Boolean contains(Object parent, String attributeName, ElContext context) throws Exception {
        Object param1 = context.get("param1");
        Object param2 = context.get("param2");
        if (param1 == null || param2 == null) {
            return null;
        }
        List<?> list = (List<?>) parent;
        for (Object item : list) {
            Object attributeValue = getValue(item, param1.toString(), context);
            if (attributeValue == null) {
                attributeValue = "";
            }
            if (attributeValue.toString().equals(param2)) {
                return true;
            }
        }
        return false;
    }

    private static Boolean equals(Object parent, String attributeName, ElContext context) throws Exception {
        Object param1 = context.get("param1");
        if (parent == null || param1 == null) {
            return null;
        }
        return parent.toString().equals(param1);
    }

}
