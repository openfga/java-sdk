# ListStores Name Filter Implementation Patch

## Overview
This patch implements support for the optional `name` parameter in `ListStores` functionality for the OpenFGA Java SDK, addressing issue [#157](https://github.com/openfga/java-sdk/issues/157).

## Feature Description
Adds the ability to filter stores by name when calling `listStores()`, supporting the API functionality introduced in [openfga/api#211](https://github.com/openfga/api/pull/211).

## Patch Files
- `listStores-name-filter.patch` - Git diff of all changes
- `listStores-name-filter-staged.patch` - Staged changes ready for commit

## Usage Examples
```java
// Filter by name only
ClientListStoresOptions options = new ClientListStoresOptions().name("my-store");
ClientListStoresResponse response = client.listStores(options).get();

// Combine with pagination
ClientListStoresOptions options = new ClientListStoresOptions()
    .name("my-store")
    .pageSize(10)
    .continuationToken("token");
```

## Changes Made

### 1. ClientListStoresOptions.java
- Added `name` field with getter/setter methods
- Follows existing pattern for optional parameters

### 2. OpenFgaApi.java  
- Extended method signatures to include `name` parameter
- Maintained backward compatibility with overloaded methods
- Updated `pathWithParams` call to include name parameter
- Added comprehensive JavaDoc documentation

### 3. OpenFgaClient.java
- Updated client to pass `name` parameter from options to API
- Uses existing options pattern consistently

### 4. Test Coverage
- **Unit Tests**: Added tests for name filter scenarios
- **Integration Tests**: Added tests for both API and Client levels
- **Combined Parameters**: Tests for name + pagination parameters
- **Backward Compatibility**: Verified existing tests still pass

## Technical Implementation Details

### Backward Compatibility
✅ All existing `listStores()` method signatures remain unchanged
✅ Existing code continues to work without modification  
✅ New functionality is purely additive

### Parameter Handling
- Uses existing `pathWithParams` utility for query parameter construction
- Null values are handled gracefully (filtered out automatically)
- Follows project conventions for optional parameters

### Code Quality
- Passes all existing tests
- Passes Spotless code formatting checks
- Follows existing code patterns and conventions
- Comprehensive test coverage added

## Testing Results
```bash
./gradlew test     # All tests pass ✅
./gradlew build    # Clean build ✅
./gradlew spotlessCheck  # Code formatting ✅
```

## Files Modified
- `src/main/java/dev/openfga/sdk/api/configuration/ClientListStoresOptions.java`
- `src/main/java/dev/openfga/sdk/api/OpenFgaApi.java`
- `src/main/java/dev/openfga/sdk/api/client/OpenFgaClient.java`
- `src/test/java/dev/openfga/sdk/api/client/OpenFgaClientTest.java`
- `src/test-integration/java/dev/openfga/sdk/api/OpenFgaApiIntegrationTest.java`
- `src/test-integration/java/dev/openfga/sdk/api/client/OpenFgaClientIntegrationTest.java`

## Application Instructions

### Apply the Patch
```bash
# Apply the patch to your OpenFGA Java SDK repository
git apply listStores-name-filter.patch

# Or if you have staged the files:
git apply listStores-name-filter-staged.patch

# Verify the changes
git status
git diff

# Run tests to verify
./gradlew test
```

### Create PR
```bash
# Stage and commit the changes
git add .
git commit -m "Add name filter support to ListStores

- Add name parameter to ClientListStoresOptions  
- Extend OpenFgaApi.listStores() method signatures
- Update OpenFgaClient to pass name parameter
- Add comprehensive unit and integration tests
- Maintain full backward compatibility

Resolves #157"

# Push to your fork
git push origin main
```

## Related Issues
- Resolves: [openfga/java-sdk#157](https://github.com/openfga/java-sdk/issues/157)
- Related: [openfga/sdk-generator#517](https://github.com/openfga/sdk-generator/issues/517)
- Implements: [openfga/api#211](https://github.com/openfga/api/pull/211)

## Implementation Notes
This implementation provides the Java SDK portion of the broader SDK generator issue. The changes follow the exact patterns used by other optional parameters in the SDK and maintain full backward compatibility.

Generated on: $(date)