#! /bin/bash


echo "Running SafeSearch $1 25 iterations..."

for i in {0..25}
do
  echo "Running $1 $i experiment..."
  timeout 180 java -jar ../build/libs/SafeSearch-1.0-SNAPSHOT.jar -v -l -s < ../input/vehicle/vehicle$i.v > ../results/$1$i.results
done

echo "Finished!"
