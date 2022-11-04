package lexicalanalysis;

import lexicalanalysis.tokens.Symbol;

import java.util.HashMap;
import java.util.Map;

public class StringTable {
	private Map<String, Symbol> table = new HashMap<>();

	public Symbol getSymbol(String string) {
		Symbol symbol = table.get(string);
		if (symbol == null) {
			symbol = new Symbol(string);
			table.put(string, symbol);
		}
		return symbol;
	}
}
