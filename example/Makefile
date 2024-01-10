all: build

project_name=example1
openfga_version=latest
language=java

build:
	cd "${project_name}" && \
		./gradlew -P language=$(language) build

run:
	cd "${project_name}" && \
		./gradlew -P language=$(language) run

run-openfga:
	docker pull docker.io/openfga/openfga:${openfga_version} && \
		docker run -p 8080:8080 docker.io/openfga/openfga:${openfga_version} run
