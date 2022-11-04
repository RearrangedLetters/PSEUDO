package semanticanalysis;

import semanticanalysis.expressions.Expression;

public class UnaryOperation extends Expression {
	public enum Type {
		MINUS("-"),
		NOT("¬");

		private String string;
		Type(String niceString) {
			this.string = niceString;
		}

		@Override
		public String toString() {
			return "Type{" +
					"string='" + string + '\'' +
					'}';
		}
	}
	private final Type type;
	private final Expression operation;

	public UnaryOperation(Type type, Expression op) {
		this.type = type;
		this.operation = op;
	}

	public Type getUnaryOperationType() {
		return this.type;
	}

	public Expression getOperation() {
		return this.operation;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
