# ExerciseInputWritePlaceholdersDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parts** | [**List[WritePlaceholdersPartDto]**](WritePlaceholdersPartDto.md) |  | [optional] 
**feedback** | [**WritePlaceholdersFeedbackDto**](WritePlaceholdersFeedbackDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_write_placeholders_dto import ExerciseInputWritePlaceholdersDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputWritePlaceholdersDto from a JSON string
exercise_input_write_placeholders_dto_instance = ExerciseInputWritePlaceholdersDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputWritePlaceholdersDto.to_json())

# convert the object into a dict
exercise_input_write_placeholders_dto_dict = exercise_input_write_placeholders_dto_instance.to_dict()
# create an instance of ExerciseInputWritePlaceholdersDto from a dict
exercise_input_write_placeholders_dto_from_dict = ExerciseInputWritePlaceholdersDto.from_dict(exercise_input_write_placeholders_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


