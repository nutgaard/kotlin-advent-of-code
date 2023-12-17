package utils

import (
	"fmt"
	"time"
)

func Run[I, T comparable](name string, fn func(I) T, input I) {
	start := time.Now()
	result := fn(input)
	elapsed := time.Since(start)
	fmt.Println(fmt.Sprintf("[%s] Result: %d Time: %s", name, result, elapsed))
}
