package syntacticanalysis.statements;

import syntacticanalysis.Visitor;
import syntacticanalysis.expressions.Expression;

public class If extends Statement {
	private final Expression condition;
	private final Statement thenStatement;
	private final Statement elseStatement;

	public If(Expression condition, Statement trueStatement, Statement falseStatement) {
		this.condition = condition;
		this.thenStatement = trueStatement;
		this.elseStatement = falseStatement;
	}

	public Expression getCondition() {
		return condition;
	}

	public Statement getThenStatement() {
		return thenStatement;
	}

	public Statement getElseStatement() {
		return elseStatement;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
