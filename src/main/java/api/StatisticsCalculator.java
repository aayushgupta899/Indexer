package api;

import index.InvertedIndex;

import java.util.*;

/**
 * Calculates a few statistics for the inverted index
 */
public class StatisticsCalculator {

    /**
     * @param args compress
     */
    public static void main(String[] args) {
        boolean compress = Boolean.parseBoolean(args[0]);
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.load(compress);
        invertedIndex.getInvertedIndex(compress, null);
        StatisticsCalculator statisticsCalculator = new StatisticsCalculator();
        double averageLengthOfScene = statisticsCalculator.getAverageLengthOfScene(invertedIndex);
        String shortestScene = statisticsCalculator.getShortestScene(invertedIndex);
        String longestScene = statisticsCalculator.getLongestScene(invertedIndex);
        double averageLengthOfPlay = statisticsCalculator.getAverageLengthOfPlay(invertedIndex);
        String shortestPlay = statisticsCalculator.getShortestPlay(invertedIndex);
        String longestPlay = statisticsCalculator.getLongestPlay(invertedIndex);
        System.out.println("*******************************");
        System.out.println("The average length of scene is: "+averageLengthOfScene);
        System.out.println("*******************************");
        System.out.println("The shortest scene is: "+shortestScene);
        System.out.println("*******************************");
        System.out.println("The longest scene is: "+longestScene);
        System.out.println("*******************************");
        System.out.println("The average length of play is: "+averageLengthOfPlay);
        System.out.println("*******************************");
        System.out.println("The shortest play is: "+shortestPlay);
        System.out.println("*******************************");
        System.out.println("The longest play is: "+longestPlay);
        System.out.println("*******************************");

    }

    /**
     * Gets the average length of a scene
     * @param invertedIndex The inverted index
     * @return The average length of a scene
     */
    public double getAverageLengthOfScene(InvertedIndex invertedIndex) {
        return invertedIndex.getAverageDocLength();
    }

    /**
     * Gets the shortest scene in the dataset
     * @param invertedIndex The inverted index
     * @return The shortest scene
     */
    public String getShortestScene(InvertedIndex invertedIndex){
        int min = Integer.MAX_VALUE;
        int minDocID = 0;
        for(Map.Entry<Integer, Integer> entry : invertedIndex.getDocLengthMap().entrySet())
        {
            if(entry.getValue() < min)
            {
                min = entry.getValue();
                minDocID = entry.getKey();
            }
        }
        return invertedIndex.getDocName(minDocID);
    }

    /**
     * Gets the longest scene in the dataset
     * @param invertedIndex The inverted index
     * @return The longest scene
     */
    public String getLongestScene(InvertedIndex invertedIndex){
        int max = Integer.MIN_VALUE;
        int maxDocID = 0;
        for(Map.Entry<Integer, Integer> entry : invertedIndex.getDocLengthMap().entrySet()) {
            if(entry.getValue() > max) {
                max = entry.getValue();
                maxDocID = entry.getKey();
            }
        }
        return invertedIndex.getDocName(maxDocID);

    }

    /**
     * Gets the longest play in the dataset
     * @param invertedIndex The inverted index
     * @return The longest play in the dataset
     */
    public String getLongestPlay(InvertedIndex invertedIndex) {
        Map<String, Integer> playCount = new HashMap<>();
        int maxCount = Integer.MIN_VALUE;
        String longestPlay = "";
        for(int docID : invertedIndex.getPlayIDMap().keySet())
        {
            String play = invertedIndex.getPlayIDMap().get(docID);
            int sceneLength = invertedIndex.getDocLength(docID);
            playCount.putIfAbsent(play, 0);
            playCount.put(play, playCount.get(play)+sceneLength);
        }
        for(Map.Entry<String, Integer> entry : playCount.entrySet())
        {
            String play = entry.getKey();
            if(playCount.get(play) > maxCount) {
                longestPlay = play;
                maxCount = playCount.get(play);
            }
        }
        return longestPlay;
    }

    /**
     * Gets the shortest play in the dataset
     * @param invertedIndex The inverted index
     * @return The shortest play
     */
    public String getShortestPlay(InvertedIndex invertedIndex) {
        Map<String, Integer> playCount = new HashMap<>();
        int minCount = Integer.MAX_VALUE;
        String shortestPlay = "";
        for(int docID : invertedIndex.getPlayIDMap().keySet())
        {
            String play = invertedIndex.getPlayIDMap().get(docID);
            int sceneLength = invertedIndex.getDocLength(docID);
            playCount.putIfAbsent(play, 0);
            playCount.put(play, playCount.get(play)+sceneLength);
        }
        for(Map.Entry<String, Integer> entry : playCount.entrySet())
        {
            String play = entry.getKey();
            if(playCount.get(play) < minCount) {
                shortestPlay = play;
                minCount = playCount.get(play);
            }
        }
        return shortestPlay;
    }

    /**
     * Gets the average length of a play in the dataset
     * @param invertedIndex The inverted index
     * @return The average length of a play
     */
    public double getAverageLengthOfPlay(InvertedIndex invertedIndex) {
        Set<String> plays = new HashSet<>();
        double sum = 0;
        for(int docID : invertedIndex.getPlayIDMap().keySet()) {
            String play = invertedIndex.getPlayIDMap().get(docID);
            int sceneLength = invertedIndex.getDocLength(docID);
            plays.add(play);
            sum += sceneLength;
        }
        return sum / plays.size();
    }
}
