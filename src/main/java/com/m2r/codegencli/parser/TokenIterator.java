package com.m2r.codegencli.parser;

import com.m2r.codegencli.parser.script.StringWrapper;
import com.m2r.easyparser.Parser;

import java.util.List;

public class TokenIterator {

    int pos = 0;
    private Parser.Token last;
    private List<Parser.Token> tokens;

    public static TokenIterator of(List<Parser.Token> tokens) {
        return new TokenIterator(tokens);
    }

    private TokenIterator(List<Parser.Token> tokens) {
        this.tokens = tokens;
    }

    public Parser.Token next() {
        last = null;
        if (pos < tokens.size()) {
            last = tokens.get(pos++);
        }
        return last;
    }

    public Parser.Token getLast() {
        return last;
    }

    public String lastAsString() {
        return getLast().getValue();
    }

    public StringWrapper lastAsStringWrapper() {
        return StringWrapper.of(lastAsString());
    }

    public String nextAsString() {
        return next().getValue();
    }

    public StringWrapper nextAsStringWrapper() {
        return StringWrapper.of(nextAsString());
    }

    public boolean nextEqual(String value) {
        return nextAsString().equals(value);
    }

    public boolean lastEqual(String value) {
        return lastAsString().equals(value);
    }

}
