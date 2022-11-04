package syntacticanalysis;

public enum SemanticType {
	VOID,
	INTEGER,
	BOOLEAN,
	INVALID,
	;

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
