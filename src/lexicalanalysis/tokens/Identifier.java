package lexicalanalysis.tokens;

public class Identifier extends Token {
	private Symbol symbol;

	public Identifier(TokenType type) {
		super(type);
	}

	public Symbol getSymbol() {
		return symbol;
	}
}
