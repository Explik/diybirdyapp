# ExerciseContentTextDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**text** | **str** |  | [optional] 
**pronunciation_url** | **str** |  | [optional] 
**transcription** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_content_text_dto import ExerciseContentTextDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseContentTextDto from a JSON string
exercise_content_text_dto_instance = ExerciseContentTextDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseContentTextDto.to_json())

# convert the object into a dict
exercise_content_text_dto_dict = exercise_content_text_dto_instance.to_dict()
# create an instance of ExerciseContentTextDto from a dict
exercise_content_text_dto_from_dict = ExerciseContentTextDto.from_dict(exercise_content_text_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


