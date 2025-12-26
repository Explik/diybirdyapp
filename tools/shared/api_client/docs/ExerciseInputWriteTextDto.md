# ExerciseInputWriteTextDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**text** | **str** |  | 
**feedback** | [**ExerciseInputFeedbackTextDto**](ExerciseInputFeedbackTextDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_write_text_dto import ExerciseInputWriteTextDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputWriteTextDto from a JSON string
exercise_input_write_text_dto_instance = ExerciseInputWriteTextDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputWriteTextDto.to_json())

# convert the object into a dict
exercise_input_write_text_dto_dict = exercise_input_write_text_dto_instance.to_dict()
# create an instance of ExerciseInputWriteTextDto from a dict
exercise_input_write_text_dto_from_dict = ExerciseInputWriteTextDto.from_dict(exercise_input_write_text_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


