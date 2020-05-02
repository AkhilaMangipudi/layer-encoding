import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;


/**
 * A binary tree that represents a mapping between symbols and strings.
 * The data structure is immutable. There are two main uses of a code tree:
 * <ul>
 *   <li>Read the root field and walk through the tree to extract the desired information.</li>
 *   <li>Call getCode() to get the huffman code for a particular encodable symbol.</li>
 * </ul>
 * <p>The path to a leaf node determines the leaf's symbol's code. Starting from the root, going
 * to the left child represents a 0, and going to the right child represents a 1. Constraints:</p>
 * <ul>
 *   <li>The root must be an internal node, and the tree is finite.</li>
 *   <li>No symbol value is found in more than one leaf.</li>
 * </ul>
 */
public final class CodeTree {
	
	/**
	 * The root node of this code tree (not {@code null}).
	 */
	public final InternalNode root;
	
	//Stores the code for each symbol, as a String
	private Map<String, String> codes;
	/**
	 * Constructs a code tree from the specified tree of nodes.
	 * @param root the root of the tree
	 * @throws NullPointerException if tree root is {@code null}
	 */
	public CodeTree(InternalNode root) {
		this.root = Objects.requireNonNull(root);	
		codes = new HashMap<String, String>();  // Initially all null
		buildCodeList(root, "");  // Fill 'codes' with appropriate data
	}
	
	
	// Recursive helper function for the constructor
	// 0 is added for the left branch and 1 for the right branch
	private void buildCodeList(Node node, String prefix) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			
			prefix = prefix + "0";
			buildCodeList(internalNode.leftChild , prefix);
			prefix = prefix.substring(0, prefix.length() - 1);
			
			prefix = prefix + "1";
			buildCodeList(internalNode.rightChild, prefix);
			prefix = prefix.substring(0, prefix.length() - 1);
			
		} else if (node instanceof Leaf) {
			Leaf leaf = (Leaf)node;
			if(!codes.containsKey(leaf.symbol)) {
				codes.put(leaf.symbol, prefix);
			}
			else
				throw new IllegalArgumentException("Symbol has more than one code");
		} else {
			throw new AssertionError("Illegal node type");
		}
	}
	
	
	/**
	 * Returns the Huffman code for the specified symbol, which is a String that contains 0s and 1s.
	 * @param symbol the symbol to query
	 * @return a String which contains 0s and 1s
	 * @throws IllegalArgumentException if the symbol is negative, or no
	 * Huffman code exists for it (e.g. because it had a zero frequency)
	 */
	public String getCode(String symbol) {
		if (codes.get(symbol) == "")
			throw new IllegalArgumentException("No code for given symbol");
		else
			return codes.get(symbol);
	}
	
}
