package com.m2r.codegen.parser.templatedefold;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Content {

    private StringBuilder buffer = new StringBuilder();
    private boolean visible = true;

    public static Content empty() {
        Content result = new Content();
        return result;
    }

    public static Content of(Content content) {
        Content result = new Content();
        result.substitute(content);
        return result;
    }

    public static Content of(List<String> lines) {
        Content result = new Content();
        lines.forEach(it -> result.buffer.append(it));
        return result;
    }

    public Content clean() {
        buffer.setLength(0);
        return this;
    }

    public Content substitute(Content value) {
        return substitute(value.toString());
    }

    public Content substitute(String value) {
        clean();
        append(value);
        return this;
    }

    public Content append(Content value) {
        return append(value.toString());
    }

    public Content append(String value) {
        buffer.append(value);
        return this;
    }

    public Content replace(String regex, String replacement) {
        String cache = buffer.toString();
        substitute(cache.replaceAll(regex, replacement));
        return this;
    }

    public Content format(String format, Object ... args) {
        substitute(String.format(format, args));
        return this;
    }

    public Content insert(int offset, String str) {
        buffer.insert(offset, str);
        return this;
    }

    public int length() {
        return buffer.length();
    }

    public List<String> toLines() {
        List<String> lines = new ArrayList<>();
        Scanner scan = new Scanner(buffer.toString());
        while(scan.hasNextLine()){
            lines.add(scan.nextLine() + "\n");
        }
        scan.close();
        return lines;
    }

    public List<String> toLines(int start, int end) {
        return toLines(start, end, 0);
    }

    public List<String> toLines(int start, int end, int offset) {
        List<String> subLines = new ArrayList<>();
        List<String> lines = toLines();
        for (int i=(start - offset); i<=(end - offset); i++) {
            subLines.add(lines.get(i));
        }
        return subLines;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

}
