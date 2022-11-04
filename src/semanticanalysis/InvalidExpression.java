package semanticanalysis;

import semanticanalysis.expressions.Expression;

public class InvalidExpression extends Expression {
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
