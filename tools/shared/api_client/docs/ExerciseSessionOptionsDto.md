# ExerciseSessionOptionsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [optional] 
**text_to_speech_enabled** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_session_options_dto import ExerciseSessionOptionsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseSessionOptionsDto from a JSON string
exercise_session_options_dto_instance = ExerciseSessionOptionsDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseSessionOptionsDto.to_json())

# convert the object into a dict
exercise_session_options_dto_dict = exercise_session_options_dto_instance.to_dict()
# create an instance of ExerciseSessionOptionsDto from a dict
exercise_session_options_dto_from_dict = ExerciseSessionOptionsDto.from_dict(exercise_session_options_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


