package lexicalanalysis.tokens;

public enum TokenType {
	VOID("void"),
	INTEGER("ℤ"),
	NATURAL("ℕ"),
	BOOLEAN("𝔹"),
	PARENTHESIS_OPEN("("),
	PARENTHESIS_CLOSE(")"),
	CURLY_OPEN("{"),
	CURLY_CLOSE("}"),
	RETURN("Return"),
	NUMBER("Number"),
	DEFINE(":="),
	EQUAL("="),
	NOT_EQUAL("≠"),
	PLUS("+"),
	MINUS("-"),
	TIMES("*"),
	DIVIDE("/"),
	EOF("EOF"),
	ERROR("Error"),
	LESS_THAN("≤"),
	LESS("<"),
	GREATER_THAN("≥"),
	GREATER(">"),
	IDENTIFIER("Identifier"),
	EOL("\n"),
	OR("∨"),
	AND("∧"),
	NOT("¬"),
	COMMA(","),
	;

	private final String string;

	TokenType(String string) {
		this.string = string;
	}

	public String getString() {
		return this.string;
	}

	@Override
	public String toString() {
		return "TokenType{" + '\"' + string + '\"' + '}';
	}
}