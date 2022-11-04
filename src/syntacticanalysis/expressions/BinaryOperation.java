package syntacticanalysis.expressions;

import syntacticanalysis.Visitor;

public class BinaryOperation extends Expression {
	public enum Type {
		PLUS("+"),
		MINUS("-"),
		MULTIPLY("*"),
		DIVIDE("/"),
		EQUAL("="),
		NOT_EQUAL("≠"),
		LESS("<"),
		LESS_EQUAL("≤"),
		GREATER(">"),
		GREATER_EQUAL("≥"),
		OR("∨"),
		AND("∧"),
		;

		private final String string;
		Type(String string) {
			this.string = string;
		}

		@Override
		public String toString() {
			return "Type{" +
					"string='" + string + '\'' +
					'}';
		}
	}

	private final Type type;
	private final Expression leftHandside;
	private final Expression rightHandside;

	public BinaryOperation(Type type, Expression leftHandside, Expression rightHandside) {
		this.type = type;
		this.leftHandside = leftHandside;
		this.rightHandside = rightHandside;
	}

	public Type getOperationType() {
		return type;
	}

	public Expression getLeftHandside() {
		return leftHandside;
	}

	public Expression getRightHandside() {
		return rightHandside;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
