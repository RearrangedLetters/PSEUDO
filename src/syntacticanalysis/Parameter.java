package syntacticanalysis;

import lexicalanalysis.tokens.Symbol;

public class Parameter implements Referenceable {
	private SemanticType semanticType;
	private Symbol name;

	public Parameter(SemanticType semanticType, Symbol name) {
		this.semanticType = semanticType;
		this.name = name;
	}

	@Override
	public SemanticType getType() {
		return semanticType;
	}

	public Symbol getName() {
		return name;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
