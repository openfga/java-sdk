Testing EasyCLA

# Java SDK for OpenFGA

[![Release](https://img.shields.io/github/v/release/openfga/java-sdk?sort=semver&color=green)](https://github.com/openfga/java-sdk/releases)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](./LICENSE)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fopenfga%2Fjava-sdk.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fopenfga%2Fjava-sdk?ref=badge_shield)
[![Discord Server](https://img.shields.io/discord/759188666072825867?color=7289da&logo=discord "Discord Server")](https://discord.gg/8naAwJfWN6)
[![Twitter](https://img.shields.io/twitter/follow/openfga?color=%23179CF0&logo=twitter&style=flat-square "@openfga on Twitter")](https://twitter.com/openfga)

This is an autogenerated Java SDK for OpenFGA. It provides a wrapper around the [OpenFGA API definition](https://openfga.dev/api).

## Table of Contents

- [About OpenFGA](#about)
- [Resources](#resources)
- [Installation](#installation)
- [Getting Started](#getting-started)
  - [Initializing the API Client](#initializing-the-api-client)
  - [Get your Store ID](#get-your-store-id)
  - [Calling the API](#calling-the-api)
    - [Stores](#stores)
      - [List All Stores](#list-stores)
      - [Create a Store](#create-store)
      - [Get a Store](#get-store)
      - [Delete a Store](#delete-store)
    - [Authorization Models](#authorization-models)
      - [Read Authorization Models](#read-authorization-models)
      - [Write Authorization Model](#write-authorization-model)
      - [Read a Single Authorization Model](#read-a-single-authorization-model)
      - [Read the Latest Authorization Model](#read-the-latest-authorization-model)
    - [Relationship Tuples](#relationship-tuples)
      - [Read Relationship Tuple Changes (Watch)](#read-relationship-tuple-changes-watch)
      - [Read Relationship Tuples](#read-relationship-tuples)
      - [Write (Create and Delete) Relationship Tuples](#write-create-and-delete-relationship-tuples)
    - [Relationship Queries](#relationship-queries)
      - [Check](#check)
      - [Batch Check](#batch-check)
      - [Expand](#expand)
      - [List Objects](#list-objects)
      - [List Relations](#list-relations)
    - [Assertions](#assertions)
      - [Read Assertions](#read-assertions)
      - [Write Assertions](#write-assertions)
  - [API Endpoints](#api-endpoints)
  - [Models](#models)
- [Contributing](#contributing)
  - [Issues](#issues)
  - [Pull Requests](#pull-requests)
- [License](#license)

## About

[OpenFGA](https://openfga.dev) is an open source Fine-Grained Authorization solution inspired by [Google's Zanzibar paper](https://research.google/pubs/pub48190/). It was created by the FGA team at [Auth0](https://auth0.com) based on [Auth0 Fine-Grained Authorization (FGA)](https://fga.dev), available under [a permissive license (Apache-2)](https://github.com/openfga/rfcs/blob/main/LICENSE) and welcomes community contributions.

OpenFGA is designed to make it easy for application builders to model their permission layer, and to add and integrate fine-grained authorization into their applications. OpenFGA’s design is optimized for reliability and low latency at a high scale.


## Resources

- [OpenFGA Documentation](https://openfga.dev/docs)
- [OpenFGA API Documentation](https://openfga.dev/api/service)
- [Twitter](https://twitter.com/openfga)
- [OpenFGA Discord Community](https://discord.gg/8naAwJfWN6)
- [Zanzibar Academy](https://zanzibar.academy)
- [Google's Zanzibar Paper (2019)](https://research.google/pubs/pub48190/)

## Installation



## Getting Started

### Initializing the API Client

[Learn how to initialize your SDK](https://openfga.dev/docs/getting-started/setup-sdk-client)

#### No Credentials

```cjava
```

#### API Token

```java
```

#### Client Credentials

```java
```


### Get your Store ID

You need your store id to call the OpenFGA API (unless it is to call the [CreateStore](#create-store) or [ListStores](#list-stores) methods).

If your server is configured with [authentication enabled](https://openfga.dev/docs/getting-started/setup-openfga#configuring-authentication), you also need to have your credentials ready.

### Calling the API

#### Stores

##### List Stores

Get a paginated list of stores.

[API Documentation](https://openfga.dev/api/service/docs/api#/Stores/ListStores)

```java
```

##### Create Store

Initialize a store.

[API Documentation](https://openfga.dev/api/service/docs/api#/Stores/CreateStore)

```java
```

##### Get Store

Get information about the current store.

[API Documentation](https://openfga.dev/api/service/docs/api#/Stores/GetStore)

> Requires a client initialized with a storeId

```java
```

##### Delete Store

Delete a store.

[API Documentation](https://openfga.dev/api/service/docs/api#/Stores/DeleteStore)

> Requires a client initialized with a storeId

```java
```

#### Authorization Models

##### Read Authorization Models

Read all authorization models in the store.

[API Documentation](https://openfga.dev/api/service#/Authorization%20Models/ReadAuthorizationModels)

```java
```

##### Write Authorization Model

Create a new authorization model.

[API Documentation](https://openfga.dev/api/service#/Authorization%20Models/WriteAuthorizationModel)

> Note: To learn how to build your authorization model, check the Docs at https://openfga.dev/docs.

> Learn more about [the OpenFGA configuration language](https://openfga.dev/docs/configuration-language).

> You can use the OpenFGA [CLI](https://github.com/openfga/cli) or [Syntax Transformer](https://github.com/openfga/syntax-transformer) to convert between the OpenFGA DSL and the JSON authorization model.

```java
```

#### Read a Single Authorization Model

Read a particular authorization model.

[API Documentation](https://openfga.dev/api/service#/Authorization%20Models/ReadAuthorizationModel)

```java
```

##### Read the Latest Authorization Model

Reads the latest authorization model (note: this ignores the model id in configuration).

[API Documentation](https://openfga.dev/api/service#/Authorization%20Models/ReadAuthorizationModel)

```java
```

#### Relationship Tuples

##### Read Relationship Tuple Changes (Watch)

Reads the list of historical relationship tuple writes and deletes.

[API Documentation](https://openfga.dev/api/service#/Relationship%20Tuples/ReadChanges)

```java
```

##### Read Relationship Tuples

Reads the relationship tuples stored in the database. It does not evaluate nor exclude invalid tuples according to the authorization model.

[API Documentation](https://openfga.dev/api/service#/Relationship%20Tuples/Read)

```java
```

##### Write (Create and Delete) Relationship Tuples

Create and/or delete relationship tuples to update the system state.

[API Documentation](https://openfga.dev/api/service#/Relationship%20Tuples/Write)

###### Transaction mode (default)

By default, write runs in a transaction mode where any invalid operation (deleting a non-existing tuple, creating an existing tuple, one of the tuples was invalid) or a server error will fail the entire operation.

```java
```

Convenience `WriteTuples` and `DeleteTuples` methods are also available.

###### Non-transaction mode

The SDK will split the writes into separate requests and send them sequentially to avoid violating rate limits.

```java
```

#### Relationship Queries

##### Check

Check if a user has a particular relation with an object.

[API Documentation](https://openfga.dev/api/service#/Relationship%20Queries/Check)

```java
```

##### Batch Check

Run a set of [checks](#check). Batch Check will return `allowed: false` if it encounters an error, and will return the error in the body.
If 429s or 5xxs are encountered, the underlying check will retry up to 15 times before giving up.

```java
```

##### Expand

Expands the relationships in userset tree format.

[API Documentation](https://openfga.dev/api/service#/Relationship%20Queries/Expand)

```java
```

##### List Objects

List the objects of a particular type a user has access to.

[API Documentation](https://openfga.dev/api/service#/Relationship%20Queries/ListObjects)

```java
```

##### List Relations

List the relations a user has on an object.

```java
```

#### Assertions

##### Read Assertions

Read assertions for a particular authorization model.

[API Documentation](https://openfga.dev/api/service#/Assertions/Read%20Assertions)

```java
```

##### Write Assertions

Update the assertions for a particular authorization model.

[API Documentation](https://openfga.dev/api/service#/Assertions/Write%20Assertions)

```java
```


### API Endpoints



### Models


- [Any](https://github.com/openfga/java-sdk/blob/main/docs/Any.md)

- [Assertion](https://github.com/openfga/java-sdk/blob/main/docs/Assertion.md)

- [AuthorizationModel](https://github.com/openfga/java-sdk/blob/main/docs/AuthorizationModel.md)

- [CheckRequest](https://github.com/openfga/java-sdk/blob/main/docs/CheckRequest.md)

- [CheckResponse](https://github.com/openfga/java-sdk/blob/main/docs/CheckResponse.md)

- [Computed](https://github.com/openfga/java-sdk/blob/main/docs/Computed.md)

- [ContextualTupleKeys](https://github.com/openfga/java-sdk/blob/main/docs/ContextualTupleKeys.md)

- [CreateStoreRequest](https://github.com/openfga/java-sdk/blob/main/docs/CreateStoreRequest.md)

- [CreateStoreResponse](https://github.com/openfga/java-sdk/blob/main/docs/CreateStoreResponse.md)

- [Difference](https://github.com/openfga/java-sdk/blob/main/docs/Difference.md)

- [ErrorCode](https://github.com/openfga/java-sdk/blob/main/docs/ErrorCode.md)

- [ExpandRequest](https://github.com/openfga/java-sdk/blob/main/docs/ExpandRequest.md)

- [ExpandResponse](https://github.com/openfga/java-sdk/blob/main/docs/ExpandResponse.md)

- [GetStoreResponse](https://github.com/openfga/java-sdk/blob/main/docs/GetStoreResponse.md)

- [InternalErrorCode](https://github.com/openfga/java-sdk/blob/main/docs/InternalErrorCode.md)

- [InternalErrorMessageResponse](https://github.com/openfga/java-sdk/blob/main/docs/InternalErrorMessageResponse.md)

- [Leaf](https://github.com/openfga/java-sdk/blob/main/docs/Leaf.md)

- [ListObjectsRequest](https://github.com/openfga/java-sdk/blob/main/docs/ListObjectsRequest.md)

- [ListObjectsResponse](https://github.com/openfga/java-sdk/blob/main/docs/ListObjectsResponse.md)

- [ListStoresResponse](https://github.com/openfga/java-sdk/blob/main/docs/ListStoresResponse.md)

- [Metadata](https://github.com/openfga/java-sdk/blob/main/docs/Metadata.md)

- [Node](https://github.com/openfga/java-sdk/blob/main/docs/Node.md)

- [Nodes](https://github.com/openfga/java-sdk/blob/main/docs/Nodes.md)

- [NotFoundErrorCode](https://github.com/openfga/java-sdk/blob/main/docs/NotFoundErrorCode.md)

- [ObjectRelation](https://github.com/openfga/java-sdk/blob/main/docs/ObjectRelation.md)

- [PathUnknownErrorMessageResponse](https://github.com/openfga/java-sdk/blob/main/docs/PathUnknownErrorMessageResponse.md)

- [ReadAssertionsResponse](https://github.com/openfga/java-sdk/blob/main/docs/ReadAssertionsResponse.md)

- [ReadAuthorizationModelResponse](https://github.com/openfga/java-sdk/blob/main/docs/ReadAuthorizationModelResponse.md)

- [ReadAuthorizationModelsResponse](https://github.com/openfga/java-sdk/blob/main/docs/ReadAuthorizationModelsResponse.md)

- [ReadChangesResponse](https://github.com/openfga/java-sdk/blob/main/docs/ReadChangesResponse.md)

- [ReadRequest](https://github.com/openfga/java-sdk/blob/main/docs/ReadRequest.md)

- [ReadResponse](https://github.com/openfga/java-sdk/blob/main/docs/ReadResponse.md)

- [RelationMetadata](https://github.com/openfga/java-sdk/blob/main/docs/RelationMetadata.md)

- [RelationReference](https://github.com/openfga/java-sdk/blob/main/docs/RelationReference.md)

- [Status](https://github.com/openfga/java-sdk/blob/main/docs/Status.md)

- [Store](https://github.com/openfga/java-sdk/blob/main/docs/Store.md)

- [Tuple](https://github.com/openfga/java-sdk/blob/main/docs/Tuple.md)

- [TupleChange](https://github.com/openfga/java-sdk/blob/main/docs/TupleChange.md)

- [TupleKey](https://github.com/openfga/java-sdk/blob/main/docs/TupleKey.md)

- [TupleKeys](https://github.com/openfga/java-sdk/blob/main/docs/TupleKeys.md)

- [TupleOperation](https://github.com/openfga/java-sdk/blob/main/docs/TupleOperation.md)

- [TupleToUserset](https://github.com/openfga/java-sdk/blob/main/docs/TupleToUserset.md)

- [TypeDefinition](https://github.com/openfga/java-sdk/blob/main/docs/TypeDefinition.md)

- [Users](https://github.com/openfga/java-sdk/blob/main/docs/Users.md)

- [Userset](https://github.com/openfga/java-sdk/blob/main/docs/Userset.md)

- [UsersetTree](https://github.com/openfga/java-sdk/blob/main/docs/UsersetTree.md)

- [UsersetTreeDifference](https://github.com/openfga/java-sdk/blob/main/docs/UsersetTreeDifference.md)

- [UsersetTreeTupleToUserset](https://github.com/openfga/java-sdk/blob/main/docs/UsersetTreeTupleToUserset.md)

- [Usersets](https://github.com/openfga/java-sdk/blob/main/docs/Usersets.md)

- [ValidationErrorMessageResponse](https://github.com/openfga/java-sdk/blob/main/docs/ValidationErrorMessageResponse.md)

- [WriteAssertionsRequest](https://github.com/openfga/java-sdk/blob/main/docs/WriteAssertionsRequest.md)

- [WriteAuthorizationModelRequest](https://github.com/openfga/java-sdk/blob/main/docs/WriteAuthorizationModelRequest.md)

- [WriteAuthorizationModelResponse](https://github.com/openfga/java-sdk/blob/main/docs/WriteAuthorizationModelResponse.md)

- [WriteRequest](https://github.com/openfga/java-sdk/blob/main/docs/WriteRequest.md)



## Contributing

### Issues

If you have found a bug or if you have a feature request, please report them on the [sdk-generator repo](https://github.com/openfga/sdk-generator/issues) issues section. Please do not report security vulnerabilities on the public GitHub issue tracker.

### Pull Requests

All changes made to this repo will be overwritten on the next generation, so we kindly ask that you send all pull requests related to the SDKs to the [sdk-generator repo](https://github.com/openfga/sdk-generator) instead.

## Author

[OpenFGA](https://github.com/openfga)

## License

This project is licensed under the Apache-2.0 license. See the [LICENSE](https://github.com/openfga/java-sdk/blob/main/LICENSE) file for more info.

The code in this repo was auto generated by [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) from a template based on the [Java template](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator/src/main/resources/Java), licensed under the [Apache License 2.0](https://github.com/OpenAPITools/openapi-generator/blob/master/LICENSE).

