# ExerciseSessionProgressModel


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** |  | [optional] 
**percentage** | **int** |  | [optional] 

## Example

```python
from openapi_client.models.exercise_session_progress_model import ExerciseSessionProgressModel

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseSessionProgressModel from a JSON string
exercise_session_progress_model_instance = ExerciseSessionProgressModel.from_json(json)
# print the JSON string representation of the object
print(ExerciseSessionProgressModel.to_json())

# convert the object into a dict
exercise_session_progress_model_dict = exercise_session_progress_model_instance.to_dict()
# create an instance of ExerciseSessionProgressModel from a dict
exercise_session_progress_model_from_dict = ExerciseSessionProgressModel.from_dict(exercise_session_progress_model_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


