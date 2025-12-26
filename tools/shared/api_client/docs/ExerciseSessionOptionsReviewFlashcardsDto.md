# ExerciseSessionOptionsReviewFlashcardsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**initial_flashcard_language_id** | **str** |  | [optional] 
**available_flashcard_languages** | [**List[ExerciseSessionOptionsLanguageOptionDto]**](ExerciseSessionOptionsLanguageOptionDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_session_options_review_flashcards_dto import ExerciseSessionOptionsReviewFlashcardsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseSessionOptionsReviewFlashcardsDto from a JSON string
exercise_session_options_review_flashcards_dto_instance = ExerciseSessionOptionsReviewFlashcardsDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseSessionOptionsReviewFlashcardsDto.to_json())

# convert the object into a dict
exercise_session_options_review_flashcards_dto_dict = exercise_session_options_review_flashcards_dto_instance.to_dict()
# create an instance of ExerciseSessionOptionsReviewFlashcardsDto from a dict
exercise_session_options_review_flashcards_dto_from_dict = ExerciseSessionOptionsReviewFlashcardsDto.from_dict(exercise_session_options_review_flashcards_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


