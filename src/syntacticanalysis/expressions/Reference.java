package syntacticanalysis.expressions;

import lexicalanalysis.tokens.Symbol;
import syntacticanalysis.Referenceable;
import syntacticanalysis.Visitor;


public class Reference extends Expression {
	private final Symbol identifier;
	private Referenceable source;

	public Reference(Symbol identifier) {
		this.identifier = identifier;
	}

	public Symbol getIdentifier() {
		return identifier;
	}

	public void setSource(Referenceable destination) {
		this.source = destination;
	}

	public Referenceable getSource() {
		return this.source;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
