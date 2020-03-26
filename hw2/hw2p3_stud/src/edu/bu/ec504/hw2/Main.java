package edu.bu.ec504.hw2;

import edu.bu.ec504.hw2.bst.BST;
import edu.bu.ec504.hw2.bst.BST.BSTRotation;
import java.util.ArrayList;
import java.util.Arrays;

class Main {

    public static void main(String[] args) {
        ArrayList<Integer> intElements = new ArrayList<>(Arrays.asList(3,1,4,5,9,2,6));
        ArrayList<String> strElements = new ArrayList<>(Arrays.asList("Hi","Aardvark","Buy","me"));
        BST<Integer> intTest = new BST<>(intElements);
        BST<String> strTest = new BST<>(strElements);

        BST<Integer>.searchRecord intResult;
        System.out.println("Your int BST is:\n"+intTest);
        System.out.println("... rightChild subtree:\n"+intTest.getRightChild());
        intResult = intTest.extendedContains(0);
        System.out.println("... 0 is in your BST? " + intResult.getSearchResult()+" in "+intResult.getSearchCount()+" steps;\n "+intResult.getLastSeen());
        intResult = intTest.extendedContains(6);
        System.out.println("... 6 is in your BST? " + intResult.getSearchResult()+" in "+intResult.getSearchCount()+" steps;\n "+intResult.getLastSeen());
        System.out.println("Your string BST is:\n"+strTest);
        System.out.println("One rotation: "+ new BSTRotation<>(3, BSTRotation.RotationType.ZAG));

    }
}
