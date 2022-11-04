package semanticanalysis.statements;

import semanticanalysis.Statement;
import semanticanalysis.Visitor;
import semanticanalysis.expressions.Expression;

public class ExpressionStatement extends Statement {
	private final Expression expression;

	public ExpressionStatement(Expression expression) {
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
