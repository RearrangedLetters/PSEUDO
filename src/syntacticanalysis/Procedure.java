package syntacticanalysis;

import lexicalanalysis.tokens.Symbol;
import syntacticanalysis.statements.Statement;

import java.util.List;

public class Procedure {
	private Symbol name;
	private Statement statement;
	private SemanticType returnSemanticType;
	private List<Parameter> parameters;

	public Procedure(SemanticType returnSemanticType, Symbol name, Statement statement, List<Parameter> parameters) {
		this.returnSemanticType = returnSemanticType;
		this.name = name;
		this.statement = statement;
		this.parameters = parameters;
	}

	public SemanticType getReturnType() {
		return returnSemanticType;
	}

	public Symbol getName() {
		return name;
	}

	public Statement getStatement() {
		return statement;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
