package lexicalanalysis.tokens;

public class Symbol {
	private final String symbol;

	public Symbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return "{\"" + symbol + '\"' + '}';
	}
}
