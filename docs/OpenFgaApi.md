# OpenFgaApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**check**](OpenFgaApi.md#check) | **POST** /stores/{store_id}/check | Check whether a user is authorized to access an object |
| [**checkWithHttpInfo**](OpenFgaApi.md#checkWithHttpInfo) | **POST** /stores/{store_id}/check | Check whether a user is authorized to access an object |
| [**createStore**](OpenFgaApi.md#createStore) | **POST** /stores | Create a store |
| [**createStoreWithHttpInfo**](OpenFgaApi.md#createStoreWithHttpInfo) | **POST** /stores | Create a store |
| [**deleteStore**](OpenFgaApi.md#deleteStore) | **DELETE** /stores/{store_id} | Delete a store |
| [**deleteStoreWithHttpInfo**](OpenFgaApi.md#deleteStoreWithHttpInfo) | **DELETE** /stores/{store_id} | Delete a store |
| [**expand**](OpenFgaApi.md#expand) | **POST** /stores/{store_id}/expand | Expand all relationships in userset tree format, and following userset rewrite rules.  Useful to reason about and debug a certain relationship |
| [**expandWithHttpInfo**](OpenFgaApi.md#expandWithHttpInfo) | **POST** /stores/{store_id}/expand | Expand all relationships in userset tree format, and following userset rewrite rules.  Useful to reason about and debug a certain relationship |
| [**getStore**](OpenFgaApi.md#getStore) | **GET** /stores/{store_id} | Get a store |
| [**getStoreWithHttpInfo**](OpenFgaApi.md#getStoreWithHttpInfo) | **GET** /stores/{store_id} | Get a store |
| [**listObjects**](OpenFgaApi.md#listObjects) | **POST** /stores/{store_id}/list-objects | List all objects of the given type that the user has a relation with |
| [**listObjectsWithHttpInfo**](OpenFgaApi.md#listObjectsWithHttpInfo) | **POST** /stores/{store_id}/list-objects | List all objects of the given type that the user has a relation with |
| [**listStores**](OpenFgaApi.md#listStores) | **GET** /stores | List all stores |
| [**listStoresWithHttpInfo**](OpenFgaApi.md#listStoresWithHttpInfo) | **GET** /stores | List all stores |
| [**read**](OpenFgaApi.md#read) | **POST** /stores/{store_id}/read | Get tuples from the store that matches a query, without following userset rewrite rules |
| [**readWithHttpInfo**](OpenFgaApi.md#readWithHttpInfo) | **POST** /stores/{store_id}/read | Get tuples from the store that matches a query, without following userset rewrite rules |
| [**readAssertions**](OpenFgaApi.md#readAssertions) | **GET** /stores/{store_id}/assertions/{authorization_model_id} | Read assertions for an authorization model ID |
| [**readAssertionsWithHttpInfo**](OpenFgaApi.md#readAssertionsWithHttpInfo) | **GET** /stores/{store_id}/assertions/{authorization_model_id} | Read assertions for an authorization model ID |
| [**readAuthorizationModel**](OpenFgaApi.md#readAuthorizationModel) | **GET** /stores/{store_id}/authorization-models/{id} | Return a particular version of an authorization model |
| [**readAuthorizationModelWithHttpInfo**](OpenFgaApi.md#readAuthorizationModelWithHttpInfo) | **GET** /stores/{store_id}/authorization-models/{id} | Return a particular version of an authorization model |
| [**readAuthorizationModels**](OpenFgaApi.md#readAuthorizationModels) | **GET** /stores/{store_id}/authorization-models | Return all the authorization models for a particular store |
| [**readAuthorizationModelsWithHttpInfo**](OpenFgaApi.md#readAuthorizationModelsWithHttpInfo) | **GET** /stores/{store_id}/authorization-models | Return all the authorization models for a particular store |
| [**readChanges**](OpenFgaApi.md#readChanges) | **GET** /stores/{store_id}/changes | Return a list of all the tuple changes |
| [**readChangesWithHttpInfo**](OpenFgaApi.md#readChangesWithHttpInfo) | **GET** /stores/{store_id}/changes | Return a list of all the tuple changes |
| [**write**](OpenFgaApi.md#write) | **POST** /stores/{store_id}/write | Add or delete tuples from the store |
| [**writeWithHttpInfo**](OpenFgaApi.md#writeWithHttpInfo) | **POST** /stores/{store_id}/write | Add or delete tuples from the store |
| [**writeAssertions**](OpenFgaApi.md#writeAssertions) | **PUT** /stores/{store_id}/assertions/{authorization_model_id} | Upsert assertions for an authorization model ID |
| [**writeAssertionsWithHttpInfo**](OpenFgaApi.md#writeAssertionsWithHttpInfo) | **PUT** /stores/{store_id}/assertions/{authorization_model_id} | Upsert assertions for an authorization model ID |
| [**writeAuthorizationModel**](OpenFgaApi.md#writeAuthorizationModel) | **POST** /stores/{store_id}/authorization-models | Create a new authorization model |
| [**writeAuthorizationModelWithHttpInfo**](OpenFgaApi.md#writeAuthorizationModelWithHttpInfo) | **POST** /stores/{store_id}/authorization-models | Create a new authorization model |



## check

> CompletableFuture<CheckResponse> check(storeId, body)

Check whether a user is authorized to access an object

The Check API queries to check if the user has a certain relationship with an object in a certain store. A &#x60;contextual_tuples&#x60; object may also be included in the body of the request. This object contains one field &#x60;tuple_keys&#x60;, which is an array of tuple keys. You may also provide an &#x60;authorization_model_id&#x60; in the body. This will be used to assert that the input &#x60;tuple_key&#x60; is valid for the model specified. If not specified, the assertion will be made against the latest authorization model ID. It is strongly recommended to specify authorization model id for better performance. The response will return whether the relationship exists in the field &#x60;allowed&#x60;.  ## Example In order to check if user &#x60;user:anne&#x60; of type &#x60;user&#x60; has a &#x60;reader&#x60; relationship with object &#x60;document:2021-budget&#x60; given the following contextual tuple &#x60;&#x60;&#x60;json {   \&quot;user\&quot;: \&quot;user:anne\&quot;,   \&quot;relation\&quot;: \&quot;member\&quot;,   \&quot;object\&quot;: \&quot;time_slot:office_hours\&quot; } &#x60;&#x60;&#x60; the Check API can be used with the following request body: &#x60;&#x60;&#x60;json {   \&quot;tuple_key\&quot;: {     \&quot;user\&quot;: \&quot;user:anne\&quot;,     \&quot;relation\&quot;: \&quot;reader\&quot;,     \&quot;object\&quot;: \&quot;document:2021-budget\&quot;   },   \&quot;contextual_tuples\&quot;: {     \&quot;tuple_keys\&quot;: [       {         \&quot;user\&quot;: \&quot;user:anne\&quot;,         \&quot;relation\&quot;: \&quot;member\&quot;,         \&quot;object\&quot;: \&quot;time_slot:office_hours\&quot;       }     ]   },   \&quot;authorization_model_id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot; } &#x60;&#x60;&#x60; OpenFGA&#39;s response will include &#x60;{ \&quot;allowed\&quot;: true }&#x60; if there is a relationship and &#x60;{ \&quot;allowed\&quot;: false }&#x60; if there isn&#39;t.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        CheckRequest body = new CheckRequest(); // CheckRequest | 
        try {
            CompletableFuture<CheckResponse> result = apiInstance.check(storeId, body);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#check");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**CheckRequest**](CheckRequest.md)|  | |

### Return type

CompletableFuture<[**CheckResponse**](CheckResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## checkWithHttpInfo

> CompletableFuture<ApiResponse<CheckResponse>> check checkWithHttpInfo(storeId, body)

Check whether a user is authorized to access an object

The Check API queries to check if the user has a certain relationship with an object in a certain store. A &#x60;contextual_tuples&#x60; object may also be included in the body of the request. This object contains one field &#x60;tuple_keys&#x60;, which is an array of tuple keys. You may also provide an &#x60;authorization_model_id&#x60; in the body. This will be used to assert that the input &#x60;tuple_key&#x60; is valid for the model specified. If not specified, the assertion will be made against the latest authorization model ID. It is strongly recommended to specify authorization model id for better performance. The response will return whether the relationship exists in the field &#x60;allowed&#x60;.  ## Example In order to check if user &#x60;user:anne&#x60; of type &#x60;user&#x60; has a &#x60;reader&#x60; relationship with object &#x60;document:2021-budget&#x60; given the following contextual tuple &#x60;&#x60;&#x60;json {   \&quot;user\&quot;: \&quot;user:anne\&quot;,   \&quot;relation\&quot;: \&quot;member\&quot;,   \&quot;object\&quot;: \&quot;time_slot:office_hours\&quot; } &#x60;&#x60;&#x60; the Check API can be used with the following request body: &#x60;&#x60;&#x60;json {   \&quot;tuple_key\&quot;: {     \&quot;user\&quot;: \&quot;user:anne\&quot;,     \&quot;relation\&quot;: \&quot;reader\&quot;,     \&quot;object\&quot;: \&quot;document:2021-budget\&quot;   },   \&quot;contextual_tuples\&quot;: {     \&quot;tuple_keys\&quot;: [       {         \&quot;user\&quot;: \&quot;user:anne\&quot;,         \&quot;relation\&quot;: \&quot;member\&quot;,         \&quot;object\&quot;: \&quot;time_slot:office_hours\&quot;       }     ]   },   \&quot;authorization_model_id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot; } &#x60;&#x60;&#x60; OpenFGA&#39;s response will include &#x60;{ \&quot;allowed\&quot;: true }&#x60; if there is a relationship and &#x60;{ \&quot;allowed\&quot;: false }&#x60; if there isn&#39;t.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        CheckRequest body = new CheckRequest(); // CheckRequest | 
        try {
            CompletableFuture<ApiResponse<CheckResponse>> response = apiInstance.checkWithHttpInfo(storeId, body);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#check");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#check");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**CheckRequest**](CheckRequest.md)|  | |

### Return type

CompletableFuture<ApiResponse<[**CheckResponse**](CheckResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## createStore

> CompletableFuture<CreateStoreResponse> createStore(body)

Create a store

Create a unique OpenFGA store which will be used to store authorization models and relationship tuples.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        CreateStoreRequest body = new CreateStoreRequest(); // CreateStoreRequest | 
        try {
            CompletableFuture<CreateStoreResponse> result = apiInstance.createStore(body);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#createStore");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **body** | [**CreateStoreRequest**](CreateStoreRequest.md)|  | |

### Return type

CompletableFuture<[**CreateStoreResponse**](CreateStoreResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## createStoreWithHttpInfo

> CompletableFuture<ApiResponse<CreateStoreResponse>> createStore createStoreWithHttpInfo(body)

Create a store

Create a unique OpenFGA store which will be used to store authorization models and relationship tuples.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        CreateStoreRequest body = new CreateStoreRequest(); // CreateStoreRequest | 
        try {
            CompletableFuture<ApiResponse<CreateStoreResponse>> response = apiInstance.createStoreWithHttpInfo(body);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#createStore");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#createStore");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **body** | [**CreateStoreRequest**](CreateStoreRequest.md)|  | |

### Return type

CompletableFuture<ApiResponse<[**CreateStoreResponse**](CreateStoreResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## deleteStore

> CompletableFuture<Void> deleteStore(storeId)

Delete a store

Delete an OpenFGA store. This does not delete the data associated with the store, like tuples or authorization models.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        try {
            CompletableFuture<Void> result = apiInstance.deleteStore(storeId);
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#deleteStore");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |

### Return type


CompletableFuture<void> (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## deleteStoreWithHttpInfo

> CompletableFuture<ApiResponse<Void>> deleteStore deleteStoreWithHttpInfo(storeId)

Delete a store

Delete an OpenFGA store. This does not delete the data associated with the store, like tuples or authorization models.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        try {
            CompletableFuture<ApiResponse<Void>> response = apiInstance.deleteStoreWithHttpInfo(storeId);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#deleteStore");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#deleteStore");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |

### Return type


CompletableFuture<ApiResponse<Void>>

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## expand

> CompletableFuture<ExpandResponse> expand(storeId, body)

Expand all relationships in userset tree format, and following userset rewrite rules.  Useful to reason about and debug a certain relationship

The Expand API will return all users and usersets that have certain relationship with an object in a certain store. This is different from the &#x60;/stores/{store_id}/read&#x60; API in that both users and computed usersets are returned. Body parameters &#x60;tuple_key.object&#x60; and &#x60;tuple_key.relation&#x60; are all required. The response will return a tree whose leaves are the specific users and usersets. Union, intersection and difference operator are located in the intermediate nodes.  ## Example To expand all users that have the &#x60;reader&#x60; relationship with object &#x60;document:2021-budget&#x60;, use the Expand API with the following request body &#x60;&#x60;&#x60;json {   \&quot;tuple_key\&quot;: {     \&quot;object\&quot;: \&quot;document:2021-budget\&quot;,     \&quot;relation\&quot;: \&quot;reader\&quot;   },   \&quot;authorization_model_id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot; } &#x60;&#x60;&#x60; OpenFGA&#39;s response will be a userset tree of the users and usersets that have read access to the document. &#x60;&#x60;&#x60;json {   \&quot;tree\&quot;:{     \&quot;root\&quot;:{       \&quot;type\&quot;:\&quot;document:2021-budget#reader\&quot;,       \&quot;union\&quot;:{         \&quot;nodes\&quot;:[           {             \&quot;type\&quot;:\&quot;document:2021-budget#reader\&quot;,             \&quot;leaf\&quot;:{               \&quot;users\&quot;:{                 \&quot;users\&quot;:[                   \&quot;user:bob\&quot;                 ]               }             }           },           {             \&quot;type\&quot;:\&quot;document:2021-budget#reader\&quot;,             \&quot;leaf\&quot;:{               \&quot;computed\&quot;:{                 \&quot;userset\&quot;:\&quot;document:2021-budget#writer\&quot;               }             }           }         ]       }     }   } } &#x60;&#x60;&#x60; The caller can then call expand API for the &#x60;writer&#x60; relationship for the &#x60;document:2021-budget&#x60;.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        ExpandRequest body = new ExpandRequest(); // ExpandRequest | 
        try {
            CompletableFuture<ExpandResponse> result = apiInstance.expand(storeId, body);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#expand");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**ExpandRequest**](ExpandRequest.md)|  | |

### Return type

CompletableFuture<[**ExpandResponse**](ExpandResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## expandWithHttpInfo

> CompletableFuture<ApiResponse<ExpandResponse>> expand expandWithHttpInfo(storeId, body)

Expand all relationships in userset tree format, and following userset rewrite rules.  Useful to reason about and debug a certain relationship

The Expand API will return all users and usersets that have certain relationship with an object in a certain store. This is different from the &#x60;/stores/{store_id}/read&#x60; API in that both users and computed usersets are returned. Body parameters &#x60;tuple_key.object&#x60; and &#x60;tuple_key.relation&#x60; are all required. The response will return a tree whose leaves are the specific users and usersets. Union, intersection and difference operator are located in the intermediate nodes.  ## Example To expand all users that have the &#x60;reader&#x60; relationship with object &#x60;document:2021-budget&#x60;, use the Expand API with the following request body &#x60;&#x60;&#x60;json {   \&quot;tuple_key\&quot;: {     \&quot;object\&quot;: \&quot;document:2021-budget\&quot;,     \&quot;relation\&quot;: \&quot;reader\&quot;   },   \&quot;authorization_model_id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot; } &#x60;&#x60;&#x60; OpenFGA&#39;s response will be a userset tree of the users and usersets that have read access to the document. &#x60;&#x60;&#x60;json {   \&quot;tree\&quot;:{     \&quot;root\&quot;:{       \&quot;type\&quot;:\&quot;document:2021-budget#reader\&quot;,       \&quot;union\&quot;:{         \&quot;nodes\&quot;:[           {             \&quot;type\&quot;:\&quot;document:2021-budget#reader\&quot;,             \&quot;leaf\&quot;:{               \&quot;users\&quot;:{                 \&quot;users\&quot;:[                   \&quot;user:bob\&quot;                 ]               }             }           },           {             \&quot;type\&quot;:\&quot;document:2021-budget#reader\&quot;,             \&quot;leaf\&quot;:{               \&quot;computed\&quot;:{                 \&quot;userset\&quot;:\&quot;document:2021-budget#writer\&quot;               }             }           }         ]       }     }   } } &#x60;&#x60;&#x60; The caller can then call expand API for the &#x60;writer&#x60; relationship for the &#x60;document:2021-budget&#x60;.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        ExpandRequest body = new ExpandRequest(); // ExpandRequest | 
        try {
            CompletableFuture<ApiResponse<ExpandResponse>> response = apiInstance.expandWithHttpInfo(storeId, body);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#expand");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#expand");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**ExpandRequest**](ExpandRequest.md)|  | |

### Return type

CompletableFuture<ApiResponse<[**ExpandResponse**](ExpandResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## getStore

> CompletableFuture<GetStoreResponse> getStore(storeId)

Get a store

Returns an OpenFGA store by its identifier

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        try {
            CompletableFuture<GetStoreResponse> result = apiInstance.getStore(storeId);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#getStore");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |

### Return type

CompletableFuture<[**GetStoreResponse**](GetStoreResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## getStoreWithHttpInfo

> CompletableFuture<ApiResponse<GetStoreResponse>> getStore getStoreWithHttpInfo(storeId)

Get a store

Returns an OpenFGA store by its identifier

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        try {
            CompletableFuture<ApiResponse<GetStoreResponse>> response = apiInstance.getStoreWithHttpInfo(storeId);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#getStore");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#getStore");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |

### Return type

CompletableFuture<ApiResponse<[**GetStoreResponse**](GetStoreResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## listObjects

> CompletableFuture<ListObjectsResponse> listObjects(storeId, body)

List all objects of the given type that the user has a relation with

The ListObjects API returns a list of all the objects of the given type that the user has a relation with. To achieve this, both the store tuples and the authorization model are used. An &#x60;authorization_model_id&#x60; may be specified in the body. If it is not specified, the latest authorization model ID will be used. It is strongly recommended to specify authorization model id for better performance. You may also specify &#x60;contextual_tuples&#x60; that will be treated as regular tuples. The response will contain the related objects in an array in the \&quot;objects\&quot; field of the response and they will be strings in the object format &#x60;&lt;type&gt;:&lt;id&gt;&#x60; (e.g. \&quot;document:roadmap\&quot;). The number of objects in the response array will be limited by the execution timeout specified in the flag OPENFGA_LIST_OBJECTS_DEADLINE and by the upper bound specified in the flag OPENFGA_LIST_OBJECTS_MAX_RESULTS, whichever is hit first.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        ListObjectsRequest body = new ListObjectsRequest(); // ListObjectsRequest | 
        try {
            CompletableFuture<ListObjectsResponse> result = apiInstance.listObjects(storeId, body);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#listObjects");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**ListObjectsRequest**](ListObjectsRequest.md)|  | |

### Return type

CompletableFuture<[**ListObjectsResponse**](ListObjectsResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## listObjectsWithHttpInfo

> CompletableFuture<ApiResponse<ListObjectsResponse>> listObjects listObjectsWithHttpInfo(storeId, body)

List all objects of the given type that the user has a relation with

The ListObjects API returns a list of all the objects of the given type that the user has a relation with. To achieve this, both the store tuples and the authorization model are used. An &#x60;authorization_model_id&#x60; may be specified in the body. If it is not specified, the latest authorization model ID will be used. It is strongly recommended to specify authorization model id for better performance. You may also specify &#x60;contextual_tuples&#x60; that will be treated as regular tuples. The response will contain the related objects in an array in the \&quot;objects\&quot; field of the response and they will be strings in the object format &#x60;&lt;type&gt;:&lt;id&gt;&#x60; (e.g. \&quot;document:roadmap\&quot;). The number of objects in the response array will be limited by the execution timeout specified in the flag OPENFGA_LIST_OBJECTS_DEADLINE and by the upper bound specified in the flag OPENFGA_LIST_OBJECTS_MAX_RESULTS, whichever is hit first.

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        ListObjectsRequest body = new ListObjectsRequest(); // ListObjectsRequest | 
        try {
            CompletableFuture<ApiResponse<ListObjectsResponse>> response = apiInstance.listObjectsWithHttpInfo(storeId, body);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#listObjects");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#listObjects");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**ListObjectsRequest**](ListObjectsRequest.md)|  | |

### Return type

CompletableFuture<ApiResponse<[**ListObjectsResponse**](ListObjectsResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## listStores

> CompletableFuture<ListStoresResponse> listStores(pageSize, continuationToken)

List all stores

Returns a paginated list of OpenFGA stores and a continuation token to get additional stores. The continuation token will be empty if there are no more stores. 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        Integer pageSize = 56; // Integer | 
        String continuationToken = "continuationToken_example"; // String | 
        try {
            CompletableFuture<ListStoresResponse> result = apiInstance.listStores(pageSize, continuationToken);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#listStores");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pageSize** | **Integer**|  | [optional] |
| **continuationToken** | **String**|  | [optional] |

### Return type

CompletableFuture<[**ListStoresResponse**](ListStoresResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## listStoresWithHttpInfo

> CompletableFuture<ApiResponse<ListStoresResponse>> listStores listStoresWithHttpInfo(pageSize, continuationToken)

List all stores

Returns a paginated list of OpenFGA stores and a continuation token to get additional stores. The continuation token will be empty if there are no more stores. 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        Integer pageSize = 56; // Integer | 
        String continuationToken = "continuationToken_example"; // String | 
        try {
            CompletableFuture<ApiResponse<ListStoresResponse>> response = apiInstance.listStoresWithHttpInfo(pageSize, continuationToken);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#listStores");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#listStores");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pageSize** | **Integer**|  | [optional] |
| **continuationToken** | **String**|  | [optional] |

### Return type

CompletableFuture<ApiResponse<[**ListStoresResponse**](ListStoresResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## read

> CompletableFuture<ReadResponse> read(storeId, body)

Get tuples from the store that matches a query, without following userset rewrite rules

The Read API will return the tuples for a certain store that match a query filter specified in the body of the request. It is different from the &#x60;/stores/{store_id}/expand&#x60; API in that it only returns relationship tuples that are stored in the system and satisfy the query.  In the body: 1. &#x60;tuple_key&#x60; is optional. If not specified, it will return all tuples in the store. 2. &#x60;tuple_key.object&#x60; is mandatory if &#x60;tuple_key&#x60; is specified. It can be a full object (e.g., &#x60;type:object_id&#x60;) or type only (e.g., &#x60;type:&#x60;). 3. &#x60;tuple_key.user&#x60; is mandatory if tuple_key is specified in the case the &#x60;tuple_key.object&#x60; is a type only. ## Examples ### Query for all objects in a type definition To query for all objects that &#x60;user:bob&#x60; has &#x60;reader&#x60; relationship in the &#x60;document&#x60; type definition, call read API with body of &#x60;&#x60;&#x60;json {  \&quot;tuple_key\&quot;: {      \&quot;user\&quot;: \&quot;user:bob\&quot;,      \&quot;relation\&quot;: \&quot;reader\&quot;,      \&quot;object\&quot;: \&quot;document:\&quot;   } } &#x60;&#x60;&#x60; The API will return tuples and a continuation token, something like &#x60;&#x60;&#x60;json {   \&quot;tuples\&quot;: [     {       \&quot;key\&quot;: {         \&quot;user\&quot;: \&quot;user:bob\&quot;,         \&quot;relation\&quot;: \&quot;reader\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       },       \&quot;timestamp\&quot;: \&quot;2021-10-06T15:32:11.128Z\&quot;     }   ],   \&quot;continuation_token\&quot;: \&quot;eyJwayI6IkxBVEVTVF9OU0NPTkZJR19hdXRoMHN0b3JlIiwic2siOiIxem1qbXF3MWZLZExTcUoyN01MdTdqTjh0cWgifQ&#x3D;&#x3D;\&quot; } &#x60;&#x60;&#x60; This means that &#x60;user:bob&#x60; has a &#x60;reader&#x60; relationship with 1 document &#x60;document:2021-budget&#x60;. Note that this API, unlike the List Objects API, does not evaluate the tuples in the store. The continuation token will be empty if there are no more tuples to query.### Query for all stored relationship tuples that have a particular relation and object To query for all users that have &#x60;reader&#x60; relationship with &#x60;document:2021-budget&#x60;, call read API with body of  &#x60;&#x60;&#x60;json {   \&quot;tuple_key\&quot;: {      \&quot;object\&quot;: \&quot;document:2021-budget\&quot;,      \&quot;relation\&quot;: \&quot;reader\&quot;    } } &#x60;&#x60;&#x60; The API will return something like  &#x60;&#x60;&#x60;json {   \&quot;tuples\&quot;: [     {       \&quot;key\&quot;: {         \&quot;user\&quot;: \&quot;user:bob\&quot;,         \&quot;relation\&quot;: \&quot;reader\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       },       \&quot;timestamp\&quot;: \&quot;2021-10-06T15:32:11.128Z\&quot;     }   ],   \&quot;continuation_token\&quot;: \&quot;eyJwayI6IkxBVEVTVF9OU0NPTkZJR19hdXRoMHN0b3JlIiwic2siOiIxem1qbXF3MWZLZExTcUoyN01MdTdqTjh0cWgifQ&#x3D;&#x3D;\&quot; } &#x60;&#x60;&#x60; This means that &#x60;document:2021-budget&#x60; has 1 &#x60;reader&#x60; (&#x60;user:bob&#x60;).  Note that, even if the model said that all &#x60;writers&#x60; are also &#x60;readers&#x60;, the API will not return writers such as &#x60;user:anne&#x60; because it only returns tuples and does not evaluate them. ### Query for all users with all relationships for a particular document To query for all users that have any relationship with &#x60;document:2021-budget&#x60;, call read API with body of  &#x60;&#x60;&#x60;json {   \&quot;tuple_key\&quot;: {       \&quot;object\&quot;: \&quot;document:2021-budget\&quot;    } } &#x60;&#x60;&#x60; The API will return something like  &#x60;&#x60;&#x60;json {   \&quot;tuples\&quot;: [     {       \&quot;key\&quot;: {         \&quot;user\&quot;: \&quot;user:anne\&quot;,         \&quot;relation\&quot;: \&quot;writer\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       },       \&quot;timestamp\&quot;: \&quot;2021-10-05T13:42:12.356Z\&quot;     },     {       \&quot;key\&quot;: {         \&quot;user\&quot;: \&quot;user:bob\&quot;,         \&quot;relation\&quot;: \&quot;reader\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       },       \&quot;timestamp\&quot;: \&quot;2021-10-06T15:32:11.128Z\&quot;     }   ],   \&quot;continuation_token\&quot;: \&quot;eyJwayI6IkxBVEVTVF9OU0NPTkZJR19hdXRoMHN0b3JlIiwic2siOiIxem1qbXF3MWZLZExTcUoyN01MdTdqTjh0cWgifQ&#x3D;&#x3D;\&quot; } &#x60;&#x60;&#x60; This means that &#x60;document:2021-budget&#x60; has 1 &#x60;reader&#x60; (&#x60;user:bob&#x60;) and 1 &#x60;writer&#x60; (&#x60;user:anne&#x60;). 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        ReadRequest body = new ReadRequest(); // ReadRequest | 
        try {
            CompletableFuture<ReadResponse> result = apiInstance.read(storeId, body);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#read");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**ReadRequest**](ReadRequest.md)|  | |

### Return type

CompletableFuture<[**ReadResponse**](ReadResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## readWithHttpInfo

> CompletableFuture<ApiResponse<ReadResponse>> read readWithHttpInfo(storeId, body)

Get tuples from the store that matches a query, without following userset rewrite rules

The Read API will return the tuples for a certain store that match a query filter specified in the body of the request. It is different from the &#x60;/stores/{store_id}/expand&#x60; API in that it only returns relationship tuples that are stored in the system and satisfy the query.  In the body: 1. &#x60;tuple_key&#x60; is optional. If not specified, it will return all tuples in the store. 2. &#x60;tuple_key.object&#x60; is mandatory if &#x60;tuple_key&#x60; is specified. It can be a full object (e.g., &#x60;type:object_id&#x60;) or type only (e.g., &#x60;type:&#x60;). 3. &#x60;tuple_key.user&#x60; is mandatory if tuple_key is specified in the case the &#x60;tuple_key.object&#x60; is a type only. ## Examples ### Query for all objects in a type definition To query for all objects that &#x60;user:bob&#x60; has &#x60;reader&#x60; relationship in the &#x60;document&#x60; type definition, call read API with body of &#x60;&#x60;&#x60;json {  \&quot;tuple_key\&quot;: {      \&quot;user\&quot;: \&quot;user:bob\&quot;,      \&quot;relation\&quot;: \&quot;reader\&quot;,      \&quot;object\&quot;: \&quot;document:\&quot;   } } &#x60;&#x60;&#x60; The API will return tuples and a continuation token, something like &#x60;&#x60;&#x60;json {   \&quot;tuples\&quot;: [     {       \&quot;key\&quot;: {         \&quot;user\&quot;: \&quot;user:bob\&quot;,         \&quot;relation\&quot;: \&quot;reader\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       },       \&quot;timestamp\&quot;: \&quot;2021-10-06T15:32:11.128Z\&quot;     }   ],   \&quot;continuation_token\&quot;: \&quot;eyJwayI6IkxBVEVTVF9OU0NPTkZJR19hdXRoMHN0b3JlIiwic2siOiIxem1qbXF3MWZLZExTcUoyN01MdTdqTjh0cWgifQ&#x3D;&#x3D;\&quot; } &#x60;&#x60;&#x60; This means that &#x60;user:bob&#x60; has a &#x60;reader&#x60; relationship with 1 document &#x60;document:2021-budget&#x60;. Note that this API, unlike the List Objects API, does not evaluate the tuples in the store. The continuation token will be empty if there are no more tuples to query.### Query for all stored relationship tuples that have a particular relation and object To query for all users that have &#x60;reader&#x60; relationship with &#x60;document:2021-budget&#x60;, call read API with body of  &#x60;&#x60;&#x60;json {   \&quot;tuple_key\&quot;: {      \&quot;object\&quot;: \&quot;document:2021-budget\&quot;,      \&quot;relation\&quot;: \&quot;reader\&quot;    } } &#x60;&#x60;&#x60; The API will return something like  &#x60;&#x60;&#x60;json {   \&quot;tuples\&quot;: [     {       \&quot;key\&quot;: {         \&quot;user\&quot;: \&quot;user:bob\&quot;,         \&quot;relation\&quot;: \&quot;reader\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       },       \&quot;timestamp\&quot;: \&quot;2021-10-06T15:32:11.128Z\&quot;     }   ],   \&quot;continuation_token\&quot;: \&quot;eyJwayI6IkxBVEVTVF9OU0NPTkZJR19hdXRoMHN0b3JlIiwic2siOiIxem1qbXF3MWZLZExTcUoyN01MdTdqTjh0cWgifQ&#x3D;&#x3D;\&quot; } &#x60;&#x60;&#x60; This means that &#x60;document:2021-budget&#x60; has 1 &#x60;reader&#x60; (&#x60;user:bob&#x60;).  Note that, even if the model said that all &#x60;writers&#x60; are also &#x60;readers&#x60;, the API will not return writers such as &#x60;user:anne&#x60; because it only returns tuples and does not evaluate them. ### Query for all users with all relationships for a particular document To query for all users that have any relationship with &#x60;document:2021-budget&#x60;, call read API with body of  &#x60;&#x60;&#x60;json {   \&quot;tuple_key\&quot;: {       \&quot;object\&quot;: \&quot;document:2021-budget\&quot;    } } &#x60;&#x60;&#x60; The API will return something like  &#x60;&#x60;&#x60;json {   \&quot;tuples\&quot;: [     {       \&quot;key\&quot;: {         \&quot;user\&quot;: \&quot;user:anne\&quot;,         \&quot;relation\&quot;: \&quot;writer\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       },       \&quot;timestamp\&quot;: \&quot;2021-10-05T13:42:12.356Z\&quot;     },     {       \&quot;key\&quot;: {         \&quot;user\&quot;: \&quot;user:bob\&quot;,         \&quot;relation\&quot;: \&quot;reader\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       },       \&quot;timestamp\&quot;: \&quot;2021-10-06T15:32:11.128Z\&quot;     }   ],   \&quot;continuation_token\&quot;: \&quot;eyJwayI6IkxBVEVTVF9OU0NPTkZJR19hdXRoMHN0b3JlIiwic2siOiIxem1qbXF3MWZLZExTcUoyN01MdTdqTjh0cWgifQ&#x3D;&#x3D;\&quot; } &#x60;&#x60;&#x60; This means that &#x60;document:2021-budget&#x60; has 1 &#x60;reader&#x60; (&#x60;user:bob&#x60;) and 1 &#x60;writer&#x60; (&#x60;user:anne&#x60;). 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        ReadRequest body = new ReadRequest(); // ReadRequest | 
        try {
            CompletableFuture<ApiResponse<ReadResponse>> response = apiInstance.readWithHttpInfo(storeId, body);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#read");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#read");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**ReadRequest**](ReadRequest.md)|  | |

### Return type

CompletableFuture<ApiResponse<[**ReadResponse**](ReadResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## readAssertions

> CompletableFuture<ReadAssertionsResponse> readAssertions(storeId, authorizationModelId)

Read assertions for an authorization model ID

The ReadAssertions API will return, for a given authorization model id, all the assertions stored for it. An assertion is an object that contains a tuple key, and the expectation of whether a call to the Check API of that tuple key will return true or false. 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        String authorizationModelId = "authorizationModelId_example"; // String | 
        try {
            CompletableFuture<ReadAssertionsResponse> result = apiInstance.readAssertions(storeId, authorizationModelId);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#readAssertions");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **authorizationModelId** | **String**|  | |

### Return type

CompletableFuture<[**ReadAssertionsResponse**](ReadAssertionsResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## readAssertionsWithHttpInfo

> CompletableFuture<ApiResponse<ReadAssertionsResponse>> readAssertions readAssertionsWithHttpInfo(storeId, authorizationModelId)

Read assertions for an authorization model ID

The ReadAssertions API will return, for a given authorization model id, all the assertions stored for it. An assertion is an object that contains a tuple key, and the expectation of whether a call to the Check API of that tuple key will return true or false. 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        String authorizationModelId = "authorizationModelId_example"; // String | 
        try {
            CompletableFuture<ApiResponse<ReadAssertionsResponse>> response = apiInstance.readAssertionsWithHttpInfo(storeId, authorizationModelId);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#readAssertions");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#readAssertions");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **authorizationModelId** | **String**|  | |

### Return type

CompletableFuture<ApiResponse<[**ReadAssertionsResponse**](ReadAssertionsResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## readAuthorizationModel

> CompletableFuture<ReadAuthorizationModelResponse> readAuthorizationModel(storeId, id)

Return a particular version of an authorization model

The ReadAuthorizationModel API returns an authorization model by its identifier. The response will return the authorization model for the particular version.  ## Example To retrieve the authorization model with ID &#x60;01G5JAVJ41T49E9TT3SKVS7X1J&#x60; for the store, call the GET authorization-models by ID API with &#x60;01G5JAVJ41T49E9TT3SKVS7X1J&#x60; as the &#x60;id&#x60; path parameter.  The API will return: &#x60;&#x60;&#x60;json {   \&quot;authorization_model\&quot;:{     \&quot;id\&quot;:\&quot;01G5JAVJ41T49E9TT3SKVS7X1J\&quot;,     \&quot;type_definitions\&quot;:[       {         \&quot;type\&quot;:\&quot;user\&quot;       },       {         \&quot;type\&quot;:\&quot;document\&quot;,         \&quot;relations\&quot;:{           \&quot;reader\&quot;:{             \&quot;union\&quot;:{               \&quot;child\&quot;:[                 {                   \&quot;this\&quot;:{}                 },                 {                   \&quot;computedUserset\&quot;:{                     \&quot;object\&quot;:\&quot;\&quot;,                     \&quot;relation\&quot;:\&quot;writer\&quot;                   }                 }               ]             }           },           \&quot;writer\&quot;:{             \&quot;this\&quot;:{}           }         }       }     ]   } } &#x60;&#x60;&#x60; In the above example, there are 2 types (&#x60;user&#x60; and &#x60;document&#x60;). The &#x60;document&#x60; type has 2 relations (&#x60;writer&#x60; and &#x60;reader&#x60;).

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        String id = "id_example"; // String | 
        try {
            CompletableFuture<ReadAuthorizationModelResponse> result = apiInstance.readAuthorizationModel(storeId, id);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#readAuthorizationModel");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **id** | **String**|  | |

### Return type

CompletableFuture<[**ReadAuthorizationModelResponse**](ReadAuthorizationModelResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## readAuthorizationModelWithHttpInfo

> CompletableFuture<ApiResponse<ReadAuthorizationModelResponse>> readAuthorizationModel readAuthorizationModelWithHttpInfo(storeId, id)

Return a particular version of an authorization model

The ReadAuthorizationModel API returns an authorization model by its identifier. The response will return the authorization model for the particular version.  ## Example To retrieve the authorization model with ID &#x60;01G5JAVJ41T49E9TT3SKVS7X1J&#x60; for the store, call the GET authorization-models by ID API with &#x60;01G5JAVJ41T49E9TT3SKVS7X1J&#x60; as the &#x60;id&#x60; path parameter.  The API will return: &#x60;&#x60;&#x60;json {   \&quot;authorization_model\&quot;:{     \&quot;id\&quot;:\&quot;01G5JAVJ41T49E9TT3SKVS7X1J\&quot;,     \&quot;type_definitions\&quot;:[       {         \&quot;type\&quot;:\&quot;user\&quot;       },       {         \&quot;type\&quot;:\&quot;document\&quot;,         \&quot;relations\&quot;:{           \&quot;reader\&quot;:{             \&quot;union\&quot;:{               \&quot;child\&quot;:[                 {                   \&quot;this\&quot;:{}                 },                 {                   \&quot;computedUserset\&quot;:{                     \&quot;object\&quot;:\&quot;\&quot;,                     \&quot;relation\&quot;:\&quot;writer\&quot;                   }                 }               ]             }           },           \&quot;writer\&quot;:{             \&quot;this\&quot;:{}           }         }       }     ]   } } &#x60;&#x60;&#x60; In the above example, there are 2 types (&#x60;user&#x60; and &#x60;document&#x60;). The &#x60;document&#x60; type has 2 relations (&#x60;writer&#x60; and &#x60;reader&#x60;).

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        String id = "id_example"; // String | 
        try {
            CompletableFuture<ApiResponse<ReadAuthorizationModelResponse>> response = apiInstance.readAuthorizationModelWithHttpInfo(storeId, id);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#readAuthorizationModel");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#readAuthorizationModel");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **id** | **String**|  | |

### Return type

CompletableFuture<ApiResponse<[**ReadAuthorizationModelResponse**](ReadAuthorizationModelResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## readAuthorizationModels

> CompletableFuture<ReadAuthorizationModelsResponse> readAuthorizationModels(storeId, pageSize, continuationToken)

Return all the authorization models for a particular store

The ReadAuthorizationModels API will return all the authorization models for a certain store. OpenFGA&#39;s response will contain an array of all authorization models, sorted in descending order of creation.  ## Example Assume that a store&#39;s authorization model has been configured twice. To get all the authorization models that have been created in this store, call GET authorization-models. The API will return a response that looks like: &#x60;&#x60;&#x60;json {   \&quot;authorization_models\&quot;: [     {       \&quot;id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot;,       \&quot;type_definitions\&quot;: [...]     },     {       \&quot;id\&quot;: \&quot;01G4ZW8F4A07AKQ8RHSVG9RW04\&quot;,       \&quot;type_definitions\&quot;: [...]     },   ],   \&quot;continuation_token\&quot;: \&quot;eyJwayI6IkxBVEVTVF9OU0NPTkZJR19hdXRoMHN0b3JlIiwic2siOiIxem1qbXF3MWZLZExTcUoyN01MdTdqTjh0cWgifQ&#x3D;&#x3D;\&quot; } &#x60;&#x60;&#x60; If there are no more authorization models available, the &#x60;continuation_token&#x60; field will be empty &#x60;&#x60;&#x60;json {   \&quot;authorization_models\&quot;: [     {       \&quot;id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot;,       \&quot;type_definitions\&quot;: [...]     },     {       \&quot;id\&quot;: \&quot;01G4ZW8F4A07AKQ8RHSVG9RW04\&quot;,       \&quot;type_definitions\&quot;: [...]     },   ],   \&quot;continuation_token\&quot;: \&quot;\&quot; } &#x60;&#x60;&#x60; 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        Integer pageSize = 56; // Integer | 
        String continuationToken = "continuationToken_example"; // String | 
        try {
            CompletableFuture<ReadAuthorizationModelsResponse> result = apiInstance.readAuthorizationModels(storeId, pageSize, continuationToken);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#readAuthorizationModels");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **pageSize** | **Integer**|  | [optional] |
| **continuationToken** | **String**|  | [optional] |

### Return type

CompletableFuture<[**ReadAuthorizationModelsResponse**](ReadAuthorizationModelsResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## readAuthorizationModelsWithHttpInfo

> CompletableFuture<ApiResponse<ReadAuthorizationModelsResponse>> readAuthorizationModels readAuthorizationModelsWithHttpInfo(storeId, pageSize, continuationToken)

Return all the authorization models for a particular store

The ReadAuthorizationModels API will return all the authorization models for a certain store. OpenFGA&#39;s response will contain an array of all authorization models, sorted in descending order of creation.  ## Example Assume that a store&#39;s authorization model has been configured twice. To get all the authorization models that have been created in this store, call GET authorization-models. The API will return a response that looks like: &#x60;&#x60;&#x60;json {   \&quot;authorization_models\&quot;: [     {       \&quot;id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot;,       \&quot;type_definitions\&quot;: [...]     },     {       \&quot;id\&quot;: \&quot;01G4ZW8F4A07AKQ8RHSVG9RW04\&quot;,       \&quot;type_definitions\&quot;: [...]     },   ],   \&quot;continuation_token\&quot;: \&quot;eyJwayI6IkxBVEVTVF9OU0NPTkZJR19hdXRoMHN0b3JlIiwic2siOiIxem1qbXF3MWZLZExTcUoyN01MdTdqTjh0cWgifQ&#x3D;&#x3D;\&quot; } &#x60;&#x60;&#x60; If there are no more authorization models available, the &#x60;continuation_token&#x60; field will be empty &#x60;&#x60;&#x60;json {   \&quot;authorization_models\&quot;: [     {       \&quot;id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot;,       \&quot;type_definitions\&quot;: [...]     },     {       \&quot;id\&quot;: \&quot;01G4ZW8F4A07AKQ8RHSVG9RW04\&quot;,       \&quot;type_definitions\&quot;: [...]     },   ],   \&quot;continuation_token\&quot;: \&quot;\&quot; } &#x60;&#x60;&#x60; 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        Integer pageSize = 56; // Integer | 
        String continuationToken = "continuationToken_example"; // String | 
        try {
            CompletableFuture<ApiResponse<ReadAuthorizationModelsResponse>> response = apiInstance.readAuthorizationModelsWithHttpInfo(storeId, pageSize, continuationToken);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#readAuthorizationModels");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#readAuthorizationModels");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **pageSize** | **Integer**|  | [optional] |
| **continuationToken** | **String**|  | [optional] |

### Return type

CompletableFuture<ApiResponse<[**ReadAuthorizationModelsResponse**](ReadAuthorizationModelsResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## readChanges

> CompletableFuture<ReadChangesResponse> readChanges(storeId, type, pageSize, continuationToken)

Return a list of all the tuple changes

The ReadChanges API will return a paginated list of tuple changes (additions and deletions) that occurred in a given store, sorted by ascending time. The response will include a continuation token that is used to get the next set of changes. If there are no changes after the provided continuation token, the same token will be returned in order for it to be used when new changes are recorded. If the store never had any tuples added or removed, this token will be empty. You can use the &#x60;type&#x60; parameter to only get the list of tuple changes that affect objects of that type. 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        String type = "type_example"; // String | 
        Integer pageSize = 56; // Integer | 
        String continuationToken = "continuationToken_example"; // String | 
        try {
            CompletableFuture<ReadChangesResponse> result = apiInstance.readChanges(storeId, type, pageSize, continuationToken);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#readChanges");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **type** | **String**|  | [optional] |
| **pageSize** | **Integer**|  | [optional] |
| **continuationToken** | **String**|  | [optional] |

### Return type

CompletableFuture<[**ReadChangesResponse**](ReadChangesResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## readChangesWithHttpInfo

> CompletableFuture<ApiResponse<ReadChangesResponse>> readChanges readChangesWithHttpInfo(storeId, type, pageSize, continuationToken)

Return a list of all the tuple changes

The ReadChanges API will return a paginated list of tuple changes (additions and deletions) that occurred in a given store, sorted by ascending time. The response will include a continuation token that is used to get the next set of changes. If there are no changes after the provided continuation token, the same token will be returned in order for it to be used when new changes are recorded. If the store never had any tuples added or removed, this token will be empty. You can use the &#x60;type&#x60; parameter to only get the list of tuple changes that affect objects of that type. 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        String type = "type_example"; // String | 
        Integer pageSize = 56; // Integer | 
        String continuationToken = "continuationToken_example"; // String | 
        try {
            CompletableFuture<ApiResponse<ReadChangesResponse>> response = apiInstance.readChangesWithHttpInfo(storeId, type, pageSize, continuationToken);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#readChanges");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#readChanges");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **type** | **String**|  | [optional] |
| **pageSize** | **Integer**|  | [optional] |
| **continuationToken** | **String**|  | [optional] |

### Return type

CompletableFuture<ApiResponse<[**ReadChangesResponse**](ReadChangesResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## write

> CompletableFuture<Object> write(storeId, body)

Add or delete tuples from the store

The Write API will update the tuples for a certain store. Tuples and type definitions allow OpenFGA to determine whether a relationship exists between an object and an user. In the body, &#x60;writes&#x60; adds new tuples while &#x60;deletes&#x60; removes existing tuples. The API is not idempotent: if, later on, you try to add the same tuple, or if you try to delete a non-existing tuple, it will throw an error. An &#x60;authorization_model_id&#x60; may be specified in the body. If it is, it will be used to assert that each written tuple (not deleted) is valid for the model specified. If it is not specified, the latest authorization model ID will be used. ## Example ### Adding relationships To add &#x60;user:anne&#x60; as a &#x60;writer&#x60; for &#x60;document:2021-budget&#x60;, call write API with the following  &#x60;&#x60;&#x60;json {   \&quot;writes\&quot;: {     \&quot;tuple_keys\&quot;: [       {         \&quot;user\&quot;: \&quot;user:anne\&quot;,         \&quot;relation\&quot;: \&quot;writer\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       }     ]   },   \&quot;authorization_model_id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot; } &#x60;&#x60;&#x60; ### Removing relationships To remove &#x60;user:bob&#x60; as a &#x60;reader&#x60; for &#x60;document:2021-budget&#x60;, call write API with the following  &#x60;&#x60;&#x60;json {   \&quot;deletes\&quot;: {     \&quot;tuple_keys\&quot;: [       {         \&quot;user\&quot;: \&quot;user:bob\&quot;,         \&quot;relation\&quot;: \&quot;reader\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       }     ]   } } &#x60;&#x60;&#x60; 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        WriteRequest body = new WriteRequest(); // WriteRequest | 
        try {
            CompletableFuture<Object> result = apiInstance.write(storeId, body);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#write");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**WriteRequest**](WriteRequest.md)|  | |

### Return type

CompletableFuture<**Object**>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## writeWithHttpInfo

> CompletableFuture<ApiResponse<Object>> write writeWithHttpInfo(storeId, body)

Add or delete tuples from the store

The Write API will update the tuples for a certain store. Tuples and type definitions allow OpenFGA to determine whether a relationship exists between an object and an user. In the body, &#x60;writes&#x60; adds new tuples while &#x60;deletes&#x60; removes existing tuples. The API is not idempotent: if, later on, you try to add the same tuple, or if you try to delete a non-existing tuple, it will throw an error. An &#x60;authorization_model_id&#x60; may be specified in the body. If it is, it will be used to assert that each written tuple (not deleted) is valid for the model specified. If it is not specified, the latest authorization model ID will be used. ## Example ### Adding relationships To add &#x60;user:anne&#x60; as a &#x60;writer&#x60; for &#x60;document:2021-budget&#x60;, call write API with the following  &#x60;&#x60;&#x60;json {   \&quot;writes\&quot;: {     \&quot;tuple_keys\&quot;: [       {         \&quot;user\&quot;: \&quot;user:anne\&quot;,         \&quot;relation\&quot;: \&quot;writer\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       }     ]   },   \&quot;authorization_model_id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot; } &#x60;&#x60;&#x60; ### Removing relationships To remove &#x60;user:bob&#x60; as a &#x60;reader&#x60; for &#x60;document:2021-budget&#x60;, call write API with the following  &#x60;&#x60;&#x60;json {   \&quot;deletes\&quot;: {     \&quot;tuple_keys\&quot;: [       {         \&quot;user\&quot;: \&quot;user:bob\&quot;,         \&quot;relation\&quot;: \&quot;reader\&quot;,         \&quot;object\&quot;: \&quot;document:2021-budget\&quot;       }     ]   } } &#x60;&#x60;&#x60; 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        WriteRequest body = new WriteRequest(); // WriteRequest | 
        try {
            CompletableFuture<ApiResponse<Object>> response = apiInstance.writeWithHttpInfo(storeId, body);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#write");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#write");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**WriteRequest**](WriteRequest.md)|  | |

### Return type

CompletableFuture<ApiResponse<**Object**>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## writeAssertions

> CompletableFuture<Void> writeAssertions(storeId, authorizationModelId, body)

Upsert assertions for an authorization model ID

The WriteAssertions API will upsert new assertions for an authorization model id, or overwrite the existing ones. An assertion is an object that contains a tuple key, and the expectation of whether a call to the Check API of that tuple key will return true or false. 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        String authorizationModelId = "authorizationModelId_example"; // String | 
        WriteAssertionsRequest body = new WriteAssertionsRequest(); // WriteAssertionsRequest | 
        try {
            CompletableFuture<Void> result = apiInstance.writeAssertions(storeId, authorizationModelId, body);
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#writeAssertions");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **authorizationModelId** | **String**|  | |
| **body** | [**WriteAssertionsRequest**](WriteAssertionsRequest.md)|  | |

### Return type


CompletableFuture<void> (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## writeAssertionsWithHttpInfo

> CompletableFuture<ApiResponse<Void>> writeAssertions writeAssertionsWithHttpInfo(storeId, authorizationModelId, body)

Upsert assertions for an authorization model ID

The WriteAssertions API will upsert new assertions for an authorization model id, or overwrite the existing ones. An assertion is an object that contains a tuple key, and the expectation of whether a call to the Check API of that tuple key will return true or false. 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        String authorizationModelId = "authorizationModelId_example"; // String | 
        WriteAssertionsRequest body = new WriteAssertionsRequest(); // WriteAssertionsRequest | 
        try {
            CompletableFuture<ApiResponse<Void>> response = apiInstance.writeAssertionsWithHttpInfo(storeId, authorizationModelId, body);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#writeAssertions");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#writeAssertions");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **authorizationModelId** | **String**|  | |
| **body** | [**WriteAssertionsRequest**](WriteAssertionsRequest.md)|  | |

### Return type


CompletableFuture<ApiResponse<Void>>

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |


## writeAuthorizationModel

> CompletableFuture<WriteAuthorizationModelResponse> writeAuthorizationModel(storeId, body)

Create a new authorization model

The WriteAuthorizationModel API will add a new authorization model to a store. Each item in the &#x60;type_definitions&#x60; array is a type definition as specified in the field &#x60;type_definition&#x60;. The response will return the authorization model&#39;s ID in the &#x60;id&#x60; field.  ## Example To add an authorization model with &#x60;user&#x60; and &#x60;document&#x60; type definitions, call POST authorization-models API with the body:  &#x60;&#x60;&#x60;json {   \&quot;type_definitions\&quot;:[     {       \&quot;type\&quot;:\&quot;user\&quot;     },     {       \&quot;type\&quot;:\&quot;document\&quot;,       \&quot;relations\&quot;:{         \&quot;reader\&quot;:{           \&quot;union\&quot;:{             \&quot;child\&quot;:[               {                 \&quot;this\&quot;:{}               },               {                 \&quot;computedUserset\&quot;:{                   \&quot;object\&quot;:\&quot;\&quot;,                   \&quot;relation\&quot;:\&quot;writer\&quot;                 }               }             ]           }         },         \&quot;writer\&quot;:{           \&quot;this\&quot;:{}         }       }     }   ] } &#x60;&#x60;&#x60; OpenFGA&#39;s response will include the version id for this authorization model, which will look like  &#x60;&#x60;&#x60; {\&quot;authorization_model_id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot;} &#x60;&#x60;&#x60; 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        WriteAuthorizationModelRequest body = new WriteAuthorizationModelRequest(); // WriteAuthorizationModelRequest | 
        try {
            CompletableFuture<WriteAuthorizationModelResponse> result = apiInstance.writeAuthorizationModel(storeId, body);
            System.out.println(result.get());
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#writeAuthorizationModel");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**WriteAuthorizationModelRequest**](WriteAuthorizationModelRequest.md)|  | |

### Return type

CompletableFuture<[**WriteAuthorizationModelResponse**](WriteAuthorizationModelResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

## writeAuthorizationModelWithHttpInfo

> CompletableFuture<ApiResponse<WriteAuthorizationModelResponse>> writeAuthorizationModel writeAuthorizationModelWithHttpInfo(storeId, body)

Create a new authorization model

The WriteAuthorizationModel API will add a new authorization model to a store. Each item in the &#x60;type_definitions&#x60; array is a type definition as specified in the field &#x60;type_definition&#x60;. The response will return the authorization model&#39;s ID in the &#x60;id&#x60; field.  ## Example To add an authorization model with &#x60;user&#x60; and &#x60;document&#x60; type definitions, call POST authorization-models API with the body:  &#x60;&#x60;&#x60;json {   \&quot;type_definitions\&quot;:[     {       \&quot;type\&quot;:\&quot;user\&quot;     },     {       \&quot;type\&quot;:\&quot;document\&quot;,       \&quot;relations\&quot;:{         \&quot;reader\&quot;:{           \&quot;union\&quot;:{             \&quot;child\&quot;:[               {                 \&quot;this\&quot;:{}               },               {                 \&quot;computedUserset\&quot;:{                   \&quot;object\&quot;:\&quot;\&quot;,                   \&quot;relation\&quot;:\&quot;writer\&quot;                 }               }             ]           }         },         \&quot;writer\&quot;:{           \&quot;this\&quot;:{}         }       }     }   ] } &#x60;&#x60;&#x60; OpenFGA&#39;s response will include the version id for this authorization model, which will look like  &#x60;&#x60;&#x60; {\&quot;authorization_model_id\&quot;: \&quot;01G50QVV17PECNVAHX1GG4Y5NC\&quot;} &#x60;&#x60;&#x60; 

### Example

```java
// Import classes:
import dev.openfga.sdk.api.client.ApiClient;
import dev.openfga.sdk.errors.ApiException;
import dev.openfga.sdk.api.client.ApiResponse;
import dev.openfga.sdk.api.configuration.Configuration;
import dev.openfga.sdk.api.client.models.*;
import dev.openfga.sdk.api.OpenFgaApi;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        OpenFgaApi apiInstance = new OpenFgaApi(defaultClient);
        String storeId = "storeId_example"; // String | 
        WriteAuthorizationModelRequest body = new WriteAuthorizationModelRequest(); // WriteAuthorizationModelRequest | 
        try {
            CompletableFuture<ApiResponse<WriteAuthorizationModelResponse>> response = apiInstance.writeAuthorizationModelWithHttpInfo(storeId, body);
            System.out.println("Status code: " + response.get().getStatusCode());
            System.out.println("Response headers: " + response.get().getHeaders());
            System.out.println("Response body: " + response.get().getData());
        } catch (InterruptedException | ExecutionException e) {
            ApiException apiException = (ApiException)e.getCause();
            System.err.println("Exception when calling OpenFgaApi#writeAuthorizationModel");
            System.err.println("Status code: " + apiException.getCode());
            System.err.println("Response headers: " + apiException.getResponseHeaders());
            System.err.println("Reason: " + apiException.getResponseBody());
            e.printStackTrace();
        } catch (ApiException e) {
            System.err.println("Exception when calling OpenFgaApi#writeAuthorizationModel");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **storeId** | **String**|  | |
| **body** | [**WriteAuthorizationModelRequest**](WriteAuthorizationModelRequest.md)|  | |

### Return type

CompletableFuture<ApiResponse<[**WriteAuthorizationModelResponse**](WriteAuthorizationModelResponse.md)>>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | A successful response. |  -  |
| **400** | Request failed due to invalid input. |  -  |
| **404** | Request failed due to incorrect path. |  -  |
| **500** | Request failed due to internal server error. |  -  |

