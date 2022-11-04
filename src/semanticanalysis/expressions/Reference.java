package semanticanalysis.expressions;

import lexicalanalysis.tokens.Symbol;
import semanticanalysis.Visitor;

import javax.naming.Referenceable;

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
		return source;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
