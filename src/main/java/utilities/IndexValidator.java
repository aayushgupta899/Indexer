package utilities;

import api.IndexBuilder;
import index.InvertedIndex;
import index.PostingList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IndexValidator {

    List<String> faultyKeys;

    public IndexValidator()
    {
        faultyKeys = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {

        String sourceJSONFileName = "/Users/aayushgupta/Downloads/shakespeare-scenes.json";
        IndexBuilder sourceIndexBuilder = new IndexBuilder();
        sourceIndexBuilder.buildIndex(sourceJSONFileName);
        InvertedIndex sourceInvertedIndex = sourceIndexBuilder.getInvertedIndex();
        System.out.println("Terms in source:"+sourceInvertedIndex.getIndex().size());
        InvertedIndex invertedIndexFromFile = new InvertedIndex();
        invertedIndexFromFile.load(true, null);
        IndexValidator validator = new IndexValidator();
        if(validator.checkIfIndicesAreEqual(sourceInvertedIndex, invertedIndexFromFile))
        {
            System.out.println("Indices are the same");
        }
        else
        {
            System.out.println("Indices don't match");
        }
    }

    public boolean checkIfIndicesAreEqual(InvertedIndex index1, InvertedIndex index2)
    {
        boolean flag = true;
        int count = 0;
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
                    flag = false;
                }
                Integer[] postings1 = entry.getValue().toIntegerArray();
                Integer[] postings2 = index2.getPostings(entry.getKey()).toIntegerArray();
                if(postings1.length != postings2.length)
                {
                    System.out.println("Postings length for Key: "+entry.getKey()+" not same in second key");
                    flag = false;
                }
                for(int i=0; i<postings1.length; i++)
                {
                    if(!postings1[i].equals(postings2[i]))
                    {
                        System.out.println("Posting at pos: "+i+" not same for Key: "+entry.getKey());
                        flag = false;
                    }
                }
                if(!flag)
                {
                    count++;
                    faultyKeys.add(entry.getKey());
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return count == 0;
    }

}
