## Examples of using the OpenFGA Java SDK

A set of Examples on how to call the OpenFGA Java SDK

### Examples
Example 1:
A bare-bones example. It creates a store, and runs a set of calls against it including creating a model, writing tuples and checking for access.
This example is implemented in both Java and Kotlin.


### Running the Examples

Prerequisites:
- `docker`
- `make`
- A Java Runtime Environment (JRE)

#### Run using a published SDK

Steps
1. Clone/Copy the example folder
2. Run `make` to build the project
3. If you have an OpenFGA server running, you can use it, otherwise run `make run-openfga` to spin up an instance (you'll need to switch to a different terminal after - don't forget to close it when done)
4. Run `make run` to run the example

#### Run using a local unpublished SDK build

Steps
1. Build the SDK
2. In the Example project file (e.g. `build.gradle`), comment out the part that specifies the remote SDK, e.g.
```groovy
dependencies {
    implementation("dev.openfga:openfga-sdk:0.3.+")

    // ...etc
}
```
and replace it with one pointing to the local gradle project, e.g.
```groovy
dependencies {
    // implementation("dev.openfga:openfga-sdk:0.3.+")
    implementation project(path: ':')

    // ...etc
}
```
3. Run `make` to build the project
4. If you have an OpenFGA server running, you can use it, otherwise run `make run-openfga` to spin up an instance (you'll need to switch to a different terminal after - don't forget to close it when done)
5. Run `make run` to run the example
