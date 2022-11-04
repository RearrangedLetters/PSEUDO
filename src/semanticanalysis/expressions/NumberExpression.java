package semanticanalysis.expressions;

import semanticanalysis.Visitor;

public class NumberExpression extends Expression {
	private final String string;

	public NumberExpression(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

	public int toInt() {
		return Integer.parseInt(string);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
