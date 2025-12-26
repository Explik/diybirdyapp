# ExerciseInputFeedbackMultipleChoiceTextFeedbackDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**correct_option_ids** | **List[str]** |  | [optional] 
**incorrect_option_ids** | **List[str]** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_feedback_multiple_choice_text_feedback_dto import ExerciseInputFeedbackMultipleChoiceTextFeedbackDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputFeedbackMultipleChoiceTextFeedbackDto from a JSON string
exercise_input_feedback_multiple_choice_text_feedback_dto_instance = ExerciseInputFeedbackMultipleChoiceTextFeedbackDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputFeedbackMultipleChoiceTextFeedbackDto.to_json())

# convert the object into a dict
exercise_input_feedback_multiple_choice_text_feedback_dto_dict = exercise_input_feedback_multiple_choice_text_feedback_dto_instance.to_dict()
# create an instance of ExerciseInputFeedbackMultipleChoiceTextFeedbackDto from a dict
exercise_input_feedback_multiple_choice_text_feedback_dto_from_dict = ExerciseInputFeedbackMultipleChoiceTextFeedbackDto.from_dict(exercise_input_feedback_multiple_choice_text_feedback_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


