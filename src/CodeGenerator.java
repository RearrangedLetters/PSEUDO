import syntacticanalysis.Parameter;
import syntacticanalysis.Procedure;
import syntacticanalysis.Program;
import syntacticanalysis.Referenceable;
import syntacticanalysis.SemanticType;
import syntacticanalysis.Visitor;
import syntacticanalysis.expressions.Assignment;
import syntacticanalysis.expressions.BinaryOperation;
import syntacticanalysis.expressions.Call;
import syntacticanalysis.expressions.Expression;
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
import syntacticanalysis.statements.Statement;
import syntacticanalysis.statements.While;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Generate jasmin bytecode assembler from a parsed and semantically analysed Program
 */
public class CodeGenerator {
	protected String className;
	protected PrintWriter writer;
	protected int stackSize;
	protected int maxStackSize;
	protected int nextVariableNumber;
	protected int nextLabelNumber;
	protected Map<Referenceable, Integer> variableNumbers;
	protected CodeGenVisitor codeGeneratorVisitor = new CodeGenVisitor();

	/** Record changes of java-operand stack size.
	 * Also keep track of the maximum as we have to specify that
	 * for the bytecode later
	 */
	protected void changeStackSize(int delta) {
		stackSize += delta;
		assert stackSize >= 0;
		if (stackSize > maxStackSize)
			maxStackSize = stackSize;
	}

	/** Creates a new unique jump-label */
	protected String makeLabel() {
		return String.format("label%d", nextLabelNumber++);
	}

	/** Evaluates a boolean expression and jumps to the label specified by
	 * @p trueDestination if the expression evluates to true, jumps to the label
	 * @p falseDestination otherwise.
	 */
	protected void evaluateBooleanExpression(Expression expression, String trueDestination,
	                                         String falseDestination) {
		BooleanEvaluationVisitor visitor = new BooleanEvaluationVisitor(trueDestination, falseDestination);
		expression.accept(visitor);
	}

	/**
	 * This visitor is used to when evaluating boolean expressions for
	 * conditional jumps.
	 */
	protected class BooleanEvaluationVisitor implements Visitor {
		private String trueDestination;
		private String falseDestination;

		public BooleanEvaluationVisitor(String trueDestination, String falseDestination) {
			this.trueDestination = trueDestination;
			this.falseDestination = falseDestination;
		}

		@Override
		public void visit(Assignment assign) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		private void createShortCircuitAnd(BinaryOperation binop) {
			String leftTrueLabel = makeLabel();  // label to jump to if left operand is true
			evaluateBooleanExpression(binop.getLeftHandside(), leftTrueLabel, falseDestination);

			writer.printf("%s:\n", leftTrueLabel);
			evaluateBooleanExpression(binop.getRightHandside(), trueDestination, falseDestination);
		}

		private void createShortCircuitOr(BinaryOperation binop) {
			String leftFalseLabel = makeLabel();  // label to jump to if left operand is false
			evaluateBooleanExpression(binop.getLeftHandside(), trueDestination, leftFalseLabel);

			writer.printf("%s:\n", leftFalseLabel);
			evaluateBooleanExpression(binop.getRightHandside(), trueDestination, falseDestination);
		}

		@Override
		public void visit(BinaryOperation binaryOperation) {
			String compareSuffix = null;
			switch (binaryOperation.getOperationType()) {
				case EQUAL -> compareSuffix = "equal";
				case NOT_EQUAL -> compareSuffix = "not_equal";
				case LESS -> compareSuffix = "less_than";
				case LESS_EQUAL -> compareSuffix = "less";
				case GREATER -> compareSuffix = "greater_than";
				case GREATER_EQUAL -> compareSuffix = "greater_equal";
				case AND -> {
					createShortCircuitAnd(binaryOperation);
					return;
				}
				case OR -> {
					createShortCircuitOr(binaryOperation);
					return;
				}
				case PLUS, MINUS, MULTIPLY, DIVIDE -> {
					throw new RuntimeException("Unexpected boolean condition binop");
				}
			}

			binaryOperation.getLeftHandside().accept(codeGeneratorVisitor);
			binaryOperation.getRightHandside().accept(codeGeneratorVisitor);
			writer.printf("\tif_icmp%s %s\n", compareSuffix, trueDestination);
			changeStackSize(-2);
			writer.printf("\tgoto %s\n", falseDestination);
		}

		@Override
		public void visit(Block block) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(Call call) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(Declaration declaration) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(ExpressionStatement expressionStatement) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(If ifs) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(InvalidExpression invalidExpression) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(InvalidStatement invalidStatement) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(NumberExpression numberExpression) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(Parameter parameter) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(Procedure procedure) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(Program program) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(Reference reference) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(Return returns) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(SemanticType type) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}

		@Override
		public void visit(UnaryOperation unaryOperation) {
			assert unaryOperation.getUnaryOperationType() == UnaryOperation.Type.NOT;
			String t = trueDestination;
			trueDestination = falseDestination;
			falseDestination = t;
			unaryOperation.getOperation().accept(this);
		}

		@Override
		public void visit(While aWhile) {
			throw new RuntimeException("Internal compiler error: invalid boolean");
		}
	}

	/**
	 * This visitor handles creation of code to evaluate expressions
	 * where the value is needed. It also contains code to setup the general
	 * program structure like header/footer for each procedure.
	 */
	protected class CodeGenVisitor implements Visitor {
		@Override
		public void visit(Assignment assign) {
			assign.getExpression().accept(this);
			writer.printf("\tdup\n"); /* we need to store the value and use it as result for the assign operation */
			changeStackSize(1);

			Referenceable destination = assign.getDestination();
			int variable = variableNumbers.get(destination);
			writer.printf("\tistore %d\n", variable);
			changeStackSize(-1);
		}

		@Override
		public void visit(BinaryOperation binaryOperation) {
			binaryOperation.getLeftHandside().accept(this);
			binaryOperation.getRightHandside().accept(this);
			switch (binaryOperation.getOperationType()) {
				case PLUS -> writer.printf("\tiadd\n");
				case MINUS -> writer.printf("\tisub\n");
				case MULTIPLY -> writer.printf("\timul\n");
				case DIVIDE -> writer.printf("\tidiv\n");
				case EQUAL, NOT_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, AND, OR ->
						throw new RuntimeException("Internal compiler error: comparison op in unexpected place");
			}
			changeStackSize(-1);
		}

		@Override
		public void visit(Block block) {
			for (Statement statement : block.getStatements())
				statement.accept(this);
		}

		@Override
		public void visit(Call call) {
			/* produce arguments */
			for (Expression argument : call.getArguments())
				argument.accept(this);

			Procedure called = call.getCalledProcedure();
			String descriptor = createDescriptor(called);
			writer.printf("\tinvokestatic %s/%s\n", className, descriptor);
			changeStackSize(- call.getArguments().size());
			if (!called.getReturnType().equals(SemanticType.VOID))
				changeStackSize(1);
		}

		@Override
		public void visit(Declaration declaration) {
			variableNumbers.put(declaration, nextVariableNumber++);
		}

		@Override
		public void visit(ExpressionStatement expressionStatement) {
			Expression expression = expressionStatement.getExpression();
			expression.accept(this);

			if (!expression.getType().equals(SemanticType.VOID)) {
				/* drop result */
				writer.printf("\tpop\n");
				changeStackSize(-1);
			}
			assert stackSize == 0;
		}

		@Override
		public void visit(If ifs) {
			String thenLabel = makeLabel();
			String elseLabel = makeLabel();
			String afterLabel = makeLabel();

			evaluateBooleanExpression(ifs.getCondition(), thenLabel, elseLabel);

			writer.printf("%s:\n", thenLabel);
			ifs.getThenStatement().accept(this);
			writer.printf("\tgoto %s\n", afterLabel);

			writer.printf("%s:\n", elseLabel);
			Statement elseStatement = ifs.getElseStatement();
			if (elseStatement != null)
				elseStatement.accept(this);

			writer.printf("%s:\n", afterLabel);
		}

		@Override
		public void visit(InvalidExpression invalidExpression) {
			throw new RuntimeException("Internal Compiler Error: InvalidExpression in CodeGenerator");
		}

		@Override
		public void visit(InvalidStatement invalidStatement) {
			throw new RuntimeException("Internal Compiler Error: InvalidStatement in CodeGenerator");
		}

		@Override
		public void visit(NumberExpression numberExpression) {
			writer.printf("\tldc %d\n", numberExpression.toInt());
			changeStackSize(1);
		}

		@Override
		public void visit(Parameter parameter) {
			variableNumbers.put(parameter, nextVariableNumber++);
		}

		private void mangleType(StringBuffer result, SemanticType type) {
			switch (type) {
				case BOOLEAN -> {
					result.append('Z');
					return;
				}
				case INTEGER -> {
					result.append('I');
					return;
				}
				case VOID -> {
					result.append('V');
					return;
				}
				default -> {}
			}
			throw new RuntimeException("Don't know how to mangle type");
		}

		/**
		 * Java expects to mangle types into a methods name.
		 */
		private String createDescriptor(Procedure procedure) {
			StringBuffer result = new StringBuffer();
			result.append(procedure.getName().toString());
			result.append("(");
			for (Parameter parameter : procedure.getParameters())
				mangleType(result, parameter.getType());
			result.append(")");
			mangleType(result, procedure.getReturnType());

			return result.toString();
		}

		@Override
		public void visit(Procedure procedure) {
			String descriptor = createDescriptor(procedure);

			/* Turn a main()V function to a java-style
			 * main(String[] args) */
			boolean isMain = descriptor.equals("main()V");
			if (isMain)
				descriptor = "main([Ljava/lang/String;)V";

			/* don't output anything for our builtin procedures */
			Statement statement = procedure.getStatement();
			if (statement == null)
				return;

			writer.printf(".method public static %s\n", descriptor);

			variableNumbers = new HashMap<Referenceable, Integer>();
			nextVariableNumber = 0;
			nextLabelNumber = 0;
			if (isMain)
				nextVariableNumber++; /* skip the String[] argument */

			for (Parameter parameter : procedure.getParameters())
				parameter.accept(this);
			maxStackSize = 0;
			stackSize = 0;
			statement.accept(this);
			assert stackSize == 0;

			writer.printf("\t.limit stack %d\n", maxStackSize);
			writer.printf("\t.limit locals %d\n", nextVariableNumber);

			/* add a return statement in case it is missing on some paths */
			if (procedure.getReturnType().equals(SemanticType.VOID)) {
				writer.printf("\treturn\n");
			} else {
				writer.printf("\tldc 0\n");
				writer.printf("\tireturn\n");
			}
			writer.printf(".end method\n");
			writer.printf("\n");
		}

		@Override
		public void visit(Program program) {
			for (Procedure procedure : program.procedures()) {
				procedure.accept(this);
			}
		}

		@Override
		public void visit(Reference reference) {
			Referenceable source = reference.getSource();
			int variable = variableNumbers.get(source);
			writer.printf("\tiload %d\n", variable);
			changeStackSize(1);
		}

		@Override
		public void visit(Return returns) {
			Expression expression = returns.getExpression();
			if (expression == null) {
				writer.printf("\treturn\n");
			} else {
				expression.accept(this);
				changeStackSize(-1);
				assert stackSize == 0;
				writer.printf("\tireturn\n");
			}
		}

		@Override
		public void visit(SemanticType type) {}

		@Override
		public void visit(UnaryOperation unaryOperation) {
			unaryOperation.getOperation().accept(this);
			if (unaryOperation.getUnaryOperationType() == UnaryOperation.Type.MINUS) {
				writer.printf("\tineg\n");
			} else {
				throw new RuntimeException("Internal compiler error: unexpected unop in codegenerator");
			}
		}

		@Override
		public void visit(While whiles) {
			String loopHeaderLabel = makeLabel();
			String loopBodyLabel = makeLabel();
			String afterLabel = makeLabel();

			writer.printf("%s:\n", loopHeaderLabel);

			BooleanEvaluationVisitor booleanVisitor = new BooleanEvaluationVisitor(loopBodyLabel, afterLabel);
			whiles.getCondition().accept(booleanVisitor);

			writer.printf("%s:\n", loopBodyLabel);
			whiles.getBody().accept(this);
			writer.printf("\tgoto %s\n", loopHeaderLabel);

			writer.printf("%s:\n", afterLabel);
		}
	}

	private void writeFramework() throws IOException {
		// import "runtime library" this is simply a file with some methods predefined in jasmin assembler
		InputStream stdlib = getClass().getClassLoader().getResourceAsStream("stdlib.j");
		Reader reader = new InputStreamReader(stdlib);
		StringBuffer read = new StringBuffer();

		char[] buffer = new char[1024];
		int readChars;
		while ( (readChars = reader.read(buffer)) > 0)
			read.append(buffer, 0, readChars);

		String processed = Pattern.compile("MYNAME").matcher(read).replaceAll(className);
		writer.println(processed);
	}

	private void executeCodeGeneration(PrintWriter writer, Program program, String name) throws IOException {
		this.className = name;
		this.writer = writer;
		writeFramework();
		program.accept(codeGeneratorVisitor);
	}

	public static void generateCode(PrintWriter writer, Program program, String name) throws IOException {
		CodeGenerator codeGen = new CodeGenerator();
		codeGen.executeCodeGeneration(writer, program, name);
	}
}