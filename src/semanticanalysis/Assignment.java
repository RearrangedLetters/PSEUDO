package semanticanalysis;

import lexicalanalysis.tokens.Symbol;
import semanticanalysis.expressions.Expression;

public class Assignment extends Expression {
	private final Symbol identifier;
	private final Expression expression;
	private Referenceable destination;

	public Assignment(Symbol identifier, Expression expression) {
		this.identifier = identifier;
		this.expression = expression;
	}

	public Symbol getIdentifier() {
		return identifier;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setDestination(Referenceable destination) {
		this.destination = destination;
	}

	public Referenceable getDestination() {
		return destination;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
