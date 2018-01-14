#!/bin/sh

# retrieve data from server
#javac retrieveData.java
#java retrieveData

#javac PrepData.java
#java PrepData

# shuffle the lines
shuf training.csv --output=training.csv

# split 95 5 training testing
VAR_1=$(cat training.csv | wc -l)
echo $VAR_1
TRAIN=$(expr 15 \* $VAR_1 / 20)
TEST=$(expr $VAR_1 - $TRAIN)
echo $TRAIN
echo $TEST
head -n $TRAIN training.csv > trainingset.csv
tail -n $TEST training.csv > testData.csv

#javac Calibrate.java
#java Calibrate
