# ExerciseInputSelectPlaceholdersDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parts** | [**List[SelectPlaceholdersInputPart]**](SelectPlaceholdersInputPart.md) |  | 
**options** | [**List[SelectPlaceholdersInputOption]**](SelectPlaceholdersInputOption.md) |  | 

## Example

```python
from openapi_client.models.exercise_input_select_placeholders_dto import ExerciseInputSelectPlaceholdersDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputSelectPlaceholdersDto from a JSON string
exercise_input_select_placeholders_dto_instance = ExerciseInputSelectPlaceholdersDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputSelectPlaceholdersDto.to_json())

# convert the object into a dict
exercise_input_select_placeholders_dto_dict = exercise_input_select_placeholders_dto_instance.to_dict()
# create an instance of ExerciseInputSelectPlaceholdersDto from a dict
exercise_input_select_placeholders_dto_from_dict = ExerciseInputSelectPlaceholdersDto.from_dict(exercise_input_select_placeholders_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


