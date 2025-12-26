# WritePlaceholdersFeedbackDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**correct_placeholders_ids** | **List[str]** |  | 
**incorrect_placeholders_ids** | **List[str]** |  | 

## Example

```python
from openapi_client.models.write_placeholders_feedback_dto import WritePlaceholdersFeedbackDto

# TODO update the JSON string below
json = "{}"
# create an instance of WritePlaceholdersFeedbackDto from a JSON string
write_placeholders_feedback_dto_instance = WritePlaceholdersFeedbackDto.from_json(json)
# print the JSON string representation of the object
print(WritePlaceholdersFeedbackDto.to_json())

# convert the object into a dict
write_placeholders_feedback_dto_dict = write_placeholders_feedback_dto_instance.to_dict()
# create an instance of WritePlaceholdersFeedbackDto from a dict
write_placeholders_feedback_dto_from_dict = WritePlaceholdersFeedbackDto.from_dict(write_placeholders_feedback_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


