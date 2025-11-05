# Streamed List Objects example for OpenFGA's Java SDK

This example demonstrates working with the `POST` `/stores/:id/streamed-list-objects` endpoint in OpenFGA using the Java SDK.

## Prerequisites

If you do not already have an OpenFGA instance running, you can start one using the following command:

```bash
make run-openfga
```

Or directly with docker:

```bash
docker run -d -p 8080:8080 openfga/openfga run
```

## Configure the example

You may need to configure the example for your environment by setting environment variables:

```bash
export FGA_API_URL=http://localhost:8080
```

Optional authentication configuration:
```bash
export FGA_CLIENT_ID=your-client-id
export FGA_CLIENT_SECRET=your-client-secret
export FGA_API_AUDIENCE=your-api-audience
export FGA_API_TOKEN_ISSUER=your-token-issuer
```

## Running the example

Build the project:

```bash
make build
```

Run the example:

```bash
make run
```

This will:
1. Create a temporary store
2. Create an authorization model
3. Write 100 mock tuples
4. Stream all objects using the `streamedListObjects` API
5. Display each object as it's received
6. Clean up the temporary store

## What to expect

The example will output each object as it's streamed from the server:

```
Created temporary store (01HXXX...)
Created temporary authorization model (01GXXX...)
Writing 100 mock tuples to store.
Listing objects using streaming endpoint:
  document:0
  document:1
  document:2
  ...
  document:99
API returned 100 objects.
Deleted temporary store (01HXXX...)
Finished.
```

## Note

The streaming API is particularly useful when dealing with large result sets, as it:
- Reduces memory usage by processing objects one at a time
- Provides faster time-to-first-result
- Allows for real-time processing of results
- Is only limited by execution timeout rather than result set size