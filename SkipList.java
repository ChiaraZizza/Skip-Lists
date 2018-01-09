/**
* Implementation of the Skip List data structures described in the article:
* 	William Pugh. 1990. Skip lists: a probabilistic alternative to balanced trees. 
*   Commun. ACM 33, 6 (June 1990), 668-676. DOI=10.1145/78973.78977. http://doi.acm.org/10.1145/78973.78977.
* @author Shida Jing, Chiara Zizza, David Neil Asanza
* Class: CSC 301-01
*/

import java.util.NoSuchElementException;
import java.util.Random;

public class SkipList {

	/**
	* Internal class for representing elements of the SkipList
	* 	Node class is non-static to allow direct access to SkipList instance fields
	*/
	private class Node  {
		public int level;      // number of forward pointers of this node
		public Node[] forward; // array holding pointers to later nodes in the list
		public String key;     // key for this node
		public String val;     // value associated with the key

		/**
		* Constructs a new Node of the given level that associates a key with a value
		* @param level the number of forward pointers in this node
		* @param key the key used for searching for val
		* @param val the value associated with key
		* @return a new Node
		* @pre_condition level is a positive integer
		* @pre_condition key is not null
		*/
		public Node (int level, String key, String val) {
			this.key = key;
			this.val = val;
			this.level = level;

			this.forward = new Node[level];
		}

		/**
		 * Compares the given search key to this node's key.
		 * @param searchKey the key to compare against this node's key
		 * @return order an integer indicating the relative ordering of
		 *           this node's key and searchKey
		 * @post_condition
		 *   if order is positive, then this.key > searchKey,
		 *   if order is negative, then this.key < searchKey,
		 *   if order is 0, then this.key == searchKey
		 *
		 * 	 If this node is the SkipList's nil node, order is always positive. In
		 * 	   other words, the SkipList's nil node has a key larger than every
		 * 	   possible key.
		 */
		public int compareToKey (String searchKey) {
			/* null key is is the largest possible key */
			if (key == null) {
				return Integer.MAX_VALUE;
			} else {
				return key.compareTo(searchKey);
			}
		}

		/**
		 * Returns the string representation of this node. The 'X's represent the
		 *   levels of the node, the '|' represent the forward pointers of the
		 *   node. The key and value of the node are included at the end of line of
		 *   'X's as 'key : value'.
		 * @return a string representing this node
		 */
		public String toString() {
			String ret = "";
			for (int i = 0; i < level; i++)  {
				ret += "X ";
			}
			for (int i = 0; i < (numLevels - level); i++)  {
				ret += "| ";
			}
			ret += "\t";
			ret +=  key + ": " + val + '\n';
			for (int i = 0; i < numLevels; i++)  {
				ret += "| ";
			}
			ret += '\n';
			return ret;
		}
	}

	/********************************
	* SkipList fields and methods *
	********************************/

	/* Maximum possible level of this SkipList */
	private static final int MAX_LEVEL = 16; // As suggested in Pugh (671)
	/* Probabilty that node with level i pointers also has level i + 1 pointers */
	private static final double PROBABILITY = 0.25; // As suggested in Pugh (673)

	private int numLevels; // Level of highest level node in this SkipList
	private Node header;   // The header of the SkipList
	private Node nil;      // The last element in the SkipList at every level
	private Random rand;   // A random number generator

	/**
	* Constructs an empty SkipList
	* @return list a new SkipList
	* @post_condition
	*   0 < list.numLevels <= MAX_LEVEL
	*   list.isEmpty() == true
	*   every forward pointer of list.header points to list.nil
	*/
	public SkipList () {
		numLevels = 1;
		nil = new Node(1, null, null);
		header = new Node(MAX_LEVEL, null, null);
		for (int i = 0; i < MAX_LEVEL; i++) {
			header.forward[i] = nil;
		}
		rand = new Random();
	}

	/**
	 * Determines whether there are any elements in this SkipList
	 * @return true if the list is empty, false otherwise
	 */
	public boolean isEmpty() {
		return header.forward[0] == nil;
	}

	/**
	 * Generates a random level to assign to a new node
	 * @return newLevel a positive interger
	 * @post_condition
	 *   1 <= newLevel <= MAX_LEVEL
	 */
	private int randomLevel() {
		int newLevel = 1;
		while (rand.nextDouble() < PROBABILITY) {
			newLevel++;
		}
		return Integer.min(newLevel, MAX_LEVEL);
	}

/**
 * Searches within a SkipList for a given key
 * @param  searchKey a key for which to search
 * @return value, the value associated with searchKey, if it exists
 * @throws NoSuchElementException if no such searchKey exists
 */
	public String search(String searchKey) {
		Node cur = header;
		/* loop invariant: cur.key < searchKey*/
		for (int i = numLevels-1; i >= 0; i--) {
			while (cur.forward[i].compareToKey(searchKey) < 0) {
				cur = cur.forward[i];
			}
		}

		/* cur.key < searchKey <= cur.forward.get(0).key */
		cur = cur.forward[0];

		if (cur.compareToKey(searchKey) == 0) {
			return cur.val;
		}  else  {
			throw new NoSuchElementException(searchKey + " was not found.");
		}
	}

	/**
	* Inserts a searchKey, newValue pair within a SkipList; if searchKey already
	*   exists within SkipList, the original value associated with it is replaced
	*   with newValue
	* @param searchKey a key for which to search
	* @param newValue a value to insert into the SkipList
	* @pre_condition searchKey is not null.
	* @post_condition
	*   SkipList is mutated
	*   this.search(searchKey) == newValue
	*/
	public void insert(String searchKey, String newValue)  {
		Node[] update = new Node[MAX_LEVEL];

		Node cur = header;
		/* loop invariant: cur.key < searchKey */
		for (int i = numLevels-1; i >= 0; i--) {
			while (cur.forward[i].compareToKey(searchKey) < 0) {
				cur = cur.forward[i];
			}
			/* cur.key < searchKey <= cur.forward.get(1).key */
			update[i] = cur;
		}
		cur = cur.forward[0];

		/* Update key to newValue if it already exists*/
		if (cur.compareToKey(searchKey) == 0) {
			cur.val = newValue;
			
		}
		
		else {
			int newLevel = randomLevel();
			
			/* If new node has higher level than maxLevels, continue populating
			 * update with additional levels to point to new node */
			if (newLevel > numLevels) {
				for  (int i = newLevel - 1; i >= numLevels; i--) {
					update[i] = header;
				}
				numLevels = newLevel;
			}
			
			/* Reassign pointers using update to correctly insert new node into SkipList */
			cur = new Node(newLevel, searchKey, newValue);
			for (int i = 0; i < newLevel; i++) {
				cur.forward[i] = update[i].forward[i];
				update[i].forward[i] = cur;
			}
		}
	}

	/**
	    * Deletes a node containing with key searchKey within a SkipList, if it exists
	    * @param searchKey a key for which to search
	    * @throws NoSuchElementException if searchKey does not exist
	    * @pre_condition searchKey is not null.
	    * @post_condition
	    *   SkipList is mutated
	    *   this.search(searchKey) throws NoSuchElementException
	    */
	public void delete(String searchKey) {
		Node[] update = new Node[MAX_LEVEL];

		Node cur = header;
		/* loop invariant: cur.key < searchKey*/
		for (int i = numLevels-1; i >= 0; i--) {
			while (cur.forward[i].compareToKey(searchKey) < 0) {
				cur = cur.forward[i];
			}
			/* cur.key < searchKey <= cur.forward.get(1).key */
			update[i] = cur;
		}
		cur = cur.forward[0];

		if (cur.compareToKey(searchKey) == 0) {
			for (int i = 0; i < numLevels; i++) {
				/* We have reached the first level above cur's highest level */
				if (update[i].forward[i] != cur) {
					break;
				}
				/* Remove pointers to cur */
				update[i].forward[i] = cur.forward[i];
			}
			/* If tallest node was deleted, reduce numLevels */
			while (numLevels > 1 && header.forward[numLevels-1] == nil) {
				numLevels--;
			}
		} else {
			throw new NoSuchElementException(searchKey + " was not found.");
		}
	}

	/**
	 * Returns a visual representation of the SkipList in the form of a String
	 * @return list a String representation of a SkipList
	 */
	public String toString() {
		String ret = "";
		ret += "Header \n";
		for (int i = 0; i < numLevels; i++)  {
			ret += "* ";
		}
		ret += '\n';
		for (int i = 0; i < numLevels; i++)  {
			ret += "| ";
		}
		ret += '\n';
		Node cur = header.forward[0];
		while (cur != nil) {
			ret += cur.toString();
			cur = cur.forward[0];
		}
		for (int i = 0; i < numLevels; i++)  {
			ret += "* ";
		}
		ret += "\nEnd of List\n";
		return ret;
	}
}
