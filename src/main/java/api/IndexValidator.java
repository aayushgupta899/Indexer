package api;

import index.InvertedIndex;
import index.PostingList;

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
        invertedIndexFromFile.load(compress, null);
        System.out.println("Terms in index from file: "+sourceInvertedIndex.getIndex().size());
        if(this.checkIfIndicesAreIdentical(sourceInvertedIndex, invertedIndexFromFile))
        {
            System.out.println("Indices are the same!");
        }
        else
        {
            System.out.println("Indices don't match!");
        }
    }

    /**
     * Checks if two indices are identical or not
     * @param index1
     * @param index2
     * @return
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

}
