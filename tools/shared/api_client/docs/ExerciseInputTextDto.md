# ExerciseInputTextDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**text** | **str** |  | [optional] 
**feedback** | [**ExerciseInputFeedbackTextDto**](ExerciseInputFeedbackTextDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_text_dto import ExerciseInputTextDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputTextDto from a JSON string
exercise_input_text_dto_instance = ExerciseInputTextDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputTextDto.to_json())

# convert the object into a dict
exercise_input_text_dto_dict = exercise_input_text_dto_instance.to_dict()
# create an instance of ExerciseInputTextDto from a dict
exercise_input_text_dto_from_dict = ExerciseInputTextDto.from_dict(exercise_input_text_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


