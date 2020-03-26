package edu.bu.ec504.hw2p2;

/**
 * A family of hash functions.
 */
public abstract class HashFamily {

  // CONSTRUCTORS
  /**
   * Constructs a family of <code>numhashes</code> hash functions, each mapping
   * a given input onto integers in the range 0..<code>range</code>-1.
   *
   * @param numHashes The maximum number of hashes in the family.
   * @param range Each hash maps input to the range 0..<code>range</code>-1
   */
  public HashFamily(int numHashes, int range) {
    myNumHashes = numHashes;
    myRange = range;
  }

  // METHODS
  // ... INFO
  public int getNumHashes() { return myNumHashes; }
  public int getRange() { return myRange; }

  // ... ACTION
  /**
   * Applies a requested hash function from this family to a specified parameter.
   *
   * @param index The index of the hash function to apply.  Must be between 0 and {@link #myNumHashes}-1 inclusive.
   * @param elem The element to hash.
   * @return The evaluation of the <code>index</code>th hash function in the family on element <code>elem</code>.
   */
  public abstract int apply(int index, int elem);

  // FIELDS
  private int myNumHashes; // the number of hashes associated with this family
  private int myRange;       // hashes in this family map to 0...range-1
}
