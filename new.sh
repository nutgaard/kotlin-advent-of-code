#!/usr/bin/env sh

# Prompt user for input
read -r -p "Enter year: " year
read -r -p "Enter day: " day
echo "Prepping files for $year - $day"

# Remove and recreate directory
rm -rf "src/main/kotlin/year$year/day$day"
echo "Copying from src/main/kotlin/dayXX to src/main/kotlin/year$year"
mkdir -p "src/main/kotlin/year$year"
cp -r "src/main/kotlin/dayXX" "src/main/kotlin/year$year/day$day"

# Verify file existence
if [ ! -f "src/main/kotlin/year$year/day$day/DayXX.kt" ]; then
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
sed -i '' "s/YY/$year/g" src/main/kotlin/year$year/day$day/DayXX.kt
sed -i '' "s/XX/$day/g" src/main/kotlin/year$year/day$day/DayXX.kt

# Rename the file
mv "src/main/kotlin/year$year/day$day/DayXX.kt" "src/main/kotlin/year$year/day$day/Day$day.kt"

echo "Preparation complete: src/main/kotlin/year$year/day$day/Day$day.kt"
