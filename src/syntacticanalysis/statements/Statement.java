package syntacticanalysis.statements;

import syntacticanalysis.Visitor;

public abstract class Statement {
	public abstract void accept(Visitor visitor);
}
