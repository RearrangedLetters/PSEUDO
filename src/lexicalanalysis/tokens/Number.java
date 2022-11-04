package lexicalanalysis.tokens;

public class Number extends Token {

	private String numberString;

	public Number(String numberString) {
		super(TokenType.NUMBER);
		this.numberString = numberString;
	}

	public long getInt() {
		return Integer.parseInt(numberString);
	}

	@Override
	public String toString() {
		return "Token{" +
				"numberString='" + numberString + '\'' +
				'}';
	}
}
