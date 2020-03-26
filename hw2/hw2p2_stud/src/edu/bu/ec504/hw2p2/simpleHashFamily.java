package edu.bu.ec504.hw2p2;

public class simpleHashFamily extends HashFamily {

  /**
   * @inheritDoc
   */
  public simpleHashFamily(int numHashes, int range) {
    super(numHashes, range);
  }

  /**
   * @inheritDoc
   */
  @Override
  public int apply(int index, int elem) {
    if (index<0 || index>getNumHashes())
      throw new IndexOutOfBoundsException();

    return elem%getRange();
  }
}
