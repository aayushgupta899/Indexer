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
This project has 5 runnable apps:
1. `src/api/IndexBuilder`: This is used to build the inverted index. The command line arguments for this are:
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
2. `src/api/IndexValidator`: This is used to validate whether the index written on disk is identical to the index parsed from JSON. The command line arguments for this are:
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
3. `src/api/QueryRetriever`: This is used to retrieve query results from an index built using `src/api/IndexBuilder`. The input queries are stored in a file, in which each line contains a set of terms separated by whitespace. It prints the time taken to perform the retrieval on the output screen, which can be used to compare the query retrieval times for different indices. This takes the following command line arguments:
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
4. `src/api/QueryGenerator`: This is used to generate the query terms. It generates 7 terms on each line of the file, which are separated by whitespace, and produces 100 such lines. These are stored on disk. It takes the following command line parameters:
    * `compress`: Whether the inverted index is compressed or not (true/false).
    
    To run the app, the following command is required:
    ```
    ./gradlew -P mainClass=api.QueryGenerator execute --args='<compress>' 
    ```
    An example would be:
    ```
    ./gradlew -P mainClass=api.QueryGenerator execute --args='true' 
    ```
5. `src/api/DiceCalculator`: This is used to calculate the DICE coefficient for each of the query terms and return the term corresponding to the maximum DICE score, which is written to file, along with the original query terms. It takes the following command line parameters:
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

