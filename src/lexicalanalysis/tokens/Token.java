package lexicalanalysis.tokens;

public abstract class Token {
	private TokenType type;

	public Token(TokenType type) {
		this.type = type;
	}

	public TokenType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "Token{" +
				"type=" + type +
				'}';
	}
}
