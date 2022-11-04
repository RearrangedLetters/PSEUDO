package syntacticanalysis.expressions;

import syntacticanalysis.Visitor;

public class InvalidExpression extends Expression {
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
