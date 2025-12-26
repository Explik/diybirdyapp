# ExerciseInputFeedbackTextDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**correct_values** | **List[str]** |  | 
**incorrect_values** | **List[str]** |  | 
**is_retype_answer_enabled** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_feedback_text_dto import ExerciseInputFeedbackTextDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputFeedbackTextDto from a JSON string
exercise_input_feedback_text_dto_instance = ExerciseInputFeedbackTextDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputFeedbackTextDto.to_json())

# convert the object into a dict
exercise_input_feedback_text_dto_dict = exercise_input_feedback_text_dto_instance.to_dict()
# create an instance of ExerciseInputFeedbackTextDto from a dict
exercise_input_feedback_text_dto_from_dict = ExerciseInputFeedbackTextDto.from_dict(exercise_input_feedback_text_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


