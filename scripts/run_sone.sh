#! /bin/bash

echo "NAME TIMES : $1 $2..."

echo "Safe Version SOne.."
echo "Running SafeSearch $1 $2 iterations..."
echo "Run$3"
for i in $(eval echo {0..$2})
do
  echo "Running $1 $2 $i experiment..."
  java -jar ../build/libs/SafeSearch-1.0-SNAPSHOT.jar -v -l -s -1 < ../input/vehicle/vehicle$i.v > ../results/sone/500x500-easy/run$3/$i.results
done

echo "Finished!"
