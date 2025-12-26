# ExerciseInputArrangeTextOptionsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**value** | **str** |  | [optional] 
**options** | [**List[ArrangeTextOption]**](ArrangeTextOption.md) |  | 

## Example

```python
from openapi_client.models.exercise_input_arrange_text_options_dto import ExerciseInputArrangeTextOptionsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ExerciseInputArrangeTextOptionsDto from a JSON string
exercise_input_arrange_text_options_dto_instance = ExerciseInputArrangeTextOptionsDto.from_json(json)
# print the JSON string representation of the object
print(ExerciseInputArrangeTextOptionsDto.to_json())

# convert the object into a dict
exercise_input_arrange_text_options_dto_dict = exercise_input_arrange_text_options_dto_instance.to_dict()
# create an instance of ExerciseInputArrangeTextOptionsDto from a dict
exercise_input_arrange_text_options_dto_from_dict = ExerciseInputArrangeTextOptionsDto.from_dict(exercise_input_arrange_text_options_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


