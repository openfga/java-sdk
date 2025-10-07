

# WriteRequestWrites


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**tupleKeys** | [**List&lt;TupleKey&gt;**](TupleKey.md) |  |  |
|**onDuplicate** | [**OnDuplicateEnum**](#OnDuplicateEnum) | On &#39;error&#39; ( or unspecified ), the API returns an error if an identical tuple already exists. On &#39;ignore&#39;, identical writes are treated as no-ops (matching on user, relation, object, and RelationshipCondition). |  [optional] |



## Enum: OnDuplicateEnum

| Name | Value |
|---- | -----|
| ERROR | &quot;error&quot; |
| IGNORE | &quot;ignore&quot; |
| UNKNOWN_DEFAULT_OPEN_API | &quot;unknown_default_open_api&quot; |



