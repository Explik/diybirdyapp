# ExerciseInputSelectOptionsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**options** | [**List[SelectOptionInputBaseOption]**](SelectOptionInputBaseOption.md) |  | [optional] 
**option_type** | **str** |  | [optional] 
**value** | **str** |  | [optional] 
**feedback** | [**SelectOptionsInputFeedbackDto**](SelectOptionsInputFeedbackDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_select_options_dto import ExerciseInputSelectOptionsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputSelectOptionsDto from a JSON string
exercise_input_select_options_dto_instance = ExerciseInputSelectOptionsDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputSelectOptionsDto.to_json())

# convert the object into a dict
exercise_input_select_options_dto_dict = exercise_input_select_options_dto_instance.to_dict()
# create an instance of ExerciseInputSelectOptionsDto from a dict
exercise_input_select_options_dto_from_dict = ExerciseInputSelectOptionsDto.from_dict(exercise_input_select_options_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


