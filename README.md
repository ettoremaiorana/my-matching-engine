# my-matching-engine

- the application requires maven and Java 13 to compile and run.

- to run without recompiling, open up a terminal and execute the following command:
 java -jar --enable-preview target\engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar

- to compile from source, the command is the following:
mvn clean compile test assembly:single

a couple of notes about the implementation:
- I did not aim to extreme performance. The application creates objects on the heap, uses String, prints to system,out etc. etc. 

What I don't like about this implementation:
- Some patterns are sub-optimal, such as lines 51 to 53 of class MatchinEngine.
- There's some unit testing but not enough in my opinion, especially on how to deal with Iceberg orders. 

Things that I like:
- I believe it's flexible enough to be extended and reasonably easy to read.

Entries sample:  
B,1111,99,50000  
B,3333,98,25500  
S,2222,100,10000  
S,4444,100,7500  
S,5555,101,20000  
B,6666,100,100000,10000  
S,2222,100,10000  
S,2222,1,11000  
B,7777,100,50000,20000  
S,2222,1,35000  
