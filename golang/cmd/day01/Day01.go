package main

import (
	"bytes"
	"fmt"
	"golang.org/x/exp/maps"
	"nutgaard/aoc/internal/utils"
	"strings"
)

var digitMap = make(map[string]rune)

func main() {
	testInput1 := utils.ReadFile("cmd/day01/Part01_test.txt")
	testInput2 := utils.ReadFile("cmd/day01/Part02_test.txt")
	input := utils.ReadFile("cmd/day01/Input.txt")

	digitMap["one"] = '1'
	digitMap["two"] = '2'
	digitMap["three"] = '3'
	digitMap["four"] = '4'
	digitMap["five"] = '5'
	digitMap["six"] = '6'
	digitMap["seven"] = '7'
	digitMap["eight"] = '8'
	digitMap["nine"] = '9'

	test := decodeDigits("zoneight234")
	fmt.Println(test)

	utils.Verify(testInput1, 142, part1)
	utils.Verify(testInput2, 281, part2)

	utils.Run("part1", part1, input)
	utils.Run("part2", part2, input)
}

func part1(input string) int {
	lines := strings.Split(input, "\n")
	sum := 0
	for _, line := range lines {
		sum += calibrationValue(line)
	}
	return sum
}
func part2(input string) int {
	lines := strings.Split(input, "\n")
	sum := 0
	for _, line := range lines {
		sum += calibrationValue(decodeDigits(line))
	}
	return sum
}

func isDigit(u rune) bool {
	return u >= '0' && u <= '9'
}

func calibrationValue(line string) int {
	n := len(line)
	v := 0
	for i := 0; i < n; i++ {
		ch := rune(line[i])
		if isDigit(ch) {
			v = 10 * int(ch-'0')
			break
		}
	}

	for i := 0; i < n; i++ {
		ch := rune(line[n-1-i])
		if isDigit(ch) {
			v += int(ch - '0')
			break
		}
	}

	return v
}

func decodeDigits(input string) string {
	var b bytes.Buffer
	keys := maps.Keys(digitMap)

	for i := 0; i < len(input); i++ {
		ch := rune(input[i])
		if isDigit(ch) {
			b.WriteRune(ch)
		} else {
			for _, key := range keys {
				if strings.HasPrefix(input[i:], key) {
					b.WriteRune(digitMap[key])
				}
			}
		}
	}

	return b.String()
}
