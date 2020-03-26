package edu.bu.ec504.hw2.bst;

public class ExtendedBSTSet<keyType extends Comparable<keyType>> extends BSTSet<keyType> {
  public ExtendedBSTSet() {
    key = null;
    leftChild = null;
    rightChild = null;
  }

  public ExtendedBSTSet(keyType theKey) {
    this();
    this.add(theKey);
  }

  public ExtendedBSTSet(Iterable<keyType> keys) {
    this();
    for (keyType key : keys) {
      this.add(key);
    }
  }

  public ExtendedBSTSet<keyType> getExtendedLeftChild() { return leftChild; }

  public ExtendedBSTSet<keyType> getExtendedRightChild() { return rightChild; }

  public class sr {
    sr(Boolean theSearchResult, ExtendedBSTSet<keyType> theLastSeen) {
      searchResult=theSearchResult;
      this.lastSeen =theLastSeen;
    }

    public boolean getSearchResult() {
      return searchResult;
    }

    public Integer getSearchCount() {
      return searchCount;
    }

    public ExtendedBSTSet<keyType> getLastSeen() {
      return lastSeen;
    }

    final Boolean searchResult;  // true iff the search was successful
    Integer searchCount=0;  // the number of nodes visited in the search
    ExtendedBSTSet<keyType> lastSeen;
  }

  public sr Helper(keyType searchKey) {
    sr tmpRecord;

    if (key == null) // the BST is null
      return null;
    else if (searchKey.compareTo(key) == 0) // i.e., searchKey == key
      tmpRecord = new sr(true, this);
    else if (searchKey.compareTo(key) > 0) {
      if (rightChild == null) // no subtree
        tmpRecord = new sr(false, this);
      else
        tmpRecord = rightChild.Helper(searchKey);
    } else
      if (leftChild == null)
        tmpRecord = new sr(false, this);
      else
        tmpRecord = leftChild.Helper(searchKey);

    assert tmpRecord != null;
    tmpRecord.searchCount++;
    return tmpRecord;
  }

  // METHODS
  /**
   * @return The parent of the current node.
   */
  public ExtendedBSTSet<keyType> getParent() {
    return parent;
  }

  public ExtendedBSTSet<keyType> parent() {
    return parent;
  }


  /**
   * Perform the rotation specified in <code>op</code> on the current tree.
   * @param op A rotational operation to complete.
   * @return true iff the rotation was successful.
   */
  public boolean rotate(BSTRotation<keyType> op) {
    return false;
  }

  /**
   * @inheritDoc
   */
  @Override
  public boolean add(keyType newKey) {
    sr theRecord = Helper(newKey);
    if (theRecord == null) {
      key = newKey;
    }
    else if (theRecord.searchResult)
      return false;
    else {
      ExtendedBSTSet<keyType> lastSeen = theRecord.lastSeen;
      if (newKey.compareTo(lastSeen.key) > 0) {
        lastSeen.rightChild = new ExtendedBSTSet<>(newKey);
        lastSeen.rightChild.parent = lastSeen;
      }
      else {
        lastSeen.leftChild = new ExtendedBSTSet<>(newKey);
        lastSeen.leftChild.parent = lastSeen;
      }
    }
    return true;
  }

  /**
   * @inheritDoc
   */
  @Override
  public String toString() {
    return super.toString();
  }

  String toString(String prefix) {
    String result = "";
    result += key + "\n";
    result += prefix + "->" + (leftChild == null ? "\n" : leftChild.toString(prefix.concat("  |")));
    result += prefix + "->" + (rightChild == null ? "\n": rightChild.toString(prefix.replace("  |","   ").concat("  |")));
    return result;
  }

  // FIELDS
  protected ExtendedBSTSet<keyType> parent;  // the parent of this node, or null if this node has no parent (e.g. it is the global root)
  public keyType key; // the key stored by this node
  public ExtendedBSTSet<keyType> leftChild;    // the left child of this node
  public ExtendedBSTSet<keyType> rightChild;   // the right child of this node
}
