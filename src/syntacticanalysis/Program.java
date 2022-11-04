package syntacticanalysis;

import java.util.List;

public record Program(List<Procedure> procedures) {
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
