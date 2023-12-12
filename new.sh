#!/usr/bin/env sh

read -p "Enter day: " day
echo "Prepping files for $day"

rm -r "src/day$day"
cp -r "src/dayXX" "src/day$day"

sed -i "" "s/XX/$day/g" "src/day$day/DayXX.kt"

mv "src/day$day/DayXX.kt" "src/day$day/Day$day.kt"