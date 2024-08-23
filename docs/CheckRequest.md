

# CheckRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**tupleKey** | [**CheckRequestTupleKey**](CheckRequestTupleKey.md) |  |  |
|**contextualTuples** | [**ContextualTupleKeys**](ContextualTupleKeys.md) |  |  [optional] |
|**authorizationModelId** | **String** |  |  [optional] |
|**trace** | **Boolean** | Defaults to false. Making it true has performance implications. |  [optional] [readonly] |
|**context** | **Object** | Additional request context that will be used to evaluate any ABAC conditions encountered in the query evaluation. |  [optional] |
|**consistency** | **ConsistencyPreference** |  |  [optional] |



