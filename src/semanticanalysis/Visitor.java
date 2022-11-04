package semanticanalysis;

import semanticanalysis.expressions.BinaryOperation;
import semanticanalysis.expressions.NumberExpression;
import semanticanalysis.expressions.Reference;
import semanticanalysis.statements.Block;
import semanticanalysis.statements.ExpressionStatement;
import semanticanalysis.statements.InvalidStatement;

public interface Visitor {
	void visit(SemanticType semanticType);
	void visit(Block block);
	void visit(InvalidStatement invalidStatement);
	void visit(Program program);
	void visit(Procedure procedure);
	void visit(ExpressionStatement expressionStatement);
	void visit(BinaryOperation.Type type);
	void visit(BinaryOperation binaryOperation);
	void visit(UnaryOperation unaryOperation);
	void visit(Parameter parameter);
	void visit(Assignment assignment);
	void visit(InvalidExpression invalidExpression);
	void visit(NumberExpression numberExpression);
	void visit(Call call);
	void visit(Reference reference);
}
