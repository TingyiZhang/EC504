package edu.bu.ec504.hw2.bst;

import java.util.*;

/**
 * An extension of BST that supports
 * @param <keyType>
 */
public class BSTSet<keyType extends Comparable<keyType>> extends BST<keyType> implements Set<keyType> {

  public List<keyType> keys;

  public BSTSet() {
    super();
  }

  public BSTSet(keyType theKey) {
    super(theKey);
  }

  public BSTSet(Iterable<keyType> keys) {
    super(keys);
  }

  /**
   * @inheritDoc
   */
  @Override
  public int size() {
    return helper(this);
  }

  public int helper(BST<keyType> t) {
    if (t == null || t.key == null) {
      return 0;
    } else {
      return 1 + helper(t.leftChild) + helper(t.rightChild);
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public boolean isEmpty() {
    if (key == null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public Iterator<keyType> iterator() {
    return this.toList().iterator();
  }

  public List<keyType> toList() {
    this.keys = new ArrayList<>();
    traversal(this);
    return this.keys;
  }

  public void traversal(BST<keyType> t) {
    if (t != null && t.key != null) {
      traversal(t.leftChild);
      this.keys.add(t.key);
      traversal(t.rightChild);
    }
  }

  /**
   * @inheritDoc
   * @implNote You do not need to implement this for the homework.
   */
  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  /**
   * @inheritDoc
   * @implNote You do not need to implement this for the homework.
   */
  @Override
  public <T> T[] toArray(T[] a) {
    return null;
  }

  /**
   * @inheritDoc
   * @implNote You do not need to implement this for the homework.
   */
  @Override
  public boolean remove(Object o) {
    return false;
  }

  /**
   * @inheritDoc
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    return this.toList().containsAll(c);
  }

  /**
   * @inheritDoc
   */
  @Override
  public boolean addAll(Collection<? extends keyType> c) {
    if (this.containsAll(c)) {
      return false;
    } else {
      for (keyType k : c) {
        if (!contains(k)) {
          add(k);
        }
      }
      return true;
    }
  }

  /**
   * @inheritDoc
   * @implNote You do not need to implement this for the homework.
   */
  @Override
  public boolean retainAll(Collection<?> c) {
    return false;
  }

  /**
   * @inheritDoc
   * @implNote You do not need to implement this for the homework.
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    return false;
  }

  /**
   * @inheritDoc
   * @implNote You do not need to implement this for the homework.
   */
  @Override
  public void clear() {

  }
}
