# ExerciseSessionOptionsSelectFlashcardsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**initial_flashcard_language_id** | **str** |  | [optional] 
**available_flashcard_languages** | [**List[ExerciseSessionOptionsLanguageOptionDto]**](ExerciseSessionOptionsLanguageOptionDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_session_options_select_flashcards_dto import ExerciseSessionOptionsSelectFlashcardsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseSessionOptionsSelectFlashcardsDto from a JSON string
exercise_session_options_select_flashcards_dto_instance = ExerciseSessionOptionsSelectFlashcardsDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseSessionOptionsSelectFlashcardsDto.to_json())

# convert the object into a dict
exercise_session_options_select_flashcards_dto_dict = exercise_session_options_select_flashcards_dto_instance.to_dict()
# create an instance of ExerciseSessionOptionsSelectFlashcardsDto from a dict
exercise_session_options_select_flashcards_dto_from_dict = ExerciseSessionOptionsSelectFlashcardsDto.from_dict(exercise_session_options_select_flashcards_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


