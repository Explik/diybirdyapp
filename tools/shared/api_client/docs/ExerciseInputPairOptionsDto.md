# ExerciseInputPairOptionsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**left_option_type** | **str** |  | 
**right_option_type** | **str** |  | 
**left_options** | [**List[PairOptionsInputOptionDto]**](PairOptionsInputOptionDto.md) |  | 
**right_options** | [**List[PairOptionsInputOptionDto]**](PairOptionsInputOptionDto.md) |  | 
**feedback** | [**PairOptionsInputFeedback**](PairOptionsInputFeedback.md) |  | [optional] 

## Example

```python
from openapi_client.models.exercise_input_pair_options_dto import ExerciseInputPairOptionsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputPairOptionsDto from a JSON string
exercise_input_pair_options_dto_instance = ExerciseInputPairOptionsDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputPairOptionsDto.to_json())

# convert the object into a dict
exercise_input_pair_options_dto_dict = exercise_input_pair_options_dto_instance.to_dict()
# create an instance of ExerciseInputPairOptionsDto from a dict
exercise_input_pair_options_dto_from_dict = ExerciseInputPairOptionsDto.from_dict(exercise_input_pair_options_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


