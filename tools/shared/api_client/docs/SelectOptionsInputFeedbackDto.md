# SelectOptionsInputFeedbackDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**correct_option_ids** | **List[str]** |  | 
**incorrect_option_ids** | **List[str]** |  | 
**is_retype_answer_enabled** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.select_options_input_feedback_dto import SelectOptionsInputFeedbackDto

# TODO update the JSON string below
json = "{}"
# create an instance of SelectOptionsInputFeedbackDto from a JSON string
select_options_input_feedback_dto_instance = SelectOptionsInputFeedbackDto.from_json(json)
# print the JSON string representation of the object
print(SelectOptionsInputFeedbackDto.to_json())

# convert the object into a dict
select_options_input_feedback_dto_dict = select_options_input_feedback_dto_instance.to_dict()
# create an instance of SelectOptionsInputFeedbackDto from a dict
select_options_input_feedback_dto_from_dict = SelectOptionsInputFeedbackDto.from_dict(select_options_input_feedback_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


