package edu.bu.ec504.hw2.bst;

/**
 * Implements a simple (not necessarily balanced) Binary Search Tree.
 *
 * @param <keyType> The type of the keys stored in this Binary Search Tree.
 */
public class BST<keyType extends Comparable<keyType> > {
    // SUBCLASSES
    /**
     * Represents one rotation (ZIG or ZAG) around one key in a Binary Search Tree
     */
    public static class BSTRotation<keyType> {
        public enum RotationType {ZIG, ZAG}

        public BSTRotation(keyType myRotKey, RotationType myRotType) {
            rotKey = myRotKey;
            rotType = myRotType;
        }

        /**
         * @return A human-readable description of the rotation
         */
        public String toString() {
            StringBuilder result = new StringBuilder();
            switch (rotType) {
                case ZIG:
                    result.append("ZIG");
                    break;
                case ZAG:
                    result.append("ZAG");
                    break;
            }
            result.append(" on node ID ").append(rotKey);
            return result.toString();
        }

        // getters
        keyType getRotKey() { return rotKey; }
        RotationType getRotType() { return rotType; }

        private final keyType rotKey;            // the key of the root of the rotation being performed
        private final RotationType rotType;      // the type of rotation being performed
    }

    /**
     * Represents the result of a {@link #extendedContains(Comparable)} call.
     */
    final public class searchRecord {

        /**
         * Constructs a search record
         * @param theSearchResult - true iff the search was successful
         * @param theLastSeen - if the search succeeds, this is the node containing the searched key;
         *                    - if the search failed, this contains the last node before falling
         */
        searchRecord(Boolean theSearchResult, BST<keyType> theLastSeen) {
            searchResult=theSearchResult;
            this.lastSeen =theLastSeen;
        }

        /**
         * @return The result produced by the search the search represented by this record.
         *         The result is true iff the search was successful.
         */
        public boolean getSearchResult() {
            return searchResult;
        }

        /**
         * @return The number of nodes visited during the search represented by this record.
         */
        public Integer getSearchCount() {
            return searchCount;
        }

        /**
         * @return a pointer to the last node seen in the search represented by this record.
         */
        public BST<keyType> getLastSeen() {
            return lastSeen;
        }

        final Boolean searchResult;  // true iff the search was successful
        Integer searchCount=0;  // the number of nodes visited in the search
        final BST<keyType> lastSeen;
    }

    // CONSTRUCTORS

    /**
     * Constructs an empty Binary Search Tree
     */
    public BST() {
        key = null;
        leftChild = null;
        rightChild = null;
    }

    /**
     * Constructs a one-node Binary Search Tree with the given key.
     *
     * @param theKey The key to insert into the empty BST.
     */
    public BST(keyType theKey) {
        this();
        add(theKey);
    }

    /**
     * Constructs a Binary Search Tree by inserting the elements
     * in <code>keys</code>, in order, into the BST
     *
     * @param keys The keys to insert into the tree.
     */
    public BST(Iterable<keyType> keys) {
        this();
        for (keyType key : keys) {
            add(key);
        }
    }


    // METHODS

    // ... getters
    public final keyType getKey() { return key; }
    public BST<keyType> getLeftChild() { return leftChild; }
    public BST<keyType> getRightChild() { return rightChild; }

    // ... setters
    public void setLeftChild(BST<keyType> newLeft) { leftChild =newLeft; }
    public void setRightChild(BST<keyType> newRight) { rightChild =newRight; }

    // ... operators

    /**
     * Search for <code>newKey</code> and report a {@link searchRecord} of the result.
     * @param searchKey The key for which to search.
     * @return {@link searchRecord} statistics related to the search.  If the BST
     *    is null, returns a null searchRecord
     */
    protected searchRecord searchHelper(keyType searchKey) {
        searchRecord tmpRecord;

        if (key == null) // the BST is null
            return null;
        else if (searchKey.compareTo(key) == 0) // i.e., searchKey == key
            tmpRecord = new searchRecord(true, this);
        else if (searchKey.compareTo(key) > 0) {     // i.e., searchKey > key   --- look at the rightChild
            if (rightChild == null) // no subtree
                tmpRecord = new searchRecord(false, this);
            else
                tmpRecord = rightChild.searchHelper(searchKey);
        } else                                  // i.e., searchKey<=key    --- look at the leftChild
            if (leftChild == null)  // no subtree
                tmpRecord = new searchRecord(false, this);
            else
                tmpRecord = leftChild.searchHelper(searchKey);

        assert tmpRecord != null;   // tmpRecord should never be null here, if insertion is properly implemented.
        tmpRecord.searchCount++;    // for this node
        return tmpRecord;
    }

    // ... final methods that you cannot modify
    /**
     * Provides a search record with extended information about hte search for <code>theKey</code>.
     * @param theKey The key for which to search.
     * @return a <Boolean,Integer> pair:
     *    * the Boolean is true iff the BST rooted at the current object contains <code>theKey</code>;
     *    * the Integer returns the number of nodes visited in this subtree during the search for <code>theKey</code>.
     */
    final public searchRecord extendedContains(keyType theKey) {
        return searchHelper(theKey);
    }

    /**
     * A generic version of {@link #extendedContains(Comparable)} that matches the {@link java.util.Set}  interface.
     * Checks whether object obj exists in this data structure.
     * @param obj The object to check for containment in the data structure.
     * @return true iff the object is in the data structure
     * @throws ClassCastException if <code>obj</code> is not of type {@link keyType}.
     */
    @SuppressWarnings("unchecked")
    final public boolean contains(Object obj) {
        if (obj==null) return false;
        keyType theKey = (keyType) obj;
        return extendedContains(theKey).searchResult;
    }


    /**
     * Inserts the new number as a key into the binary search tree
     *
     * @param newKey The new key to be inserted.
     * @return true if the key was successfully inserted into the tree
     */
    public boolean add(keyType newKey) {
        searchRecord theRecord = searchHelper(newKey);

        if (theRecord == null) // insert here
            key = newKey;
        else if (theRecord.searchResult) // the item is already in the tree
            return false; // newKey is already in this data structure
        else {
            // put into the appropriate child
            BST<keyType> lastSeen = theRecord.lastSeen;
            if (newKey.compareTo(lastSeen.key) > 0) // i.e. newKey > key
                lastSeen.rightChild = new BST<>(newKey);
            else
                lastSeen.leftChild = new BST<>(newKey);
        }
        return true;
    }

    // ... informational
    /**
     * Return the BST rooted at this node as a human-readable string
     */
    public String toString() {
        return toString("|");
    }

    /**
     * Return the BST rooted at this node as a human-readable string,
     * indented by {@code depth} characters
     *
     * @param prefix The current prefix for the subtree being printed
     */
    String toString(String prefix) {
        String result = "";

        // output the key
        result += key + "\n";

        // recurse
        result += prefix + "->" + (leftChild == null ? "\n" : leftChild.toString(prefix.concat("  |")));
        result += prefix + "->" + (rightChild == null ? "\n": rightChild.toString(prefix.replace("  |","   ").concat("  |")));
        return result;
    }


    // FIELDS
    protected keyType key; // the key stored by this node
    protected BST<keyType> leftChild;    // the left child of this node
    protected BST<keyType> rightChild;   // the right child of this node
}
