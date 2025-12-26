# UpdateConfigRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [optional] 
**text_to_speech_enabled** | **bool** |  | [optional] 
**initial_flashcard_language_id** | **str** |  | [optional] 
**available_flashcard_languages** | [**List[ExerciseSessionOptionsLanguageOptionDto]**](ExerciseSessionOptionsLanguageOptionDto.md) |  | [optional] 
**answer_language_id** | **str** |  | [optional] 
**available_answer_languages** | [**List[ExerciseSessionOptionsLanguageOptionDto]**](ExerciseSessionOptionsLanguageOptionDto.md) |  | [optional] 
**retype_correct_answer_enabled** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.update_config_request import UpdateConfigRequest

# TODO update the JSON string below
json = "{}"
# create an instance of UpdateConfigRequest from a JSON string
update_config_request_instance = UpdateConfigRequest.from_json(json)
# print the JSON string representation of the object
print(UpdateConfigRequest.to_json())

# convert the object into a dict
update_config_request_dict = update_config_request_instance.to_dict()
# create an instance of UpdateConfigRequest from a dict
update_config_request_from_dict = UpdateConfigRequest.from_dict(update_config_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


