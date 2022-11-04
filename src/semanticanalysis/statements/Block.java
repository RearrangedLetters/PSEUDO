package semanticanalysis.statements;

import semanticanalysis.Statement;
import semanticanalysis.Visitor;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;

public class Block extends Statement {
	private List<Statement> statements = new ArrayList<>();

	public void addStatement(Statement statement) {
		statements.add(statement);
	}

	public List<Statement> getStatements() {
		return statements;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
