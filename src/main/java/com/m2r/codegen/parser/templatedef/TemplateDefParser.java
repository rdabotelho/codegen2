package com.m2r.codegen.parser.templatedef;

import com.m2r.codegen.parser.TokenIterator;
import com.m2r.codegen.parser.templatedefold.Block;
import com.m2r.easyparser.Parser;
import com.m2r.easyparser.ParserException;
import java.io.Reader;
import java.util.regex.Pattern;

public class TemplateDefParser extends Parser<TemplateDef> {

    private static final int ATTRIBUTE = 1;
    private static final int METHOD = 2;
    private static final int PARAM = 3;

    private Method root;

    static private enum TokenType implements ITokenType {

        ID("[a-zA-Z][a-zA-Z0-9_\\.\\<\\>]*"),
        DELIMITER("[\\{\\}\\(\\)\\,:]"),
        STRING("'(.*?)'"),
        INTEGER("\\d+"),
        FLOAT("\\d+\\.\\d+"),
        BOOLEAN("boolean"),
        SPACE("\\s");

        private Pattern regex;

        private TokenType(String regex) {
            this.regex = Pattern.compile("^("+regex+")");
        }

        public Pattern getRegex() {
            return regex;
        }

    }

    public TemplateDefParser() {
        super(true);
    }

    public static TemplateDef parse(Reader reader) throws ParserException {
        TemplateDefParser me = new TemplateDefParser();
        return me.execute(reader);
    }

    @Override
    protected ITokenType[] getTokenTypes() {
        return TokenType.values();
    }

    @Override
    protected boolean ignoreToken(Token token) {
        return token.getType() == TokenType.SPACE;
    }

    private void updateTemplateDefinition(TemplateDefParser templateParser) {
        this.root = Method.createRoot();
        TokenIterator tokens = TokenIterator.of(templateParser.getTokens());
        if (tokens.nextEqual("template")) {
            if (tokens.nextEqual("{")) {
                tokens.next();
                while (tokens.getLast() != null && !tokens.lastEqual("}")) {
                    if (tokens.getLast().isFlag(ATTRIBUTE)) {
                        templateParser.result.getAttributes().add(extractAttribute(tokens));
                    }
                    else if (tokens.getLast().isFlag(METHOD)) {
                        templateParser.result.getMethods().add(extractMethod(root, tokens));
                    }
                }
            }
        }
    }

    private Attribute extractAttribute(TokenIterator tokens) {
        Attribute attribute = new Attribute();
        attribute.setName(tokens.lastAsString());
        tokens.next();
        attribute.setValue(extractString(tokens.nextAsString()));
        tokens.next();
        return attribute;
    }

    private Method extractMethod(Method parent, TokenIterator tokens) {
        Method method = Method.create(parent, tokens.lastAsString());
        method.setPos(tokens.getLast().getPos());
        if (tokens.nextEqual("(")) {
            while (tokens.getLast() != null && !tokens.lastEqual(")")) {
                Token param = tokens.next();
                if (param.toString().equals(",")) {
                    param = tokens.next();
                }
                String paramValue = extractString(param.getValue());
                method.getParameters().add(new Param(paramValue, param.getType() != TokenType.STRING));
                tokens.next();
            }
            tokens.next();
        }
        if (tokens.lastEqual("{")) {
            tokens.next();
            while (tokens.getLast() != null && !tokens.lastEqual("}")) {
                if (tokens.getLast().isFlag(METHOD)) {
                    method.getMethods().add(extractMethod(method, tokens));
                }
            }
            tokens.next();
        }
        if (method.isBlock()) {
            method.setBlock(Block.create(method));
        }
        return method;
    }

    private String extractString(String value) {
        if (value == null) return null;
        return value.replaceAll("'","");
    }

    /**
     * Gramática Livre de Contexto
     *
     * TEMPLATE			-> template <TEMPLATE_BODY>
     * TEMPLATE_BODY	-> { <DEFINITION_LIST> } | { }
     * DEFINITION_LIST  -> <DEFINITION> \n <DEFINITION_LIST> | <DEFINITION> \n
     * DEFINITION       -> <ATTRIBUTE> | <METHOD>
     * ATTRIBUTE		-> ID : <VALUE>
     * METHOD           -> ID <PARAMS_BODY> <METHOD-BODY> | ID <PARAMS_BODY>
     * PARAMS_BODY      -> ( <PARAMS_LIST> ) | ( )
     * PARAMS_LIST		-> <PARAM> , <PARAMS_LIST> | <PARAM>
     * PARAM    		-> ID | <VALUE>
     * METHOD_BODY      -> { <METHOD_LIST> } | { }
     * METHOD_LIST      -> <METHOD> <METHOD_LIST> | <METHOD>
     * VALUE            -> STRING | INTEGER | FLOAT | BOOLEAN
     */

    @Override
    protected void sem() throws ParserException {
        for (Method method : result.getMethods()) {
            if (!method.getName().equals("block")) {
                throw new ParserException("Method '" + method.getName() + "' not allowed in this place", method.getPos());
            }
            if (method.getParameters().size() < 2) {
                throw new ParserException("Block method required 2 parameter (lineStart and lineEnd)", method.getPos());
            }
            if (!method.getParameters().get(0).getValue().matches("\\d+")) {
                throw new ParserException("Parameter lineStart must be integer", method.getPos());
            }
            if (!method.getParameters().get(1).getValue().matches("\\d+")) {
                throw new ParserException("Parameter lineEnd must be integer", method.getPos());
            }
        }
        result.getMethods().sort((a,b) -> {
            Integer num1 = Integer.parseInt(a.getParameters().get(0).getValue());
            Integer num2 = Integer.parseInt(b.getParameters().get(0).getValue());
            return num1.compareTo(num2);
        });
        int currentLine = 0;
        for (Method method : result.getMethods()) {
            Integer line = Integer.parseInt(method.getParameters().get(0).getValue());
            if (line <= currentLine) {
                throw new ParserException("Can't be there no overlapping lines in the blocks", method.getPos());
            }
            currentLine = line;
        }
    }

    @Override
    protected boolean exp() {
        int start = pos;
        boolean ok = (template() || reset(start));
        if (ok) {
            updateTemplateDefinition(this);
        }
        return ok;
    }

    protected boolean template() {
        int start = pos;
        return ((term(TokenType.ID, "template") && templateBody()) || reset(start));
    }

    protected boolean templateBody() {
        int start = pos;
        return ((term(TokenType.DELIMITER, "{") && definitionList() && term(TokenType.DELIMITER, "}")) || reset(start)) ||
                ((term(TokenType.DELIMITER, "{") && term(TokenType.DELIMITER, "}")) || reset(start));
    }

    protected boolean definitionList() {
        int start = pos;
        return ((definition() && definitionList()) || reset(start)) ||
                ((definition()) || reset(start));
    }

    protected boolean definition() {
        int start = pos;
        return ((attribute()) || reset(start)) ||
                ((method()) || reset(start));
    }

    protected boolean attribute() {
        int start = pos;
        return ((term(TokenType.ID) && term(TokenType.DELIMITER, ":") && value() && flag(start, ATTRIBUTE)) || reset(start));
    }

    protected boolean method() {
         int start = pos;
         return ((term(TokenType.ID) && paramsBody() && methodBody() && flag(start, METHOD)) || reset(start)) ||
                 ((term(TokenType.ID) && paramsBody() && flag(start, METHOD)) || reset(start));
     }

    protected boolean paramsBody() {
        int start = pos;
        return ((term(TokenType.DELIMITER, "(") && paramsList() && term(TokenType.DELIMITER, ")")) || reset(start)) ||
                ((term(TokenType.DELIMITER, "(") && term(TokenType.DELIMITER, ")")) || reset(start));
    }

     protected boolean paramsList() {
         int start = pos;
         return ((param() && term(TokenType.DELIMITER, ",") && paramsList()) || reset(start)) ||
                 ((param()) || reset(start));
     }

     protected boolean param() {
         int start = pos;
         return ((term(TokenType.ID) && flag(start, PARAM)) || reset(start)) ||
                 ((value() && flag(start, PARAM)) || reset(start));
     }

    protected boolean methodBody() {
        int start = pos;
        return ((term(TokenType.DELIMITER, "{") && methodList() && term(TokenType.DELIMITER, "}")) || reset(start)) ||
                ((term(TokenType.DELIMITER, "{") &&  term(TokenType.DELIMITER, "}")) || reset(start));
    }

    protected boolean methodList() {
        int start = pos;
        return ((method() && methodList()) || reset(start)) ||
                ((method()) || reset(start));
    }

    protected boolean value() {
        int start = pos;
        return ((term(TokenType.STRING)) || reset(start)) ||
                ((term(TokenType.INTEGER)) || reset(start)) ||
                ((term(TokenType.FLOAT)) || reset(start)) ||
                ((term(TokenType.BOOLEAN)) || reset(start));
    }

    private boolean flag(int pos, int flag) {
        getTokens().get(pos).flag(flag);
        return true;
    }

}
