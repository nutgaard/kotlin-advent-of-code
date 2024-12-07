#!/usr/bin/env sh

read -p "Enter year: " year
read -p "Enter day: " day
echo "Prepping files for $year - $day"

rm -rf "src/year$year/day$day"
cp -r "src/dayXX" "src/year$year/day$day"

sed -i "" "s/YY/$year/g" "src/year$year/day$day/DayXX.kt"
sed -i "" "s/XX/$day/g" "src/year$year/day$day/DayXX.kt"

mv "src/year$year/day$day/DayXX.kt" "src/year$year/day$day/Day$day.kt"