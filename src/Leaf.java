/**
 * A leaf node in a code tree. It has a symbol value. Immutable.
 * @see CodeTree
 */
public final class Leaf extends Node {
	
	public final String symbol;
	
	public Leaf(String sym) {
		this.symbol = sym;
	}
}
