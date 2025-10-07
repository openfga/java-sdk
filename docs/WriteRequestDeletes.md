

# WriteRequestDeletes


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**tupleKeys** | [**List&lt;TupleKeyWithoutCondition&gt;**](TupleKeyWithoutCondition.md) |  |  |
|**onMissing** | [**OnMissingEnum**](#OnMissingEnum) | On &#39;error&#39;, the API returns an error when deleting a tuple that does not exist. On &#39;ignore&#39;, deletes of non-existent tuples are treated as no-ops. |  [optional] |



## Enum: OnMissingEnum

| Name | Value |
|---- | -----|
| ERROR | &quot;error&quot; |
| IGNORE | &quot;ignore&quot; |
| UNKNOWN_DEFAULT_OPEN_API | &quot;unknown_default_open_api&quot; |



