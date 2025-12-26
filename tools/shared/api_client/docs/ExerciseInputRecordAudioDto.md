# ExerciseInputRecordAudioDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**url** | **str** |  | 
**feedback** | [**ExerciseInputRecordAudioFeedbackDto**](ExerciseInputRecordAudioFeedbackDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_record_audio_dto import ExerciseInputRecordAudioDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputRecordAudioDto from a JSON string
exercise_input_record_audio_dto_instance = ExerciseInputRecordAudioDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputRecordAudioDto.to_json())

# convert the object into a dict
exercise_input_record_audio_dto_dict = exercise_input_record_audio_dto_instance.to_dict()
# create an instance of ExerciseInputRecordAudioDto from a dict
exercise_input_record_audio_dto_from_dict = ExerciseInputRecordAudioDto.from_dict(exercise_input_record_audio_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


