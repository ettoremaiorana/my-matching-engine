# my-matching-engine

- the application requires maven and Java 13 to compile and run.

- to run without recompiling, open up a terminal and execute the following command:
 java -jar --enable-preview target\engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar

- to compile from source, the command is the following:
mvn clean compile test assembly:single

a couple of notes about the implementation:
- I did not aim to extreme performance. The application creates object on the heap, uses String, prints to system,out etc. etc. 

What I don't like about this implementation:
- Some patterns are sub-optimal, such as lines 51 to 53 of the class MatchinEngine.
- There's some unit testing but not enough in my opinion, especially on how to deal with Iceberg orders. 

Things that I like:
- I believe it's flexible enough to be extended and reasonably easy to read.
