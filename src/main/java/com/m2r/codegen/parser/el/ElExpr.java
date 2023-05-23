package com.m2r.codegen.parser.el;

import com.m2r.mdsl.utils.StringWrapper;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElExpr {

    private static Pattern DYNAMIC_REMOVE_PATTERN = Pattern.compile("remove(\\w+)");
    private static Pattern DYNAMIC_CONVERT_PATTERN = Pattern.compile("convert(\\w+)To(\\w+)");
    private static Map<String, String> DYNAMIC_PARAMS_MAP;

    static {
        DYNAMIC_PARAMS_MAP = new HashMap<>();
        DYNAMIC_PARAMS_MAP.put("Space", " ");
        DYNAMIC_PARAMS_MAP.put("Dot", "\\.");
        DYNAMIC_PARAMS_MAP.put("Slash", "/");
        DYNAMIC_PARAMS_MAP.put("Underscore", "_");
        DYNAMIC_PARAMS_MAP.put("Dash", "-");
    }

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
        Object realParent = parent instanceof String ? StringWrapper.of((String) parent) : parent;

        if (realParent instanceof StringWrapper) {
            if (attributeName.startsWith("remove")) {
                return callDynamicRemove((StringWrapper) realParent, attributeName);
            }
            else if (attributeName.startsWith("convert")) {
                return callDynamicConvert((StringWrapper) realParent, attributeName);
            }
        }

        Method[] methods =  realParent.getClass().getDeclaredMethods();
        Method method = Arrays.stream(methods)
                .filter(it -> {
                    return it.getName().equals(String.format("get%s", an.toPascalCase())) ||
                            it.getName().equals(String.format("is%s", an.toPascalCase())) ||
                            it.getName().equals(String.format("to%s", an.toPascalCase()));
                })
                .findFirst()
                .orElse(null);

        return method != null ? method.invoke(realParent) : resolveAlternativeMethod(realParent, attributeName, context);
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
            try {
                Method getParamMethod = parent.getClass().getDeclaredMethod("getParam", String.class);
                if (getParamMethod != null) {
                    return getParamMethod.invoke(parent, attributeName);
                }
            }
            catch (NoSuchMethodException e) {
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

    private static String callDynamicRemove(StringWrapper parent, String attributeName) {
        if (parent == null) {
            return null;
        }
        Matcher matcher = DYNAMIC_REMOVE_PATTERN.matcher(attributeName);
        if (matcher.find()) {
            String param = matcher.group(1);
            String regex = DYNAMIC_PARAMS_MAP.get(param);
            if (param != null && regex != null) {
                return parent.replace(regex, "");
            }
        }
        return null;
    }

    private static String callDynamicConvert(StringWrapper parent, String attributeName) {
        if (parent == null) {
            return null;
        }
        Matcher matcher = DYNAMIC_CONVERT_PATTERN.matcher(attributeName);
        if (matcher.find()) {
            String param1 = matcher.group(1);
            String param2 = matcher.group(2);
            String regex = DYNAMIC_PARAMS_MAP.get(param1);
            String replaciment = DYNAMIC_PARAMS_MAP.get(param2);
            if (param1 != null && param2 != null && regex != null && replaciment != null) {
                return parent.replace(regex, replaciment);
            }
        }
        return null;
    }

}
