package syntacticanalysis.statements;

import syntacticanalysis.Visitor;

public class InvalidStatement extends Statement {
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
