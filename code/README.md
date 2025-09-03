# FeatureRef - A Refactoring Recommendation Tool

## Overview

FeatureRef is a Java tool for recommending sequences of refactoring based on the combination of static analysis and search-based algorithms.
The refactorings aim at removing recurrent design problems. Currently, FeatureRef supports only the removal of Concern Overload problems.

## Building

We use gradle as our dependency and build management tool. Thus, to build FeatureRef, please use the following gradle command:

`gradle distZip`

It will generate a zip file containing the all the jar files (lib folder) and shell scripts for Windows (.bat) and unix based OSs.

## Running

In its current version, FeatureRef a graphical user interface, which means that it can only be called in the terminal.
It accepts the following arguments:

```

usage: organic
 -alg,--optimization-algorithm <algorithm>   Identifier of the selected
                                             search-based algorithm.
                                             Available Options: [SA,
                                             NSGAII, NSGAIII, RANDOM]
 -cmf,--concerns-model-dir <file>            Path to the directory
                                             containing the concern model
                                             file (topics-model.gz) and
                                             the inferencer file
                                             (inferencer.mallet)
 -con,--context-strategy <strategy>          Identifier of the desired
                                             context selection strategy.
                                             Available Options: [SMELLS,
                                             LOCAL_CHANGE]
 -cs,--cooling-schedule <schedule>           Cooling schedule for the
                                             Simulated Annealing
                                             algorithm. Available Options:
                                             [EXPONENTIAL, LINEAR, BOLTZ]
 -mev,--max-evaluations <evaluations>        Maximum number of evaluations
                                             for the optimization
                                             algorithm
 -out,--output-folder <folder>               Folder for saving the
                                             resulting output
 -ps,--population-size <size>                Size of the initial
                                             population (only for global
                                             search algorithms)
 -src,--source-folder <folder>               Folder containing all files
                                             to be processed
 -tm,--topic-model-mode                      Run the tool only for
                                             creating a topic model based
                                             on files of source folder.
                                             Will save the topic model in
                                             the output directory.


```

Only the src and out options are mandatory.

### Running Examples

Run FeatureRef with the Simulated Annealing algorithm:
```
organic -src /project -out /results -alg SA -cmf /concerns-model-folder -cs EXPONENTIAL
```

Run FeatureRef in the topic modeling mode to build the concerns inference model:
```
organic -src /project -out /results -tm
```

## Target Design Problems

### Concern Overload

### Scattered Concern

## Symptoms

### Traditional Symptoms

- God Class
- Feature Envy
- Complex Class

### Feature-Based Symptoms

- Feature Concentration
- Feature Dispersion
