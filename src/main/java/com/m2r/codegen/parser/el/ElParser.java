package com.m2r.codegen.parser.el;

import com.m2r.easyparser.Parser;
import com.m2r.easyparser.ParserException;

import java.io.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ElParser extends Parser<Writer> {

    private Writer writer;
    private Function<String, String> callback;

    static private enum TokenType implements ITokenType {

        ID("[a-zA-Z][a-zA-Z0-9_\\.]*"),
        DELIMITER("[\\$\\{\\}]"),
        SPACE("\\s"),
        OTHER(".");

        private Pattern regex;

        private TokenType(String regex) {
            this.regex = Pattern.compile("^("+regex+")");
        }

        public Pattern getRegex() {
            return regex;
        }

    }

    public ElParser(Writer writer, Function<String, String> filter) {
        super(false);
        this.writer = writer;
        this.callback = filter;
    }

    @Override
    protected Object newResult() {
        return this.writer;
    }

    public static Writer parse(Reader reader, Writer writer, Function<String, String> filter) throws ParserException {
        ElParser me = new ElParser(writer, filter);
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

    private boolean consume(int start) {
        Token tid = tokens.get(start);
        String value = tid.getValue();
        try {
            result.write(value);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean replace(int start) {
        Token tid = tokens.get(start + 2);
        String id = tid.getValue();
        String value = callback.apply(id);
        try {
            result.write(value);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Context-Free Grammar
     *
     * EXP				    -> <ANYTHING-LIST>
     * ANYTHING_LIST	    -> <ANYTHING> <ANYTHING_LIST> | <ANYTHING>
     * ANYTHING			    -> PROPERTY | ID | SPACE | OTHER
     * PROPERTY			    -> DELIMITER($) DELIMITER({) ID DELIMITER(})
     */

    @Override
    protected boolean exp() {
        int start = pos;
        return anythingList() || reset(start);
    }

    protected boolean anythingList() {
        int start = pos;
        return  isEnd() ||
                ((anything() && anythingList()) || reset(start)) ||
                (anything() || reset(start));
    }

    protected boolean anything() {
        int start = pos;
        return ((property() && replace(start)) || reset(start)) ||
                ((term(TokenType.ID) && consume(start)) || reset(start)) ||
                ((term(TokenType.SPACE) && consume(start)) || reset(start)) ||
                ((term(TokenType.OTHER) && consume(start)) || reset(start));
    }

    protected boolean property() {
        int start = pos;
        return (term(TokenType.DELIMITER, "$") && term(TokenType.DELIMITER, "{") && term(TokenType.ID) && term(TokenType.DELIMITER, "}")) || reset(start);
    }

}

