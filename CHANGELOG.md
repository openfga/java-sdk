# Changelog

## [Unreleased](https://github.com/openfga/java-sdk/compare/v0.9.2...HEAD)
- feat: Add `streamedListObjects` API endpoint with consumer callback support (#252)

## v0.9.2

### [0.9.2](https://github.com/openfga/java-sdk/compare/v0.9.1...v0.9.2) (2025-10-23)

### Added

- Add support for write conflict options (#234)
  - The SDK now supports setting a `onDuplicate` for writing tuples (`ClientWriteOptions` or `ClientWriteTuplesOptions`) and `onMissing` (`ClientWriteOptions` or `ClientDeleteTuplesOptions`) for deleting tuples. See the [documentation](https://github.com/openfga/java-sdk#conflict-options-for-write-operations) for more details.
- Add support for `name` filter on `ListStores` (#237)
  - Thanks to @Oscmage and @varkart for their work on this!

## v0.9.1

### [0.9.1](https://github.com/openfga/java-sdk/compare/v0.9.0...v0.9.1) (2025-10-07)

### Fixed

- Override `defaultHeaders` in `ClientConfiguration` to return correct type when using method (#226)
- Correctly handle options with no modelID set in `readAuthorizationModel` (#226)
- Include headers when converting from `ClientListRelationsOptions` to `ClientBatchCheckOptions` (#226)

## v0.9.0

### [0.9.0](https://github.com/openfga/java-sdk/compare/v0.8.3...v0.9.0) (2025-08-15)

### Added
- RFC 9110 compliant `Retry-After` header support with exponential backoff and jitter
- `Retry-After` header value exposed in error objects for better observability
- `FgaError` now exposes `Retry-After` header value via `getRetryAfterHeader()` method

### Changed
- Enhanced retry strategy with delay calculation
- **BREAKING**: Maximum allowable retry count is now enforced at 15 (default remains 3)
- **BREAKING**: Configuration.minimumRetryDelay() now requires non-null values and validates input, throwing IllegalArgumentException for null or negative values

**Migration Guide**: 
- Update error handling code if using FgaError properties - new getRetryAfterHeader() method available
- Note: Maximum allowable retries is now enforced at 15 (validation added to prevent exceeding this limit)
- **IMPORTANT**: Configuration.minimumRetryDelay() now requires non-null values and validates input - ensure you're not passing null or negative Duration values, as this will now throw IllegalArgumentException. Previously null values were silently accepted and would fall back to default behavior at runtime.

### Fixed
- Fixed issue where telemetry metrics are not being exported correctly [#590](https://github.com/openfga/sdk-generator/pull/590)
- Fixed issue with non-transactional write error handling [https://github.com/openfga/sdk-generator/pull/573](https://github.com/openfga/sdk-generator/pull/573) 

## v0.8.3

### [0.8.3](https://github.com/openfga/java-sdk/compare/v0.8.2...v0.8.3) (2025-07-15)

Fixed:
- client: fix connectTimeout config (#182)
- client: fix batchCheck error handling (#183)

## v0.8.2

### [0.8.2](https://github.com/openfga/java-sdk/compare/v0.8.1...v0.8.2) (2025-07-02)

Added:
- client: allow accessing the internal api client via `getApi` (#178)

Fixed:
- client: fix BatchCheck ignoring passed in model ID override  (#177)

## v0.8.1

### [0.8.1](https://github.com/openfga/java-sdk/compare/v0.8.0...v0.8.1) (2025-02-18)

- fix: use HTTP 1.1 by default (#148)
- fix: ensure default telemetry attributes are sent (#145)
- feat: add batch check telemetry attribute (#143)
 
## v0.8.0

### [0.8.0](https://github.com/openfga/java-sdk/compare/v0.7.2...v0.8.0) (2025-02-07)

- feat!: add support for server-side [`batchCheck`](https://openfga.dev/docs/interacting/relationship-queries#batch-check) method (#141) - thanks @piotrooo!!
  This is a more efficient way to check on multiple tuples than calling the existing client-side `batchCheck`. Using this method requires an OpenFGA [v1.8.0+](https://github.com/openfga/openfga/releases/tag/v1.8.0) server.
    The existing `batchCheck` method has been renamed to `clientBatchCheck`.
    The existing `BatchCheckResponse` has been renamed to `ClientBatchCheckResponse`.
- feat: add support for `start_time` parameter in `ReadChanges` endpoint (#137)

BREAKING CHANGES:
- Usage of the existing `batchCheck` method should now use the `clientBatchCheck` method.

## v0.7.2

### [0.7.2](https://github.com/openfga/java-sdk/compare/v0.7.1...v0.7.2) (2024-12-18)

- fix: Ensure executor is shutdown (#133)

## v0.7.1

### [0.7.1](https://github.com/openfga/java-sdk/compare/v0.7.0...v0.7.1) (2024-09-23)

- refactor(OpenTelemetry): remove SDK version from meter name
- fix(OpenTelemetry): `http.request.method` should be enabled by default (#114)
- chore(deps): update dependencies  (#110, #111, #112)
- docs(OpenTelemetry): update Metrics and Attributes tables (#115)

## v0.7.0

### [0.7.0](https://github.com/openfga/java-sdk/compare/v0.6.1...v0.7.0) (2024-08-28)

- feat: support consistency parameter [\#107](https://github.com/openfga/java-sdk/pull/107)
Note: To use this feature, you need to be running OpenFGA v1.5.7+ with the experimental flag `enable-consistency-params` enabled. 
See the [v1.5.7 release notes](https://github.com/openfga/openfga/releases/tag/v1.5.7) for details.

## v0.6.1

- fix: Maven build issue

## v0.6.0

- feat: support [OpenTelemetry metrics reporting](https://github.com/openfga/java-sdk/blob/main/docs/OpenTelemetry.md) [\#94](https://github.com/openfga/java-sdk/pull/94) [\#95](https://github.com/openfga/java-sdk/pull/95)
- chore: update dependencies [\#100](https://github.com/openfga/java-sdk/pull/100) [\#101](https://github.com/openfga/java-sdk/pull/100) [\#102](https://github.com/openfga/java-sdk/pull/102) [\#103](https://github.com/openfga/java-sdk/pull/103)

## v0.5.0

### [0.5.0](https://github.com/openfga/java-sdk/compare/v0.4.0...v0.5.0) (2024-06-14)

- chore!: remove excluded users from ListUsers response

BREAKING CHANGE:

This version removes `getExcludedUsers` and `setExcludedUsers` from the `ListUsersResponse` and `ClientListUsersResponse` classes,
for more details see the [associated API change](https://github.com/openfga/api/pull/171).

## v0.4.2

### [0.4.2](https://github.com/openfga/java-sdk/compare/v0.4.1...v0.4.2) (2024-05-02)

- feat: support the [ListUsers](https://github.com/openfga/rfcs/blob/main/20231214-listUsers-api.md) endpoint (#80)
- fix: improve check for validity of token (#76)

## v0.4.1

### [0.4.1](https://github.com/openfga/java-sdk/compare/v0.4.0...v0.4.1) (2024-04-09)

- feat: support setting context on ListObjects - thanks @Didier-SimpleCommeDev
- feat: support setting context and contextual tuples on ListRelations
- feat: add retries to OAuth2 Client Credentials request
- feat: support modular models metadata
- fix: avoid clone of object mapper - thanks @paulosuzart

## v0.4.0

### [0.4.0](https://github.com/openfga/java-sdk/compare/v0.3.2...v0.4.0) (2024-03-04)

- fix!: reverse the transaction behaviour when `disableTransactions` is set on `Write`
  ⚠️ This is a behavioral breaking change!
  Previously, the `OpenFgaClient` reversed the behavior of write transactions based on the `disableTransactions` flag. This has been fixed so that batched writes are sent if `disableTransactions == true` and a single transactional write if it is false (default).

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
