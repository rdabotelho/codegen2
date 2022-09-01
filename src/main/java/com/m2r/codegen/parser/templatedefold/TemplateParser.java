package com.m2r.codegen.parser.templatedefold;

import com.m2r.codegen.parser.templatedef.Method;
import com.m2r.codegen.parser.templatedef.TemplateDef;
import com.m2r.easyparser.Parser;
import com.m2r.easyparser.ParserException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TemplateParser extends Parser<TemplateProcess> {

    private TemplateDef templateDef;

    static private enum TokenType implements ITokenType {

        CAR_RETURN("\\r"),
        BREAK_LINE("\\n"),
        OTHER(".");

        private Pattern regex;

        private TokenType(String regex) {
            this.regex = Pattern.compile("^("+regex+")");
        }

        public Pattern getRegex() {
            return regex;
        }

    }

    public TemplateParser(TemplateDef template) {
        super(false);
        this.templateDef = template;
    }

    protected TemplateProcess execute(Reader reader) throws ParserException {
        try {
            return super.execute(reader);
        }
        catch (Exception e) {
            System.out.println(this.pos);
            System.out.println(this.tokens);
            throw e;
        }
    }

    public static TemplateProcess parse(TemplateDef templateDef, Reader reader) throws ParserException {
        TemplateParser me = new TemplateParser(templateDef);
        return me.execute(reader);
    }

    @Override
    protected ITokenType[] getTokenTypes() {
        return TokenType.values();
    }

    @Override
    protected boolean ignoreToken(Token token) {
        return false;
    }

    @Override
    protected void sem() throws ParserException {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (Token token : tokens) {
            line.append(token.getValue());
            if (token.getType() == TokenType.BREAK_LINE) {
                lines.add(line.toString());
                line.setLength(0);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }

        Method rootMethod = Method.createRoot();
        rootMethod.setBlock(Block.create(rootMethod));
        templateDef.getMethods().forEach(m -> rootMethod.getMethods().add(m));
        addBlockMethodContent(rootMethod, 1, lines);

        result.setRoot(rootMethod);
    }

    private void addBlockMethodContent(Method parentMethod, int offset, List<String> lines) {
        int currentLine = offset;
        List<Method> methods = new ArrayList<>();
        parentMethod.getMethods().forEach(m -> methods.add(m));
        parentMethod.getMethods().clear();
        for (Method method : methods) {
            int startLine = method.getBlock().getLineStart();
            int endLine = method.getBlock().getLineEnd();
            if (currentLine < startLine) {
                Method startMethod = Method.createBorder(parentMethod, currentLine, startLine - 1);
                List<String> subLines = getLines(lines, startMethod.getBlock(), offset);
                startMethod.getBlock().setContent(Content.of(subLines));
                parentMethod.getMethods().add(startMethod);
            }
            List<String> subLines = getLines(lines, method.getBlock(), offset);
            method.getBlock().setContent(Content.of(subLines));
            addBlockMethodContent(method, startLine, subLines);
            if (method.getName().equals("iterate")) {
                Method middle = Method.createBorder(method, method.getBlock().getLineStart(), method.getBlock().getLineEnd());
                middle.getBlock().getContent().substitute(method.getBlock().getContent());
                method.getMethods().forEach(m -> {
                    middle.getMethods().add(m);
                    m.setParent(middle);
                });
                middle.getBlock().setContent(method.getBlock().getContent());
                method.getMethods().clear();
                method.getMethods().add(middle);
            }
            parentMethod.getMethods().add(method);
            currentLine = endLine + 1;
        }
        int offsetEnd = offset +  lines.size() - 1;
        if (parentMethod.getBlock().getLineEnd() == 0) { // root
            parentMethod.getBlock().setLineEnd(offsetEnd);
            parentMethod.getBlock().setContent(Content.of(lines));
        }
        if (currentLine <= parentMethod.getBlock().getLineEnd()) {
            Method endMethod = Method.createBorder(parentMethod, currentLine, parentMethod.getBlock().getLineEnd());
            List<String> subLines = getLines(lines, endMethod.getBlock(), offset);
            endMethod.getBlock().setContent(Content.of(subLines));
            parentMethod.getMethods().add(endMethod);
        }
    }

    private List<String> getLines(List<String> lines, Block block, int offset) {
        List<String> subLines = new ArrayList<>();
        int start = block.getLineStart() - offset;
        int end = block.getLineEnd() - offset;
        for (int i=start; i<=end; i++) {
            if (i < lines.size()) {
                subLines.add(lines.get(i));
            }
        }
        return subLines;
    }

    @Override
    protected boolean exp() {
        return true;
    }

    @Override
    protected boolean isEnd() {
        return true;
    }

}

