package syntacticanalysis;

import lexicalanalysis.Lexer;
import lexicalanalysis.StringTable;
import lexicalanalysis.tokens.Identifier;
import lexicalanalysis.tokens.Symbol;
import lexicalanalysis.tokens.Token;
import lexicalanalysis.tokens.TokenType;
import syntacticanalysis.expressions.Assignment;
import syntacticanalysis.expressions.BinaryOperation;
import syntacticanalysis.expressions.Call;
import syntacticanalysis.expressions.Expression;
import syntacticanalysis.expressions.InvalidExpression;
import syntacticanalysis.expressions.NumberExpression;
import syntacticanalysis.expressions.Reference;
import syntacticanalysis.expressions.UnaryOperation;
import syntacticanalysis.statements.Block;
import syntacticanalysis.statements.ExpressionStatement;
import syntacticanalysis.statements.InvalidStatement;
import syntacticanalysis.statements.Statement;

import java.util.ArrayList;

public class Parser {
	private final Lexer lexer;
	private final StringTable stringTable;
	private Token currentToken;
	private boolean hadErrors = false;

	private void parseError(String message) {
		System.err.println(message);
		hadErrors = true;
	}

	public Parser(Lexer lexer, StringTable stringTable) {
		this.lexer = lexer;
		this.stringTable = stringTable;
	}

	private void nextToken() {
		currentToken = lexer.nextToken();
	}

	private SemanticType getParseType() {
		switch (currentToken.getType()) {
			case INTEGER -> {
				nextToken();
				return SemanticType.INTEGER;
			}
			case VOID -> {
				nextToken();
				return SemanticType.VOID;
			}
			default -> {
				parseError("Expected type, got " + currentToken + " instead");
				return SemanticType.INVALID;
			}
		}
	}

	private Statement parseBlock() {
		Block result = new Block();
		skip(TokenType.CURLY_OPEN);
		while (currentToken.getType() != TokenType.CURLY_CLOSE) {
			if (currentToken.getType() == TokenType.EOF) {
				parseError("Missing \"}\"");
				return result;
			}
			Statement statement = parseStatement();
			if (statement != null) result.addStatement(statement);
		}
		skip(TokenType.CURLY_CLOSE);
		return result;
	}

	private Statement parseStatement() {
		switch (currentToken.getType()) {
			case CURLY_OPEN -> {
				return parseBlock();
			}
			case RETURN -> {
				return parseReturn();
			}
			case NUMBER -> {
				return parseExpressionStatement();
			}
			default -> {
				parseError("Expected statement");
				return new InvalidStatement();
			}
		}
	}

	private void skipTo(TokenType type) {
		TokenType currentType = currentToken.getType();
		while (currentType != type && currentType != TokenType.EOF) {
			nextToken();
		}
	}

	private Statement parseExpressionStatement() {
		Expression expression = parseExpression();
		if (currentToken.getType() != TokenType.EOL) {
			parseError(String.format("Expected semicolon after expression, got '%s'", currentToken));
			skipTo(TokenType.EOL);
		}
		skip(TokenType.EOL);
		return new ExpressionStatement(expression);
	}

	private Expression parseExpression() {
		Expression expression = parseOrOperation();
		while (true) {
			if (currentToken.getType() == TokenType.OR) {
				skip(TokenType.OR);
				Expression right = parseOrOperation();
				expression = new BinaryOperation(BinaryOperation.Type.OR, expression, right);
				continue;
			}
			break;
		}
		return expression;
	}

	private Expression parseOrOperation() {
		Expression expression = parseAndOperation();
		while (true) {
			if (currentToken.getType() == TokenType.AND) {
				skip(TokenType.AND);
				Expression right = parseAndOperation();
				expression = new BinaryOperation(BinaryOperation.Type.AND, expression, right);
				continue;
			}
			break;
		}
		return expression;
	}

	private Expression parseAndOperation() {
		Expression expression = parseEqualityOperation();
		while (true) {
			switch (currentToken.getType()) {
				case DEFINE -> {
					skip(TokenType.DEFINE);
					Expression right = parseEqualityOperation();
					expression = new BinaryOperation(BinaryOperation.Type.EQUAL, expression, right);
					continue;
				}
				case NOT_EQUAL -> {
					skip(TokenType.NOT_EQUAL);
					Expression right = parseEqualityOperation();
					expression = new BinaryOperation(BinaryOperation.Type.NOT_EQUAL, expression, right);
					continue;
				}
				default -> {}
			}
			break;
		}
		return expression;
	}

	private Expression parseEqualityOperation() {
		Expression expression = parseRelationalOperation();
		while (true) {
			switch (currentToken.getType()) {
				case LESS -> {
					skip(TokenType.LESS);
					Expression right = parseRelationalOperation();
					expression = new BinaryOperation(BinaryOperation.Type.LESS, expression, right);
					continue;
				}
				case LESS_THAN -> {
					skip(TokenType.LESS_THAN);
					Expression right = parseRelationalOperation();
					expression = new BinaryOperation(BinaryOperation.Type.LESS_EQUAL, expression, right);
					continue;
				}
				case GREATER -> {
					skip(TokenType.GREATER);
					Expression right = parseRelationalOperation();
					expression = new BinaryOperation(BinaryOperation.Type.GREATER, expression, right);
					continue;
				}
				case GREATER_THAN -> {
					skip(TokenType.GREATER_THAN);
					Expression right = parseRelationalOperation();
					expression = new BinaryOperation(BinaryOperation.Type.GREATER_EQUAL, expression, right);
					continue;
				}
				default -> {
				}
			}
			break;
		}
		return expression;
	}

	private Expression parseRelationalOperation() {
		Expression expression = parseTerm();
		while (true) {
			switch (currentToken.getType()) {
				case PLUS -> {
					skip(TokenType.PLUS);
					Expression right = parseTerm();
					expression = new BinaryOperation(BinaryOperation.Type.PLUS, expression, right);
					continue;
				}
				case MINUS -> {  // binary MINUS
					skip(TokenType.MINUS);
					Expression right = parseTerm();
					expression = new BinaryOperation(BinaryOperation.Type.MINUS, expression, right);
					continue;
				}
				default -> {
				}
			}
			break;
		}
		return expression;
	}

	private Expression parseTerm() {
		Expression expression = parseFactor();
		while (true) {
			switch (currentToken.getType()) {
				case TIMES -> {
					skip(TokenType.TIMES);
					Expression right = parseFactor();
					expression = new BinaryOperation(BinaryOperation.Type.MULTIPLY, expression, right);
					continue;
				}
				case DIVIDE -> {
					skip(TokenType.DIVIDE);
					Expression right = parseFactor();
					expression = new BinaryOperation(BinaryOperation.Type.DIVIDE, expression, right);
					continue;
				}
				default -> {
				}
			}
			break;
		}
		return expression;
	}

	private Expression parseAssignment(Symbol destination) {
		skip(TokenType.EQUAL);
		Expression expression = parseExpression();
		return new Assignment(destination, expression);
	}

	private Expression parseFactor() {
		switch (currentToken.getType()) {
			case PLUS: // unary plus
				skip(TokenType.PLUS);
				return parseFactor();
			case MINUS: { // unary minus
				skip(TokenType.MINUS);
				Expression op = parseFactor();
				return new UnaryOperation(UnaryOperation.Type.MINUS, op);
			}
			case NOT: {
				skip(TokenType.NOT);
				Expression op = parseFactor();
				return new UnaryOperation(UnaryOperation.Type.NOT, op);
			}
			case NUMBER: {
				Expression result = new NumberExpression(currentToken.toString());
				skip(TokenType.NUMBER);
				return result;
			}
			case IDENTIFIER: {
				Symbol symbol = ((Identifier) currentToken).getSymbol();
				skip(TokenType.IDENTIFIER);
				if (currentToken.getType() == TokenType.CURLY_OPEN) {
					return parseCall(symbol);
				}
				if (currentToken.getType() == TokenType.EQUAL) {
					return parseAssignment(symbol);
				}
				return new Reference(symbol);
			}
			case CURLY_OPEN:
				return parseBraceExpression();
			default:
				parseError(String.format("Parse Error: Expected expression, got token '%s'", currentToken));
				skipTo(TokenType.EOL);
				return new InvalidExpression();
		}
	}

	private Expression parseBraceExpression() {
		if (currentToken.getType() != TokenType.CURLY_OPEN) {
			parseError(String.format("Expected '(', got '%s'", currentToken));
			return new InvalidExpression();
		}
		skip(TokenType.CURLY_OPEN);
		Expression result = parseExpression();
		if (currentToken.getType() != TokenType.CURLY_CLOSE) {
			parseError(String.format("Expected ')' got '%s'", currentToken));
			skipTo(TokenType.EOL);
			return new InvalidExpression();
		}
		skip(TokenType.CURLY_CLOSE);
		return result;
	}

	private Expression parseCall(Symbol callee) {
		ArrayList<Expression> arguments = new ArrayList<Expression>();
		skip(TokenType.CURLY_OPEN);
		if (currentToken.getType() != TokenType.CURLY_CLOSE) {
			while (true) {
				if (currentToken.getType() == TokenType.EOF) {
					parseError("Unexpected end of file while parsing call arguments");
					return new InvalidExpression();
				}
				Expression argument = parseExpression();
				arguments.add(argument);
				if (currentToken.getType() == TokenType.CURLY_CLOSE) {
					break;
				}
				if (currentToken.getType() != TokenType.COMMA) {
					parseError(String.format("Expected ')' or ',' after argument, got '%s'", currentToken));
					skipTo(TokenType.CURLY_CLOSE);
					break;
				}
				skip(TokenType.COMMA);
			}
		}
		skip(TokenType.CURLY_CLOSE);
		return new Call(callee, arguments);
	}

	private Statement parseReturn() {
		return null;
	}

	private void skip(TokenType type) {
		assert currentToken.getType() == type;
		nextToken();
	}

	private Procedure parseProcedure() {
		SemanticType returnSemanticType = parseType();
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			parseError(String.format("Expected identifier, got '%s'", currentToken));
			skipTo(TokenType.CURLY_CLOSE);
			skip(TokenType.CURLY_CLOSE);
			return null;
		}
		Symbol name = ((Identifier) currentToken).getSymbol();
		nextToken();

		ArrayList<Parameter> parameters = parseParameters();
		if (currentToken.getType() != TokenType.CURLY_OPEN) {
			parseError(String.format("Expected '{', got '%s'", currentToken));
			return null;
		}
		Statement body = parseBlock();
		return new Procedure(returnSemanticType, name, body, parameters);
	}

	private ArrayList<Parameter> parseParameters() {
		ArrayList<Parameter> result = new ArrayList<Parameter>();
		if (currentToken.getType() != TokenType.CURLY_OPEN) {
			parseError(String.format("expected parameterlist, got '%s'", currentToken));
			skipTo(TokenType.CURLY_OPEN);
			return result;
		}
		skip(TokenType.CURLY_OPEN);
		boolean first = true;
		while (currentToken.getType() != TokenType.CURLY_CLOSE) {
			if (!first) {
				if (currentToken.getType() != TokenType.COMMA) {
					parseError(String.format("Expected comma, got '%s'", currentToken));
					skipTo(TokenType.CURLY_CLOSE);
					break;
				}
				skip(TokenType.COMMA);
			} else {
				first = false;
			}
			if (currentToken.getType() == TokenType.EOF) {
				parseError("reached enf of file while parsing parameterlist");
				return result;
			}
			SemanticType type = parseType();
			if (currentToken.getType() != TokenType.IDENTIFIER) {
				parseError(String.format("Expected identifier, got '%s'", currentToken));
				skipTo(TokenType.CURLY_CLOSE);
				break;
			}
			Symbol name = ((Identifier) currentToken).getSymbol();
			skip(TokenType.IDENTIFIER);
			result.add(new Parameter(type, name));
		}
		skip(TokenType.CURLY_CLOSE);
		return result;
	}

	private SemanticType parseType() {
		switch (currentToken.getType()) {
			case INTEGER -> {
				nextToken();
				return SemanticType.INTEGER;
			}
			case BOOLEAN -> {
				nextToken();
				return SemanticType.BOOLEAN;
			}
			case VOID -> {
				nextToken();
				return SemanticType.VOID;
			}
			default -> {
				parseError(String.format("Expected type, got token " + currentToken));
				return SemanticType.INVALID;
			}
		}
	}

	public Program parseProgram() {
		ArrayList<Procedure> procedures = new ArrayList<>();
		// populate the procedure list with builtin functions
		Symbol readSymbol = stringTable.getSymbol("read");
		Procedure read = new Procedure(SemanticType.INTEGER, readSymbol, null, new ArrayList<Parameter>());
		procedures.add(read);

		Symbol printlnSymbol = stringTable.getSymbol("println");
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter(SemanticType.INTEGER, stringTable.getSymbol("value")));
		Procedure println = new Procedure(SemanticType.VOID, printlnSymbol, null, parameters);
		procedures.add(println);

		while (currentToken.getType() != TokenType.EOF) {
			Procedure procedure = parseProcedure();
			procedures.add(procedure);
		}

		if (hadErrors) return null;
		return new Program(procedures);
	}
}
