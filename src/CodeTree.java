/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;


/**
 * A binary tree that represents a mapping between symbols and binary strings.
 * The data structure is immutable. There are two main uses of a code tree:
 * <ul>
 *   <li>Read the root field and walk through the tree to extract the desired information.</li>
 *   <li>Call getCode() to get the binary code for a particular encodable symbol.</li>
 * </ul>
 * <p>The path to a leaf node determines the leaf's symbol's code. Starting from the root, going
 * to the left child represents a 0, and going to the right child represents a 1. Constraints:</p>
 * <ul>
 *   <li>The root must be an internal node, and the tree is finite.</li>
 *   <li>No symbol value is found in more than one leaf.</li>
 *   <li>Not every possible symbol value needs to be in the tree.</li>
 * </ul>
 * <p>Illustrated example:</p>
 * <pre>  Huffman codes:
 *    0: Symbol A
 *    10: Symbol B
 *    110: Symbol C
 *    111: Symbol D
 *  
 *  Code tree:
 *      .
 *     / \
 *    A   .
 *       / \
 *      B   .
 *         / \
 *        C   D</pre>
 * @see FrequencyTable
 * @see CanonicalCode
 */
public final class CodeTree {
	
	/*---- Fields and constructor ----*/
	
	/**
	 * The root node of this code tree (not {@code null}).
	 */
	public final InternalNode root;
	
	//Stores the code for each symbol, as a list of integers
	private Map<String, String> codes;
	/**
	 * Constructs a code tree from the specified tree of nodes and specified symbol limit.
	 * Each symbol in the tree must have value strictly less than the symbol limit.
	 * @param root the root of the tree
	 * @param symbolLimit the symbol limit
	 * @throws NullPointerException if tree root is {@code null}
	 * @throws IllegalArgumentException if the symbol limit is less than 2, any symbol in the tree has
	 * a value greater or equal to the symbol limit, or a symbol value appears more than once in the tree
	 */
	public CodeTree(InternalNode root, int symbolLimit) {
		this.root = Objects.requireNonNull(root);	
		codes = new HashMap<String, String>();  // Initially all null
		buildCodeList(root, "");  // Fill 'codes' with appropriate data
	}
	
	
	// Recursive helper function for the constructor
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
	
	
	
	/*---- Various methods ----*/
	
	/**
	 * Returns the Huffman code for the specified symbol, which is a list of 0s and 1s.
	 * @param symbol the symbol to query
	 * @return a list of 0s and 1s, of length at least 1
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
