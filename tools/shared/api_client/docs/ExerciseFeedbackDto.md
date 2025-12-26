# ExerciseFeedbackDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [optional] 
**state** | **str** |  | [optional] 
**message** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_feedback_dto import ExerciseFeedbackDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseFeedbackDto from a JSON string
exercise_feedback_dto_instance = ExerciseFeedbackDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseFeedbackDto.to_json())

# convert the object into a dict
exercise_feedback_dto_dict = exercise_feedback_dto_instance.to_dict()
# create an instance of ExerciseFeedbackDto from a dict
exercise_feedback_dto_from_dict = ExerciseFeedbackDto.from_dict(exercise_feedback_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


