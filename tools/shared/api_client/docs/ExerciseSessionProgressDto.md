# ExerciseSessionProgressDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [optional] 
**percentage** | **int** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_session_progress_dto import ExerciseSessionProgressDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseSessionProgressDto from a JSON string
exercise_session_progress_dto_instance = ExerciseSessionProgressDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseSessionProgressDto.to_json())

# convert the object into a dict
exercise_session_progress_dto_dict = exercise_session_progress_dto_instance.to_dict()
# create an instance of ExerciseSessionProgressDto from a dict
exercise_session_progress_dto_from_dict = ExerciseSessionProgressDto.from_dict(exercise_session_progress_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


