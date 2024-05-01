

# ListUsersRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**authorizationModelId** | **String** |  |  [optional] |
|**_object** | [**FgaObject**](FgaObject.md) |  |  |
|**relation** | **String** |  |  |
|**userFilters** | [**List&lt;UserTypeFilter&gt;**](UserTypeFilter.md) | The type of results returned. Only accepts exactly one value. |  |
|**contextualTuples** | [**List&lt;TupleKey&gt;**](TupleKey.md) |  |  [optional] |
|**context** | **Object** | Additional request context that will be used to evaluate any ABAC conditions encountered in the query evaluation. |  [optional] |



