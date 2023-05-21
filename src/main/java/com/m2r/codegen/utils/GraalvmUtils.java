package com.m2r.codegen.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.m2r.codegen.command.CreateModelCommand;
import com.m2r.codegen.command.CreateTemplateCommand;
import com.m2r.codegen.command.GenerateCommand;
import com.m2r.codegen.command.InfoCommand;
import com.m2r.codegen.command.InitCommand;
import com.m2r.codegen.command.ShiftCommand;
import com.m2r.codegen.parser.templatedef.Template;
import com.m2r.codegen.parser.templatedef.TemplateDef;
import com.m2r.mdsl.model.Domain;
import com.m2r.mdsl.model.DomainAttribute;
import com.m2r.mdsl.model.DomainList;
import com.m2r.mdsl.model.ParamValue;
import com.m2r.mdsl.utils.StringWrapper;

public class GraalvmUtils {
    
    public static void init() {
        TemplateRepo.cloneBranch("https://github.com/rdabotelho/codegen-archetype.git", "master", new File("graalvm/temp"));
        new CreateModelCommand();
        new CreateTemplateCommand();
        new GenerateCommand();
        new InfoCommand();
        new InitCommand();
        new ShiftCommand();
        new TemplateDef();
        new DomainList();
        new Domain(null);
        new DomainAttribute(null);
        new ParamValue(null);
        new Template();
        new StringWrapper();
    }

    public static void scanAllMethods() {        
        try {
            List<String> packages = Arrays.asList("com.m2r.codegen.parser.el",
                "com.m2r.codegen.parser.templatedef",
                "com.m2r.codegen.parser.templatedefold");

            String content = "[\n";
            for (String packageName : packages) {
                List<Class<?>> classes = getClasses(packageName);
                for (Class<?> clazz : classes) {
                    content += scanMethods(clazz);
                }
            }
            content += "]";
            
            Path file = new File(System.getProperty("user.dir")+"/graalvm/classes.json").toPath();
            Files.write(file, content.getBytes());    
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String scanMethods(Class<?> clazz) throws Exception {
        Method[] methods = clazz.getDeclaredMethods();
        String json = "{\n" +
            "\t\"name\":\"" + clazz.getName() +"\",\n" +
            "\t\"allDeclaredFields\":true,\n" +
            "\t\"allDeclaredClasses\": true,\n" +
            "\t\"allPublicClasses\": true,\n" +
            "\t\"methods\":[\n" +
            "\t\t{\"name\":\"<init>\", \"parameterTypes\":[] }";

        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                continue;
            }   
            json += ",\n\t\t{\"name\":\"" + method.getName() + "\", \"parameterTypes\":[" + 
                Arrays.stream(method.getParameters())
                    .map(it -> "\"" + it.getType().getName() + "\"")
                    .collect(Collectors.joining(",")) + "] }";
        }

        json += "\n\t]\n";
        json += "},\n";
        return json;
    }

    private static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        String path = packageName.replace('.', '/');
        String base = System.getProperty("user.dir");
        File directory = new File(base + "/src/main/java", path);

        List<Class<?>> classes = new ArrayList<>();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        classes.addAll(getClasses(packageName + "." + file.getName()));
                    } else if (file.getName().endsWith(".java")) {
                        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 5);
                        classes.add(Class.forName(className));
                    }
                }
            }
        }

        return classes;
    }    

}
