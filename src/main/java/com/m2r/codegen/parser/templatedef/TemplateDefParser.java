package com.m2r.codegen.parser.templatedef;

import com.m2r.codegen.parser.template.Method;
import com.m2r.codegen.parser.template.Template;
import com.m2r.easyparser.Parser;
import com.m2r.easyparser.ParserException;
import java.io.Reader;
import java.util.regex.Pattern;

public class TemplateDefParser extends Parser<FileContent> {

    private Template template;

    static private enum TokenType implements ITokenType {

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

    public TemplateDefParser(Template template) {
        super(false);
        this.template = template;
    }

    public static FileContent parse(Template templateDef, Reader reader) throws ParserException {
        TemplateDefParser me = new TemplateDefParser(templateDef);
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
        int currentLine = 1;
        result.getBlocks().clear();
        for (Method method : template.getMethods()) {
            int startLine = Integer.parseInt(method.getParameters().get(0).getValue());
            int endLine = Integer.parseInt(method.getParameters().get(1).getValue());
            if (currentLine < startLine) {
                result.getBlocks().add(new BlockContent(currentLine, startLine - 1, null));
            }
            result.getBlocks().add(new BlockContent(startLine, endLine, method));
            currentLine = endLine + 1;
        }
        result.getBlocks().add(new BlockContent(currentLine, currentLine, null));

        currentLine = 2;
        BlockContent block = result.getBlocks().get(0);
        for (Token token : tokens) {
            block.getContent().append(token.getValue());
            if (token.getType() == TokenType.BREAK_LINE) {
                BlockContent nextBlock = result.getBlockByLine(currentLine);
                if (nextBlock != null) {
                    block = nextBlock;
                }
                currentLine++;
            }
        }
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

