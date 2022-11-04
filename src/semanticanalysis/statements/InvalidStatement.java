package semanticanalysis.statements;

import semanticanalysis.Statement;
import semanticanalysis.Visitor;

public class InvalidStatement extends Statement {
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
