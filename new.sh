#!/usr/bin/env sh

# Prompt user for input
read -r -p "Enter year: " year
read -r -p "Enter day: " day
echo "Prepping files for $year - $day"

# Remove and recreate directory
rm -rf "src/year$year/day$day"
echo "Copying from src/dayXX to src/year$year/day$day"
ls src/dayXX
cp -r "src/dayXX" "src/year$year/day$day"

# Verify file existence
if [ ! -f "src/year$year/day$day/DayXX.kt" ]; then
  echo "Error: src/year$year/day$day/DayXX.kt does not exist. Please check your src/dayXX template."
  exit 1
fi

# Determine the appropriate sed option for in-place editing
if [ "$(uname)" = "Darwin" ]; then
  SEDOPTION="-i ''"
else
  SEDOPTION="-i"
fi

# Replace placeholders in the file
sed $SEDOPTION "s/YY/$year/g" "src/year$year/day$day/DayXX.kt"
sed $SEDOPTION "s/XX/$day/g" "src/year$year/day$day/DayXX.kt"

# Rename the file
mv "src/year$year/day$day/DayXX.kt" "src/year$year/day$day/Day$day.kt"

echo "Preparation complete: src/year$year/day$day/Day$day.kt"
