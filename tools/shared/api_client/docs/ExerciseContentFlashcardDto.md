# ExerciseContentFlashcardDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**front** | [**ExerciseContentDto**](ExerciseContentDto.md) |  | [optional] 
**back** | [**ExerciseContentDto**](ExerciseContentDto.md) |  | [optional] 
**initial_side** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_content_flashcard_dto import ExerciseContentFlashcardDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseContentFlashcardDto from a JSON string
exercise_content_flashcard_dto_instance = ExerciseContentFlashcardDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseContentFlashcardDto.to_json())

# convert the object into a dict
exercise_content_flashcard_dto_dict = exercise_content_flashcard_dto_instance.to_dict()
# create an instance of ExerciseContentFlashcardDto from a dict
exercise_content_flashcard_dto_from_dict = ExerciseContentFlashcardDto.from_dict(exercise_content_flashcard_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


