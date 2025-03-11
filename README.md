# Multi-Language Code Benchmark
The benchmark application provides a user-friendly interface to compare the performance of code written in three programming languages: C++, C#, and Java. It measures key performance metrics such as execution time for a selected set of codes.

## Key Features
### Algorithm Selection:
 - Users can choose from predefined basic operations to run the benchmark, such as : **Memory Allocation**, **Memory Access**, **Thread Creation**, **Thread Context Switch**, **Thread Context Migration**.

![Screenshot 2025-03-11 214507](https://github.com/user-attachments/assets/666e63c3-c786-4e56-a451-140e1d647ebc)

### Language Comparison:
- The application executes the same algorithm implemented in **C++**, **C#**, and **Java**, measuring performance metrics in real time.

### Save results and load results:
- The user can choose to save the results, and also he ca load previous saved results. The results are saved in a JSON file format.

![Screenshot 2025-03-11 214519](https://github.com/user-attachments/assets/9a402b8c-4fb8-4290-86eb-8eb53d14f660)


### Graphical Results:
Results are displayed as graphical plots using JavaFX Chart API.

![Screenshot 2025-03-11 214552](https://github.com/user-attachments/assets/44f73bc0-595f-4620-b84f-9bc673c5eb56)


### Interactive Interface:
The JavaFX UI allows users to:

* Select the algorithm to benchmark.</br>
* Set input sizes.</br>
* View real-time results and performance comparisons.</br>

#### Code Execution:

The JavaFX application invokes external executables (.exe for C++, .dll for C#, and .jar for Java) using the ProcessBuilder in Java.
Output from each executable is captured and processed to compute the benchmarks.

## Implementation Overview
### Languages:

* Java and JavaFX for the GUI.</br>
* Java, C++, and C# for the benchmarking algorithms.</br>
