all: build

openfga_version=latest

build:
	./gradlew build

run:
	./gradlew run

run-with-agent:
	./gradlew runWithAgent

run-openfga:
	docker pull docker.io/openfga/openfga:${openfga_version} && \
		docker run -p 8080:8080 docker.io/openfga/openfga:${openfga_version} run
