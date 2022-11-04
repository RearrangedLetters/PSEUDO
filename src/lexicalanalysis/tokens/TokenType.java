package lexicalanalysis.tokens;

public enum TokenType {
	VOID("void"),
	PARENTHESIS_OPEN("("),
	PARENTHESIS_CLOSE(")"),
	CURLY_OPEN("{"),
	CURLY_CLOSE("}"),
	RETURN("RETURN"),
	NUMBER(""),
	ASSIGN_LEFT("<-"),
	PLUS("+"),
	;

	private final String string;

	TokenType(String string) {
		this.string = string;
	}

	public String getString() {
		return this.string;
	}
}