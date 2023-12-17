package utils

import "fmt"

func Verify[I, T comparable](input I, expected T, fn func(I) T) {
	actual := fn(input)
	if expected != actual {
		panic(format(expected, actual))
	}
}

func format[I comparable](expected, actual I) string {
	switch valueType := any(expected).(type) {
	case string:
		return fmt.Sprintf("Expected %s, but got %s", valueType, actual)
	case int:
		return fmt.Sprintf("Expected %d, but got %d", valueType, actual)
	default:
		panic(fmt.Sprintf("Unknown value type: %s", valueType))
	}
}
