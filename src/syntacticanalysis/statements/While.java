package syntacticanalysis.statements;

import syntacticanalysis.Visitor;
import syntacticanalysis.expressions.Expression;

public class While extends Statement {
	private final Expression condition;
	private final Statement body;

	public While(Expression condition, Statement body) {
		this.condition = condition;
		this.body = body;
	}

	public Expression getCondition() {
		return condition;
	}

	public Statement getBody() {
		return body;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}