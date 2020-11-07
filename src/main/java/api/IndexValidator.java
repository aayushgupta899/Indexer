package api;

import index.InvertedIndex;
import index.PostingList;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines methods to check if two inverted indices are identical or not
 */
public class IndexValidator {

    /**
     * @param args JSON_filename compress
     */
    public static void main(String[] args) {

        String sourceJSONFileName = args[0];
        boolean compress = Boolean.parseBoolean(args[1]);
        System.out.println("Starting the index validation process with the following arguments:");
        System.out.println("Source JSON file: "+sourceJSONFileName);
        System.out.println("Is Index Compressed: "+compress);
        System.out.println("*******************************");
        IndexValidator validator = new IndexValidator();
        validator.indexValidatorHelper(sourceJSONFileName, compress);
        System.out.println("*******************************");
    }

    /**
     * Helper method for validating index
     * @param sourceJSONFileName The JSON file which contains the data
     * @param compress Whether to check with the compressed index or not
     */
    public void indexValidatorHelper(String sourceJSONFileName, boolean compress) {
        IndexBuilder sourceIndexBuilder = new IndexBuilder();
        sourceIndexBuilder.buildIndex(sourceJSONFileName);
        InvertedIndex sourceInvertedIndex = sourceIndexBuilder.getInvertedIndex();
        System.out.println("Terms in source: "+sourceInvertedIndex.getIndex().size());
        InvertedIndex invertedIndexFromFile = new InvertedIndex();
        invertedIndexFromFile.load(compress);
        invertedIndexFromFile.getQueryPostings(compress, null);
        System.out.println("Terms in index from file: "+sourceInvertedIndex.getIndex().size());
        if(this.checkIfIndicesAreIdentical(sourceInvertedIndex, invertedIndexFromFile))
        {
            System.out.println("Indices are the same!");
        }
        else
        {
            System.out.println("Indices don't match!");
        }
        Map<Integer, Map<String, Integer>> sourceDocTermMap = sourceInvertedIndex.getDocToTermMap();
        Map<Integer, Map<String, Integer>> fileDocTermMap = invertedIndexFromFile.getDocToTermMap();
        if(this.checkIfDocTermMapAreIdentical(sourceDocTermMap, fileDocTermMap)){
            System.out.println("Doc Term Maps are the same!");
        }
        else{
            System.out.println("Doc Term Maps don't match!");
        }
    }

    /**
     * Checks if two indices are identical or not
     * @param index1 The first index
     * @param index2 The second index
     * @return true/false
     */
    public boolean checkIfIndicesAreIdentical(InvertedIndex index1, InvertedIndex index2) {
        try {
            if(index1.getIndex().size() != index2.getIndex().size())
            {
                System.out.println("Length of indices unequal");
                return false;
            }
            for(Map.Entry<String, PostingList> entry : index1.getIndex().entrySet())
            {
                if(!index2.getIndex().containsKey(entry.getKey()))
                {
                    System.out.println("Key: "+entry.getKey()+" not in second key");
                    return false;
                }
                Integer[] postings1 = entry.getValue().toIntegerArray();
                Integer[] postings2 = index2.getPostings(entry.getKey()).toIntegerArray();
                if(postings1.length != postings2.length)
                {
                    System.out.println("Postings length for Key: "+entry.getKey()+" not same in second key");
                    return false;
                }
                for(int i=0; i<postings1.length; i++)
                {
                    if(!postings1[i].equals(postings2[i]))
                    {
                        System.out.println("Posting at pos: "+i+" not same for Key: "+entry.getKey());
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     *
     */
    public boolean checkIfDocTermMapAreIdentical(Map<Integer, Map<String, Integer>> map1, Map<Integer, Map<String, Integer>> map2){
        if(map1 == null || map2 == null){
            System.out.println("Null");
            return false;
        }
        if(map1.size() != map2.size()){
            System.out.println("Map sizes unequal");
            return false;
        }
        for(Map.Entry<Integer, Map<String, Integer>> entry : map1.entrySet()){
            Integer docID1 = entry.getKey();
            Map<String, Integer> termMap1 = entry.getValue();
            Map<String, Integer> termMap2 = map2.get(docID1);
            if(termMap1.size() != termMap2.size()){
                System.out.println("Size mismatch for docID "+ docID1);
                return false;
            }
            for(Map.Entry<String, Integer> termEntry : termMap1.entrySet()){
                String term = termEntry.getKey();
                int termCount1 = termEntry.getValue();
                int termCount2 = termMap2.get(term);
                if(termCount1 != termCount2){
                    System.out.println("Count mismatch for term: "+term+" , docID: "+docID1);
                    return false;
                }
            }
        }
        return true;
    }

}
