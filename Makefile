.PHONY: help build test test-integration lint fmt clean all

# Default target when 'make' is run without arguments
help:
	@echo "Available targets:"
	@echo "  build            - Compile the project"
	@echo "  test             - Run unit tests"
	@echo "  test-integration - Run integration tests"
	@echo "  lint             - Run static analysis and checkstyle"
	@echo "  fmt              - Auto-format code using Spotless"
	@echo "  clean            - Clean build artifacts"
	@echo "  all              - Run all tests and lint"

# Compile the project
build:
	./gradlew assemble

# Run unit tests
test:
	./gradlew test

# Run integration tests
test-integration:
	./gradlew test-integration

# Run static analysis and checkstyle
lint:
	./gradlew check

# Auto-format code using Spotless
fmt:
	./gradlew spotlessApply

# Clean build artifacts
clean:
	./gradlew clean

# Run all tests and lint
all: test lint
