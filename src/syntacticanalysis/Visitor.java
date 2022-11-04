package syntacticanalysis;

import syntacticanalysis.expressions.Assignment;
import syntacticanalysis.expressions.BinaryOperation;
import syntacticanalysis.expressions.Call;
import syntacticanalysis.expressions.InvalidExpression;
import syntacticanalysis.expressions.NumberExpression;
import syntacticanalysis.expressions.Reference;
import syntacticanalysis.expressions.UnaryOperation;
import syntacticanalysis.statements.Block;
import syntacticanalysis.statements.Declaration;
import syntacticanalysis.statements.ExpressionStatement;
import syntacticanalysis.statements.If;
import syntacticanalysis.statements.InvalidStatement;
import syntacticanalysis.statements.Return;
import syntacticanalysis.statements.While;

public interface Visitor {
	void visit(SemanticType semanticType);
	void visit(Block block);
	void visit(InvalidStatement invalidStatement);
	void visit(Program program);
	void visit(Procedure procedure);
	void visit(ExpressionStatement expressionStatement);
	void visit(BinaryOperation binaryOperation);
	void visit(UnaryOperation unaryOperation);
	void visit(Parameter parameter);
	void visit(Assignment assignment);
	void visit(InvalidExpression invalidExpression);
	void visit(NumberExpression numberExpression);
	void visit(Call call);
	void visit(Reference reference);
	void visit(Declaration declaration);
	void visit(If anIf);
	void visit(Return aReturn);
	void visit(While aWhile);
}
