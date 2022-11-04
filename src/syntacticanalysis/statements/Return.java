package syntacticanalysis.statements;

import syntacticanalysis.Visitor;
import syntacticanalysis.expressions.Expression;

public class Return extends Statement {
	private final Expression expression;

	public Return(Expression expression) {
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}