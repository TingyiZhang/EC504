package edu.bu.ec504.hw2p2;

public class Main {

  public static void main(String[] args) {
    HashFamily hashes = new simpleHashFamily(10,1000);
    BloomFilter bf = new BloomFilter(hashes);

    for (int ii=0; ii<10; ii++)
      bf.insert(ii);
    for (int ii=0; ii<2000; ii++)
      if (bf.inFilter(ii))
        System.out.println(ii);
  }
}
