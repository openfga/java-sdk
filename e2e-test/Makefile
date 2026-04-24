.PHONY: help up down restart build run test clean logs status

# SDK jar — resolved dynamically from the parent build output
SDK_JAR := $(wildcard ../build/libs/openfga-sdk-*.jar)
BUILD_DIR := /tmp/e2e-build
GRADLE_CACHE := $(HOME)/.gradle/caches/modules-2/files-2.1

# Dependency versions (must match ../build.gradle)
JACKSON_VER     := 2.21.2
JACKSON_ANN_VER := 2.21
NULLABLE_VER    := 0.2.10
JSR305_VER      := 3.0.2
OTEL_VER        := 1.61.0

# Resolve jars from Gradle cache
DEP_JARS := $(shell find $(GRADLE_CACHE) -name "*.jar" 2>/dev/null | \
	grep -E "jackson-core-$(JACKSON_VER)|jackson-databind-$(JACKSON_VER)|jackson-annotations-$(JACKSON_ANN_VER)\b|jackson-datatype-jsr310-$(JACKSON_VER)|jackson-databind-nullable-$(NULLABLE_VER)|jsr305-$(JSR305_VER)|opentelemetry-api-$(OTEL_VER)|opentelemetry-context-$(OTEL_VER)" | \
	sort -u | paste -sd: -)

COMPILE_CP := $(SDK_JAR):$(DEP_JARS)
RUN_CP     := $(BUILD_DIR):$(COMPILE_CP)

# Default target
help:
	@echo ""
	@echo "  E2E Streaming Auth Test — openfga/java-sdk#330"
	@echo "  ─────────────────────────────────────────────────"
	@echo ""
	@echo "  Targets:"
	@echo "    up       - Start Docker containers (OpenFGA + mock OAuth2)"
	@echo "    down     - Stop and remove Docker containers"
	@echo "    restart  - Recreate Docker containers from scratch"
	@echo "    status   - Show container status"
	@echo "    logs     - Tail container logs"
	@echo ""
	@echo "    build    - Build the SDK jar and compile the E2E test"
	@echo "    run      - Compile (if needed) and run the E2E test"
	@echo "    test     - Full end-to-end: up → build → run → report"
	@echo ""
	@echo "    clean    - Remove compiled E2E classes"
	@echo ""

# ── Docker ──────────────────────────────────────────────────

up:
	@echo "Starting Docker containers..."
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	@sleep 3
	@curl -sf http://localhost:9090/default/.well-known/openid-configuration > /dev/null && \
		echo "  ✅ mock-oauth2 ready" || echo "  ⏳ mock-oauth2 not ready yet"
	@curl -sf -o /dev/null -w "" http://localhost:8082/healthz && \
		echo "  ✅ openfga-preshared ready" || echo "  ⏳ openfga-preshared not ready yet"
	@curl -sf -o /dev/null -w "" http://localhost:8080/healthz && \
		echo "  ✅ openfga-oidc ready" || echo "  ⏳ openfga-oidc not ready yet"

down:
	docker-compose down

restart: down up

status:
	docker-compose ps

logs:
	docker-compose logs -f --tail=50

# ── Build & Run ─────────────────────────────────────────────

# Build the SDK jar (delegates to parent Gradle)
../build/libs/openfga-sdk-%.jar:
	@echo "Building SDK jar..."
	cd .. && ./gradlew jar

build: ../build/libs/openfga-sdk-*.jar
	@mkdir -p $(BUILD_DIR)
	@echo "Compiling E2EStreamingAuthTest..."
	javac -cp "$(COMPILE_CP)" -d $(BUILD_DIR) E2EStreamingAuthTest.java 2>&1 | \
		grep -v "^warning:" || true
	@echo "  ✅ Compiled"

run: build
	@echo ""
	java -cp "$(RUN_CP)" dev.openfga.sdk.e2e.E2EStreamingAuthTest

# Full lifecycle: start containers, build, run tests
test: up build
	@echo ""
	@java -cp "$(RUN_CP)" dev.openfga.sdk.e2e.E2EStreamingAuthTest; \
		EXIT=$$?; \
		echo ""; \
		if [ $$EXIT -eq 0 ]; then \
			echo "🎉 All E2E tests passed!"; \
		else \
			echo "💥 Some E2E tests failed (exit $$EXIT)"; \
		fi; \
		exit $$EXIT

clean:
	rm -rf $(BUILD_DIR)

