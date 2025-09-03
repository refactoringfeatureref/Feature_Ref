# [FeatureRef: Towards Effective Refactoring of Features] Validation and Replication results

## SUMMARY

Software projects are commonly composed by multiple features, which represent functional and non-functional requirements of the project. Each feature should be properly modularized in the projects design. However, some design decisions may result in Design Problems (DPs) caused by the incorrect modularization of features. An example of such DPs is the Scattered Feature, which occurs when a functionality is scattered in multiple non-cohesive elements. When DPs are not properly handled, the project may become difficult to maintain and evolve. Therefore, developers need to remove existing DPs through refactorings. Nevertheless, the inherent complexity of DPs removal may considerably hamper developers in applying the most effective sequences of refactorings. Thus, in this paper, we propose the FeatureRef technique. FeatureRef is intended to help developers in spotting and refactoring feature-related DPs. The DPs are detected through data extracted from the projects design and source code. FeatureRef uses a topic modeling algorithm for finding existing features in the project. Then, FeatureRef combines features information with internal quality measures and code smells to find DPs. For creating refactoring recommendations, FeatureRef relies on a new refactoring recommendation strategy, which combines refactoring heuristics with search-based optimization. We evaluated FeatureRef with an empirical study involving open-source projects. Our results show that, when compared to a baseline, FeatureRef significantly improves the design quality of refactored elements.

## General

This repository contains a package of artifacts for the paper entitled "FeatureRef: Towards Effective Refactoring of Features". The files are organized as follows:

- results
    - raw-data: results in json files.
    - graphs: graphs generated for analyzing the results.
    - manual-evaluation: artifacts used for the manual evaluation.
    - Quality Impact Analysis - Final Data.xlsx: a spreadsheet summaring the results.
- code: source code of the FeatureRef tool.
- models: lda topic models generated for the target projects of this work. 




