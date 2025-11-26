.PHONY: build run run-openfga
all: build

project_name=.
openfga_version=latest
language=java

build:
	../../gradlew -P language=$(language) build

run:
	../../gradlew -P language=$(language) run

run-openfga:
	docker pull docker.io/openfga/openfga:${openfga_version} && \
		docker run -p 8080:8080 docker.io/openfga/openfga:${openfga_version} run