package com.m2r.codegen.parser.script;

import com.m2r.codegen.parser.TokenIterator;
import com.m2r.easyparser.Parser;
import com.m2r.easyparser.ParserException;
import java.io.Reader;
import java.util.regex.Pattern;

public class ScriptParser extends Parser<DomainList> {

	static private enum TokenType implements ITokenType {
		
		ID("[a-zA-Z][a-zA-Z0-9_\\.\\<\\>]*"),
		DELIMITER("[\\{\\}\\(\\)\\,\\[,\\],:]"),
		STRING("'(.*?)'"),
		SPACE("\\s");
		
		private Pattern regex;

		private TokenType(String regex) {
			this.regex = Pattern.compile("^("+regex+")");
		}
		
		public Pattern getRegex() {
			return regex;
		}
		
	}

	public ScriptParser() {
		super(true);
	}

	public static DomainList parse(Reader reader) throws ParserException {
		ScriptParser me = new ScriptParser();
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

	private void updateDomainList() {
		TokenIterator tokens = TokenIterator.of(getTokens());
		tokens.next();
		while (tokens.getLast() != null) {
			Domain domain = new Domain(result);
			domain.setType(tokens.lastAsStringWrapper());
			domain.setName(tokens.nextAsStringWrapper());
			result.getDomains().add(domain);

			if (tokens.nextEqual("(")) {
				while (!tokens.lastEqual(")")) {
					String key = tokens.nextAsString();
					tokens.next();

					// start: params value
					if (tokens.nextEqual("[")) {
						ParamValue paramValue = new ParamValue(true);
						domain.getParams().put(key, paramValue);
						while (!tokens.lastEqual("]")) {
							StringWrapper value = tokens.nextAsStringWrapper();
							if (!value.toString().equals("]")) {
								paramValue.getValues().add(StringWrapper.of(extractString(value.getValue())));
								tokens.next();
							}
						}
					}
					else {
						StringWrapper value = tokens.lastAsStringWrapper();
						value.setValue(extractString(value.getValue()));
						ParamValue paramValue = new ParamValue(value);
						domain.getParams().put(key, paramValue);
					}
					tokens.next();
					// end: params value

				}
				tokens.next();
			}

			if (tokens.lastEqual("{")) {
				tokens.next();
				while (!tokens.lastEqual("}")) {
					DomainAttribute attr = new DomainAttribute(domain);
					if (domain.getType().toString().equals("enum")) {
						attr.setType(StringWrapper.of("String"));
						attr.setName(tokens.lastAsStringWrapper());
					}
					else {
						attr.setType(tokens.lastAsStringWrapper());
						attr.setName(tokens.nextAsStringWrapper());
					}
					domain.getAttributes().add(attr);
					if (tokens.nextEqual("(")) {
						while (!tokens.lastEqual(")")) {
							String key = tokens.nextAsString();
							tokens.next();

							// start: params value
							if (tokens.nextEqual("[")) {
								ParamValue paramValue = new ParamValue(true);
								attr.getParams().put(key, paramValue);
								while (!tokens.lastEqual("]")) {
									StringWrapper value = tokens.nextAsStringWrapper();
									if (!value.toString().equals("]")) {
										paramValue.getValues().add(StringWrapper.of(extractString(value.getValue())));
										tokens.next();
									}
								}
							}
							else {
								StringWrapper value = tokens.lastAsStringWrapper();
								value.setValue(extractString(value.getValue()));
								ParamValue paramValue = new ParamValue(value);
								attr.getParams().put(key, paramValue);
							}
							tokens.next();
							// end: params value

						}
						tokens.next();
					}
				}
				tokens.next();
			}
		}
		for (Domain domain : result.getDomains()) {
			domain.finallyProcess();
		}
	}

	private String extractString(String value) {
		if (value == null) return null;
		return value.replaceAll("'","");
	}

	/**
	 * Gramática Livre de Contexto
	 *
	 * DOMAIN_LIST		-> <DOMAIN> <DOMAIN_LIST> | <DOMAIN>
	 * DOMAIN			-> MODEL | ENUM
	 * MODEL			-> model <PID> { <ATTRIBUTES_LIST> } | model <PID> {  }
	 * ENUM				-> enum <PID> { <PID_LIST> }
	 * PID_LIST			-> <PID> <PID_LIST> | <PID>
	 * PID				-> ID ( <PARAMS_LIST> ) | ID
	 * PARAMS_LIST		-> <PARAM> , <PARAMS_LIST> | <PARAM>
	 * PARAM			-> ID : PARAM_VALUE
	 * PARAM_VALUE		-> [ STRING_LIST ] | [ ] | STRING
	 * STRING_LIST		-> STRING , STRING_LIST | STRING
	 * ATTRIBUTES_LIST	-> <ATTRIBUTE> ; </ATTRIBUTES_LIST> | <ATTRIBUTE> ;
	 * ATTRIBUTE		-> <TYPE> <PID>
	 * TYPE				-> ID
	 */

	@Override
	protected boolean exp() {
		int start = pos;
		boolean ok = (domainList() || reset(start));
		if (ok) {
			updateDomainList();
		}
		return ok;
	}

	protected boolean domainList() {
		int start = pos;
		return ((domain() && domainList()) || reset(start)) ||
				(domain() || reset(start));
	}

	protected boolean domain() {
		int start = pos;
		return (model() || reset(start)) ||
				(enumeration() || reset(start));
	}

	protected boolean model() {
		int start = pos;
		return ((term(TokenType.ID, "model") && pid() && term(TokenType.DELIMITER, "{") && attributesList() && term(TokenType.DELIMITER, "}")) || reset(start)) ||
				((term(TokenType.ID, "model") && pid() && term(TokenType.DELIMITER, "{") && term(TokenType.DELIMITER, "}")) || reset(start));
	}

	protected boolean enumeration() {
		int start = pos;
		return ((term(TokenType.ID, "enum") && pid() && term(TokenType.DELIMITER, "{") && pidlist() && term(TokenType.DELIMITER, "}")) || reset(start));
	}

	protected boolean pidlist() {
		int start = pos;
		return ((pid() && pidlist()) || reset(start)) ||
				((pid()) || reset(start));
	}

	protected boolean pid() {
		int start = pos;
		return((term(TokenType.ID) && term(TokenType.DELIMITER, "(") && paramsList() && term(TokenType.DELIMITER, ")")) || reset(start)) ||
				(term(TokenType.ID) || reset(start));
	}

	protected boolean paramsList() {
		int start = pos;
		return ((param() && term(TokenType.DELIMITER, ",") && paramsList()) || reset(start)) ||
				(param() || reset(start));
	}

	protected boolean param() {
		int start = pos;
		return ((term(TokenType.ID) && term(TokenType.DELIMITER, ":") && paramValue()) || reset(start));
	}

	protected boolean paramValue() {
		int start = pos;
		return ((term(TokenType.DELIMITER, "[") && stringList() && term(TokenType.DELIMITER, "]")) || reset(start)) ||
				((term(TokenType.DELIMITER, "[") && term(TokenType.DELIMITER, "]")) || reset(start)) ||
				(term(TokenType.STRING) || reset(start));
	}

	protected boolean stringList() {
		int start = pos;
		return ((term(TokenType.STRING) && term(TokenType.DELIMITER, ",") && stringList()) || reset(start)) ||
				(term(TokenType.STRING) || reset(start));
	}

	protected boolean attributesList() {
		int start = pos;
		return ((atribute() && attributesList()) || reset(start)) ||
				((atribute()) || reset(start));
	}

	protected boolean atribute() {
		int start = pos;
		return ((type() && pid()) || reset(start));
	}

	protected boolean type() {
		int start = pos;
		return term(TokenType.ID) || reset(start);
	}

}
