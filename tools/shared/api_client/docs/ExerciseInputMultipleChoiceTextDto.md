# ExerciseInputMultipleChoiceTextDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**feedback** | [**ExerciseInputFeedbackMultipleChoiceTextFeedbackDto**](ExerciseInputFeedbackMultipleChoiceTextFeedbackDto.md) |  | [optional] 
**options** | [**List[Option]**](Option.md) |  | [optional] 
**value** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_multiple_choice_text_dto import ExerciseInputMultipleChoiceTextDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputMultipleChoiceTextDto from a JSON string
exercise_input_multiple_choice_text_dto_instance = ExerciseInputMultipleChoiceTextDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputMultipleChoiceTextDto.to_json())

# convert the object into a dict
exercise_input_multiple_choice_text_dto_dict = exercise_input_multiple_choice_text_dto_instance.to_dict()
# create an instance of ExerciseInputMultipleChoiceTextDto from a dict
exercise_input_multiple_choice_text_dto_from_dict = ExerciseInputMultipleChoiceTextDto.from_dict(exercise_input_multiple_choice_text_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


