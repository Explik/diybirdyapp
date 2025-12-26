# ExerciseSessionOptionsLearnFlashcardsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**answer_language_ids** | **List[str]** |  | [optional] 
**include_review_exercises** | **bool** |  | [optional] 
**include_multiple_choice_exercises** | **bool** |  | [optional] 
**include_writing_exercises** | **bool** |  | [optional] 
**include_listening_exercises** | **bool** |  | [optional] 
**include_pronunciation_exercises** | **bool** |  | [optional] 
**retype_correct_answer_enabled** | **bool** |  | [optional] 
**available_answer_languages** | [**List[ExerciseSessionOptionsLanguageOptionDto]**](ExerciseSessionOptionsLanguageOptionDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_session_options_learn_flashcards_dto import ExerciseSessionOptionsLearnFlashcardsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseSessionOptionsLearnFlashcardsDto from a JSON string
exercise_session_options_learn_flashcards_dto_instance = ExerciseSessionOptionsLearnFlashcardsDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseSessionOptionsLearnFlashcardsDto.to_json())

# convert the object into a dict
exercise_session_options_learn_flashcards_dto_dict = exercise_session_options_learn_flashcards_dto_instance.to_dict()
# create an instance of ExerciseSessionOptionsLearnFlashcardsDto from a dict
exercise_session_options_learn_flashcards_dto_from_dict = ExerciseSessionOptionsLearnFlashcardsDto.from_dict(exercise_session_options_learn_flashcards_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


