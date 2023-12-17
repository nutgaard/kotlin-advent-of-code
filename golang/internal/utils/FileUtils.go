package utils

import "os"

func ReadFile(path string) string {
	file, err := os.ReadFile(path)
	Check(err)

	return string(file)
}
