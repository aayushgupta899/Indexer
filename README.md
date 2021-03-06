# Indexer - Aayush Gupta
## Overview
This project defines a simple Indexer which creates an inverted index and stores it in a file on disk. It can also compress the index using VByte Compression and Delta Encoding, and has APIs for retrieval, validation, and computing the DICE Coefficient.
## Build System
The project uses the build system `gradle`. Usually, we need to install gradle in the system to run gradle commands, but here, I have provided a `gradle_wrapper` JAR file, which can build gradle projects without installing gradle on the system. The JAR file can be found in the `gradle` directory. Some important files required for the `gradle` build:
* `build.gradle` - Contains the dependencies and other parameters
* `settings.gradle` - Contains the settings
* `gradlew` - A shell script which is used to build and run the project. This uses a wrapper around the gradle library, which is provided as a JAR  with this project. Therefore, there is no need to install gradle on your system.
* `gradle.bat` - A binary file used with the gradle wrapper
* `gradle/` - This directory contains the gradle wrapper JAR file.
## Downloading the dependencies
The dependencies for this project are managed using `gradle`, and are defined in a `build.gradle` file, which is provided with this project. As such, there is no need to download the dependencies manually, it will be done using `gradle`.
## Building the code
Use the following command to build the code:
```
chmod +x gradlew
./gradlew build
```
## Running the code
This project has 6 runnable apps:
1. `api/IndexBuilder`: This is used to build the inverted index. The command line arguments for this are:
    * `filename` - The absolute path of the input JSON file (in this case - Shakespeare plays).
    * `compress` - Whether to compress the index or not (true/false).  
 
   To run the app, the following command is required:
   ```
   ./gradlew -P mainClass=api.IndexBuilder execute --args='<file_path> <compress>' 
   ```
   An example would be:
   ```
   ./gradlew -P mainClass=api.IndexBuilder execute --args='/Users/aayushgupta/Downloads/shakespeare-scenes.json true' 
   ```
2. `api/IndexValidator`: This is used to validate whether the index written on disk is identical to the index parsed from JSON. The command line arguments for this are:
    * `JSON_filename` - The absolute path for the JSON file which was used to create the original index.
    * `compress` - Whether the index is compressed or not (true/false)
    
    To run the app, the following command is required:
    ```
    ./gradlew -P mainClass=api.IndexValidator execute --args='<JSON_file_path> <compress>' 
    ```
    An example would be:
    ```
    ./gradlew -P mainClass=api.IndexValidator execute --args='/Users/aayushgupta/Downloads/shakespeare-scenes.json true' 
    ```
3. `api/QueryRetriever`: This is used to retrieve query results from an index built using `src/api/IndexBuilder`. The input queries are stored in a file, in which each line contains a set of terms separated by whitespace. It prints the time taken to perform the retrieval on the output screen, which can be used to compare the query retrieval times for different indices. It also saves the output from the following retrieval models into a file:
    * Vector Space Model with `log TF` and `log IDF` weights. Saved in `vs.trecrun`.
    * BM25 model with `k1 = 1.5`, `k2 = 500` and `b = 0.75`. Saved in `bm25.trecrun`.
    * Query Likelihood Model with Jelinik-Mercer smoothing and `lambda = 0.2`. Saved in `ql-jm.trecrun`.
    * Query Likelihood Model with Dirchlet smoothing and `mu = 1200`. Saved in `ql-dir.trecrun`.
    
    This takes the following command line arguments:
    * `filename` - This is the absolute/relative path of the file containing the queries. The relative path should be relative to the root of the project.
    * `compress` - Whether the inverted index to query from is compressed or not (true/false).
    * `k` - The number of results to return.
    
    To run the app, the following command is required:
    ```
    ./gradlew -P mainClass=api.QueryRetriever execute --args='<filename> <compress> <k>' 
    ```
    An example would be:
    ```
    ./gradlew -P mainClass=api.QueryRetriever execute --args='Queries_700.txt true 4' 
    ```
4. `api/QueryGenerator`: This is used to generate the query terms. It generates 7 terms on each line of the file, which are separated by whitespace, and produces 100 such lines. These are stored on disk. It takes the following command line parameters:
    * `compress`: Whether the inverted index is compressed or not (true/false).
    
    To run the app, the following command is required:
    ```
    ./gradlew -P mainClass=api.QueryGenerator execute --args='<compress>' 
    ```
    An example would be:
    ```
    ./gradlew -P mainClass=api.QueryGenerator execute --args='true' 
    ```
5. `api/DiceCalculator`: This is used to calculate the DICE coefficient for each of the query terms and return the term corresponding to the maximum DICE score, which is written to file, along with the original query terms. It takes the following command line parameters:
    * `filename` - The absolute/relative path of the file which has the input queries. The terms should be in separate lines, and each term in a line should be separated by a whitespace character.
    * `compress` - Whether the inverted index is compressed or not (true/false).
    
    To run the app, the following command is required:
    ```
    ./gradlew -P mainClass=api.DiceCalculator execute --args='<filename> <compress>' 
    ```
    An example would be:
    ```
    ./gradlew -P mainClass=api.DiceCalculator execute --args='Queries_700.txt true' 
    ```
6. `api/StatisticsCalculator`: This is used for calculating certain statistics, such as the shortest scene, longest scene, average length of a scene, shortest play etc. It takes the following command line parameters:
    * `compress` - Whether the inverted index is compressed or not (true/false).
    
    To run the app, the following command is required:
    ```
    ./gradlew -P mainClass=api.StatisticsCalculator execute --args='<compress>' 
    ```
    An example would be:
    ```
    ./gradlew -P mainClass=api.StatisticsCalculator execute --args='true' 
    ```
7.  `inferencenetwork/TestInferenceNetwork`: This is used to run the inference network with the following operators:
     * Ordered Window, with the window size of 1 (exact phrase)
     * Unordered Window, with window size 3 * size of the query
     * SUM
     * AND
     * OR
     * MAX
     
     All of the operators are scored with Query Likelihood model, with Dirchlet smoothing, with the value of mu = 1500.
     It takes the following parameters:
     * `k`: The number of results to return
     * `compressed`: Whether to use the compressed index or not
     * `queryFile`: The name of the file containing the query
     
     To run the app, the following command is required:
     ```
     ./gradlew -P mainClass=inferencenetwork.TestInferenceNetwork execute --args='<k> <compressed> <queryFile>' 
     ```
     An example would be:
     ```
     ./gradlew -P mainClass=inferencenetwork.TestInferenceNetwork execute --args='10 true queries.txt' 
     ```
8.  `api/OnlineCluster`: This is used to create the document clusters, given the index compression and the linkage. It creates files for thresholds ranging from 0.05 to 0.95.
    The following are the supported linkages:
    * SINGLE_LINK
    * COMPLETE_LINK
    * AVERAGE_LINK
    * MEAN_LINK
    
    To run the app, the following command is required:
     ```
     ./gradlew -P mainClass=api.OnlineCluster execute --args='<k> <compressed> <linkage>' 
     ```
     An example would be:
     ```
     ./gradlew -P mainClass=api.OnlineCluster execute --args='true MEAN_LINK' 
     ```
9.  `api/GeneratePriors`: This is used to create the priors using `uniform` and `random`, and writes them to a file on disk.

     To run the app, the following command is required:
      ```
      ./gradlew -P mainClass=api.GeneratePriors execute --args='<compressed>' 
      ```
      An example would be:
      ```
      ./gradlew -P mainClass=api.GeneratePriors execute --args='true' 
      ```
10.   `api/RunPriorQueries`: This is used to run the prior queries using the arguments provided.

       To run the app, the following command is required:
      ```
      ./gradlew -P mainClass=api.RunPriorQueries execute --args='<priorFileName> <compressed> <k>' 
      ```
      An example would be:
      ```
      ./gradlew -P mainClass=api.RunPriorQueries execute --args='uniform.prior true 10' 
      ```
## Troubleshooting
1. The gradle commands require JAVA_HOME environment variable to be correctly set in the system.
2. If the above mentioned commands fail to build the code, please use IntelliJ to import the project. It will import the project correctly, and install the dependencies. After that, the gradle commands can be used, or the files can be run using IntelliJ.

