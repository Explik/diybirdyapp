# ExerciseDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | 
**type** | **str** |  | 
**content** | [**ExerciseDtoContent**](ExerciseDtoContent.md) |  | [optional] 
**input** | [**SubmitAnswerRequest**](SubmitAnswerRequest.md) |  | [optional] 
**feedback** | [**ExerciseFeedbackDto**](ExerciseFeedbackDto.md) |  | [optional] 
**properties** | [**ExerciseDtoProperties**](ExerciseDtoProperties.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_dto import ExerciseDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseDto from a JSON string
exercise_dto_instance = ExerciseDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseDto.to_json())

# convert the object into a dict
exercise_dto_dict = exercise_dto_instance.to_dict()
# create an instance of ExerciseDto from a dict
exercise_dto_from_dict = ExerciseDto.from_dict(exercise_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


