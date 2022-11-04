import lexicalanalysis.Lexer;
import lexicalanalysis.StringTable;
import lexicalanalysis.tokens.Token;
import lexicalanalysis.tokens.TokenType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Main {
	public static void main(String[] args) {
		Lexer lexer;
		try {
			Reader inputReader = new BufferedReader(new FileReader("examples/hello_world.pseudo"));
			StringTable stringTable = new StringTable();
			lexer = new Lexer(inputReader, stringTable);
			Token token;
			do {
				token = lexer.nextToken();
				System.out.println(token);
			} while (token.getType() != TokenType.EOF && token.getType() != TokenType.ERROR);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
