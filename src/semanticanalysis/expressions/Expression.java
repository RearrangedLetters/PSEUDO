package semanticanalysis.expressions;

import semanticanalysis.Visitor;
import semanticanalysis.SemanticType;

public abstract class Expression {
	private SemanticType semanticType;

	public SemanticType getType() {
		return this.semanticType;
	}

	public void setType(SemanticType semanticType) {
		this.semanticType = semanticType;
	}

	public abstract void accept(Visitor visitor);
}
