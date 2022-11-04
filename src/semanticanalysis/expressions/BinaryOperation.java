package semanticanalysis.expressions;

import semanticanalysis.Visitor;

public class BinaryOperation extends Expression {
	public enum Type {
		PLUS("+"),
		MINUS("-"),
		MULTIPLY("*"),
		DIVIDE("/"),
		EQUAL("="),
		NOTEQUAL("≠"),
		LESS("<"),
		LESS_THAN("≤"),
		GREATER(">"),
		GREATER_THAN("≥"),
		OR("∨"),
		AND("∧"),
		;

		private final String string;
		private Type(String string) {
			this.string = string;
		}

		@Override
		public String toString() {
			return "Type{" +
					"string='" + string + '\'' +
					'}';
		}
	}

	private Type type;
	private Expression left_handside;
	private Expression right_handside;

	public BinaryOperation(Type type, Expression left_handside, Expression right_handside) {
		this.type = type;
		this.left_handside = left_handside;
		this.right_handside = right_handside;
	}

	public Type getOperationType() {
		return type;
	}

	public Expression getLeft_handside() {
		return left_handside;
	}

	public Expression getRight_handside() {
		return right_handside;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
