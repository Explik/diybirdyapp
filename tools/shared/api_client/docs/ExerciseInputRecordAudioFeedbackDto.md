# ExerciseInputRecordAudioFeedbackDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**correct_values** | **List[str]** |  | 
**incorrect_values** | **List[str]** |  | 

## Example

```python
from openapi_client.models.exercise_input_record_audio_feedback_dto import ExerciseInputRecordAudioFeedbackDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputRecordAudioFeedbackDto from a JSON string
exercise_input_record_audio_feedback_dto_instance = ExerciseInputRecordAudioFeedbackDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputRecordAudioFeedbackDto.to_json())

# convert the object into a dict
exercise_input_record_audio_feedback_dto_dict = exercise_input_record_audio_feedback_dto_instance.to_dict()
# create an instance of ExerciseInputRecordAudioFeedbackDto from a dict
exercise_input_record_audio_feedback_dto_from_dict = ExerciseInputRecordAudioFeedbackDto.from_dict(exercise_input_record_audio_feedback_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


