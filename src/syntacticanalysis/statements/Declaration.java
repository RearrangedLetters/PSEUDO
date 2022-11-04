package syntacticanalysis.statements;

import lexicalanalysis.tokens.Symbol;
import syntacticanalysis.Referenceable;
import syntacticanalysis.SemanticType;
import syntacticanalysis.Visitor;

public class Declaration extends Statement implements Referenceable {
	private final SemanticType type;
	private final Symbol name;

	public Declaration(SemanticType type, Symbol name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public SemanticType getType() {
		return type;
	}

	public Symbol getName() {
		return name;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
