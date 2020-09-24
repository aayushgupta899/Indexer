package api;

import index.InvertedIndex;
import index.Posting;
import index.PostingList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains methods to compute DICE coefficient
 */
public class DiceCalculator {

    private static final String OUTPUT_FILE_NAME = "QueryTerms1400_test.txt";

    /**
     * @param args filename compress
     */
    public static void main(String[] args) {
        DiceCalculator diceCalculator = new DiceCalculator();
        String filename = args[0];
        boolean compress = Boolean.parseBoolean(args[1]);
        System.out.println("Starting the dice term generation process with the following arguments:");
        System.out.println("Input file name: "+filename);
        System.out.println("Is Index Compressed: "+compress);
        System.out.println("Output Query File name: "+OUTPUT_FILE_NAME);
        System.out.println("*******************************");
        diceCalculator.diceHelper(filename, compress);
        System.out.println("Query terms generated successfully using DICE!");
        System.out.println("*******************************");
    }

    /**
     * Helper function to compute Dice
     * @param filename The name of the input file
     * @param compress Whether the index is compressed or not
     */
    public void diceHelper(String filename, boolean compress){
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.load(compress);
        invertedIndex.getQueryPostings(compress, null);
        try(BufferedReader reader=  new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            PrintWriter diceWriter = new PrintWriter(OUTPUT_FILE_NAME, "UTF-8");) {
            Set<String> vocabulary = invertedIndex.getVocabulary();
            String query = reader.readLine();
            while (query != null) {
                String[] queryTerms = query.split("\\s+");
                List<String> addedTerms = new ArrayList<>();
                for(int i=0; i<queryTerms.length; i++) {
                    double bestScore = 0;
                    String bestTerm = "";
                    for (String term : vocabulary) {
                        double diceScore = this.computeDice(invertedIndex, queryTerms[i], term);
                        if(diceScore > bestScore) {
                            bestScore = diceScore;
                            bestTerm = term;
                        }
                    }
                    addedTerms.add(bestTerm);
                }
                diceWriter.print(query);
                for(String term : addedTerms) {
                    diceWriter.print(" "+term);
                }
                diceWriter.println();
                query = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes the DICE Coefficient
     * @param invertedIndex The Inverted Index
     * @param termA The first term for DICE computation
     * @param termB The second term for DICE computation
     * @return The DICE score
     */
    public double computeDice(InvertedIndex invertedIndex, String termA, String termB)
    {
        PostingList listA = invertedIndex.getPostings(termA);
        PostingList listB = invertedIndex.getPostings(termB);
        int nA = invertedIndex.getTermFrequency(termA);
        int nB = invertedIndex.getTermFrequency(termB);
        double nAB = 0.0;
        for(int i=0; i<listA.getPostings().size(); i++)
        {
            Posting a = listA.getPostings().get(i);
            Posting b = null;
            for(int j=0; j<listB.getPostings().size(); j++)
            {
                if(listB.getPostings().get(j).getDocID() == a.getDocID())
                {
                    b = listB.getPostings().get(j);
                    break;
                }
            }
            if(b != null && b.getDocID() == a.getDocID())
            {
                Set<Integer> aPos = new HashSet<>(a.getPositions());
                Set<Integer> bPos = new HashSet<>(b.getPositions());
                for(int pos : aPos) {
                    if(bPos.contains(pos+1)) {
                        nAB++;
                    }
                }
            }
        }
        return nAB / (nA + nB);
    }
}
