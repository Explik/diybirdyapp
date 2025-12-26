# ExerciseSessionOptionsWriteFlashcardsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**answer_language_id** | **str** |  | [optional] 
**available_answer_languages** | [**List[ExerciseSessionOptionsLanguageOptionDto]**](ExerciseSessionOptionsLanguageOptionDto.md) |  | [optional] 
**retype_correct_answer_enabled** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_session_options_write_flashcards_dto import ExerciseSessionOptionsWriteFlashcardsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseSessionOptionsWriteFlashcardsDto from a JSON string
exercise_session_options_write_flashcards_dto_instance = ExerciseSessionOptionsWriteFlashcardsDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseSessionOptionsWriteFlashcardsDto.to_json())

# convert the object into a dict
exercise_session_options_write_flashcards_dto_dict = exercise_session_options_write_flashcards_dto_instance.to_dict()
# create an instance of ExerciseSessionOptionsWriteFlashcardsDto from a dict
exercise_session_options_write_flashcards_dto_from_dict = ExerciseSessionOptionsWriteFlashcardsDto.from_dict(exercise_session_options_write_flashcards_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


