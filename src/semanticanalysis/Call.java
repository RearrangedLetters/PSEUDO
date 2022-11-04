package semanticanalysis;

import lexicalanalysis.tokens.Symbol;
import semanticanalysis.expressions.Expression;

import java.util.List;

public class Call extends Expression {
	private final Symbol callee;
	private Procedure calledProcedure;
	private final List<Expression> arguments;

	public Call(Symbol callee, List<Expression> arguments) {
		this.callee = callee;
		this.arguments = arguments;
	}

	public Symbol getCallee() {
		return callee;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	public void setCalledProcedure(Procedure procedure) {
		calledProcedure = procedure;
	}

	public Procedure getCalledProcedure() {
		return calledProcedure;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
