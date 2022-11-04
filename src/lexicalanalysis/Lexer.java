package lexicalanalysis;

import lexicalanalysis.tokens.Identifier;
import lexicalanalysis.tokens.MilabToken;
import lexicalanalysis.tokens.Symbol;
import lexicalanalysis.tokens.Token;
import lexicalanalysis.tokens.TokenType;
import lexicalanalysis.tokens.Number;

import java.io.Reader;
import java.util.Dictionary;
import java.util.Hashtable;

public class Lexer {
	private Reader inputReader;
	private StringTable stringTable;
	private final int INVALID = Integer.MIN_VALUE;
	private int currentCharacter = INVALID;
	private Dictionary<String, TokenType> tokens = new Hashtable<>();
	private Exception lastException;

	public Lexer(Reader inputReader, StringTable stringTable) {
		this.inputReader = inputReader;
		this.stringTable = stringTable;
		nextCharacter();

	}

	private void nextCharacter() {
		try {
			currentCharacter = inputReader.read();
		} catch (Exception e) {
			currentCharacter = INVALID;
		}
	}

	public Token nextToken() {
		switch (currentCharacter) {
			case INVALID -> {
				if (lastException == null) {
					return new MilabToken(TokenType.EOF);
				} else {
					return new MilabToken(TokenType.ERROR);
				}
			}
			case ' ', '\t', '\n' -> {
				nextCharacter();
				return nextToken();
			}
			case '+' -> {
				nextCharacter();
				return new MilabToken(TokenType.PLUS);
			}
			case '{' -> {
				nextCharacter();
				return new MilabToken(TokenType.CURLY_OPEN);
			}
			case '}' -> {
				nextCharacter();
				return new MilabToken(TokenType.CURLY_CLOSE);
			}
			case '(' -> {
				nextCharacter();
				return new MilabToken(TokenType.PARENTHESIS_OPEN);
			}
			case ')' -> {
				nextCharacter();
				return new MilabToken(TokenType.PARENTHESIS_CLOSE);
			}
			case '<' -> {
				nextCharacter();
				if (currentCharacter == '-') {
					nextCharacter();
					return new MilabToken(TokenType.ASSIGN);
				} else if (currentCharacter == '=') {
					nextCharacter();
					return new MilabToken(TokenType.LESS_THAN);
				} else {
					return new MilabToken(TokenType.LESS);
				}
			}
			default -> {
				if (Character.isDigit(currentCharacter)) {
					return parseNumber();
				} else if (Character.isUnicodeIdentifierPart(currentCharacter)) {
					return parseIdentifier();
				}
				lastException = new Exception("Encountered unexpected character!");
				return new MilabToken(TokenType.ERROR);
			}
		}
	}

	private Token parseIdentifier() {
		StringBuilder identifierString = new StringBuilder();
		do {
			identifierString.append((char) currentCharacter);
			nextCharacter();
		} while (Character.isUnicodeIdentifierPart(currentCharacter));
		String tokenString = identifierString.toString();
		TokenType tokenType = tokens.get(tokenString);
		if (tokenType != null) {
			return new MilabToken(tokenType);
		}
		Symbol identifier = stringTable.getSymbol(tokenString);
		return new Identifier(identifier);
	}

	private Token parseNumber() {
		StringBuilder numberString = new StringBuilder();
		do {
			numberString.append((char) currentCharacter);
			nextCharacter();
		} while (Character.isDigit(currentCharacter));
		return new Number(numberString.toString());
	}
}