Team: Franz Kuntke, Johannes Filter

NaiveBayesClassifier
====================

Naive Bayes Classifier for data set at http://archive.ics.uci.edu/ml/datasets/Car+Evaluation

Basic Idea
----------
1. Read Data from Files
2. Shuffle Data
3. Take first 2/3 of data as training data
4. Take last 1/3 as test data
5. Build up Classifier
6. Test against test data and save error

* Repeat Steps 2. to 6. and sum up error


Evaluation
----------
* The classifier classifies about 85% correctly. The error is about 15%.
* Especially 'good' was classified bad.

