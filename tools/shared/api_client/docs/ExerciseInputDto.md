# ExerciseInputDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**type** | **str** |  | [optional] 
**session_id** | **str** |  | 

## Example

```python
from openapi_client.models.exercise_input_dto import ExerciseInputDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputDto from a JSON string
exercise_input_dto_instance = ExerciseInputDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputDto.to_json())

# convert the object into a dict
exercise_input_dto_dict = exercise_input_dto_instance.to_dict()
# create an instance of ExerciseInputDto from a dict
exercise_input_dto_from_dict = ExerciseInputDto.from_dict(exercise_input_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


