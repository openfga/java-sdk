# Changelog

## v0.3.2

### [0.3.2](https://github.com/openfga/java-sdk/compare/v0.3.1...v0.3.2) (2024-01-26)

- fix: fix token validity check for expiry (#48)
- fix: send OAuth2 credentials request as form-urlencoded post (#47) - thanks @le-yams
- fix: do not create new http client on every request (#46)

## v0.3.1

### [0.3.1](https://github.com/openfga/java-sdk/compare/v0.3.0...v0.3.1) (2024-01-22)

- feat: oauth2 client credentials support - thanks @le-yams
- fix: add context to ClientCheckRequest
- fix incorrect check for whether transactionChunkSize is not set

## v0.3.0

### [0.3.0](https://github.com/openfga/java-sdk/compare/v0.2.3...v0.3.0) (2023-12-13)

- feat: support for [conditions](https://openfga.dev/blog/conditional-tuples-announcement)
- feat: standard OpenFGA headers have been added to Write, BatchCheck, and ListRelations calls
- feat: apiTokenIssuer has been expanded to support arbitrary http and https URLs. previously it supported
  only configuring a hostname - thanks @le-yams
- feat: allow setting and overriding http headers
- [BREAKING] chore!: use latest API interfaces
- chore: dependency updates
- refactor: abstract common functionality; update validation and exception types

## v0.2.3

### [0.2.3](https://github.com/openfga/java-sdk/compare/v0.2.2...v0.2.3) (2023-11-21)

- feat(client): implement batchCheck, listRelations, and non-transaction write
- fix(client): adds missing "contextual tuples" field to check request

## v0.2.2

### [0.2.2](https://github.com/openfga/java-sdk/compare/v0.2.1...v0.2.2) (2023-10-31)

- fix(client): an empty read request will no longer send an empty tuple
- fix(client): an unused "user" field, and related methods, was removed from ClientExpandRequest

## v0.2.1

### [0.2.1](https://github.com/openfga/java-sdk/compare/v0.2.0...v0.2.1) (2023-10-13)

No changes, this patch release is just to test release automation.

## v0.2.0

### [0.2.0](https://github.com/openfga/java-sdk/compare/v0.1.0...v0.2.0) (2023-10-11)

- feat(client): automatic retries for errors have been implemented. HTTP 429 and HTTP 5XX error responses
  will automatically be retried. (With the exception of the HTTP 501 "Not Implemented" status code.)
- feat(client): new response error classes have been introduced to classify FGA error responses
- feat(client): response types have been enriched with HTTP status/header/body response data
- feat(client): response errors have been enriched with data from both the HTTP request and its repsonse 
- [BREAKING] refactor(client): in the lower level OpenFgaApi class, api calls and api calls "...WithHttpInfo"
  are collapsed into a single api call that always includes HTTP information.

## v0.1.0

### [0.1.0](https://github.com/openfga/java-sdk/compare/v0.0.5...v0.1.0) (2023-09-27)

- [BREAKING] refactor(client): simplify OpenFgaClient and OpenFgaApi constructors to not require
  an ApiClient. This is a breaking change as it changed the ordering of parameters in constructors.
- [BREAKING] refactor(client): all options classes for OpenFgaClient are now consistently prefixed
  with "Client"
- chore(client): add ClientReadAssertionsOptions and ClientWriteAssertionsOptions for their
  respective Client APIs.

## v0.0.5

### [0.0.5](https://github.com/openfga/java-sdk/compare/v0.0.4...v0.0.5) (2023-09-27)

- feat(client): add `OpenFgaClient` wrapping `OpenFgaApi` and exposing a simplified interface.
  See [docs](https://github.com/openfga/java-sdk?tab=readme-ov-file#initializing-the-api-client)
- chore(docs): update the README with installation and usage instructions.

## v0.0.3, v0.0.4

### [0.0.4](https://github.com/openfga/java-sdk/compare/v0.0.2...v0.0.4) (2023-09-21)

- fix: publishing to maven central

## v0.0.2

### [0.0.2](https://github.com/openfga/java-sdk/compare/v0.0.1...v0.0.2) (2023-09-15)

The Maven Group ID was updated to `dev.openfga`.

## v0.0.1

### [0.0.1](https://github.com/openfga/java-sdk/releases/tag/v0.0.1) (2023-09-14)

This is an initial beta release. While it can be used to call an FGA server, it lacks conveniences
already present in other OpenFGA SDKs and already planned.

Most notably it lacks a higher-level client (work is in progress), which will be the recommended
entry point.
