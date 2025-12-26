# ExerciseDtoContent


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | 
**type** | **str** |  | 
**front** | [**ExerciseContentDto**](ExerciseContentDto.md) |  | [optional] 
**back** | [**ExerciseContentDto**](ExerciseContentDto.md) |  | [optional] 
**initial_side** | **str** |  | [optional] 
**content** | [**ExerciseContentDto**](ExerciseContentDto.md) |  | [optional] 
**image_url** | **str** |  | [optional] 
**text** | **str** |  | [optional] 
**pronunciation_url** | **str** |  | [optional] 
**transcription** | **str** |  | [optional] 
**video_url** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_dto_content import ExerciseDtoContent

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseDtoContent from a JSON string
exercise_dto_content_instance = ExerciseDtoContent.from_json(json)
# print the JSON string representation of the object
print(ExerciseDtoContent.to_json())

# convert the object into a dict
exercise_dto_content_dict = exercise_dto_content_instance.to_dict()
# create an instance of ExerciseDtoContent from a dict
exercise_dto_content_from_dict = ExerciseDtoContent.from_dict(exercise_dto_content_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


