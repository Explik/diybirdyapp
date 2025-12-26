# ExerciseSessionDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**type** | **str** |  | 
**completed** | **bool** |  | [optional] 
**flashcard_deck_id** | **str** |  | [optional] 
**exercise** | [**ExerciseDto**](ExerciseDto.md) |  | [optional] 
**progress** | [**ExerciseSessionProgressDto**](ExerciseSessionProgressDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_session_dto import ExerciseSessionDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseSessionDto from a JSON string
exercise_session_dto_instance = ExerciseSessionDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseSessionDto.to_json())

# convert the object into a dict
exercise_session_dto_dict = exercise_session_dto_instance.to_dict()
# create an instance of ExerciseSessionDto from a dict
exercise_session_dto_from_dict = ExerciseSessionDto.from_dict(exercise_session_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


