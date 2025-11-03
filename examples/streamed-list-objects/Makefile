.PHONY: all build run run-openfga

all: build

openfga_version=latest

build:
	./gradlew build

run:
	./gradlew run

run-openfga:
	docker pull docker.io/openfga/openfga:${openfga_version} && \
		docker run -p 8080:8080 docker.io/openfga/openfga:${openfga_version} run
