package lexicalanalysis.tokens;

public class Identifier extends Token {
	private Symbol symbol;

	public Identifier(Symbol symbol) {
		super(TokenType.IDENTIFIER);
		this.symbol = symbol;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return "Identifier{" +
				"symbol=" + symbol +
				'}';
	}
}
