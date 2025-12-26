# Update1Request


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**flashcard** | [**FlashcardDto**](FlashcardDto.md) |  | 
**files** | **List[bytearray]** |  | [optional] 

## Example

```python
from openapi_client.models.update1_request import Update1Request

# TODO update the JSON string below
json = "{}"
# create an instance of Update1Request from a JSON string
update1_request_instance = Update1Request.from_json(json)
# print the JSON string representation of the object
print(Update1Request.to_json())

# convert the object into a dict
update1_request_dict = update1_request_instance.to_dict()
# create an instance of Update1Request from a dict
update1_request_from_dict = Update1Request.from_dict(update1_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


