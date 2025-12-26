# SubmitAnswerRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**type** | **str** |  | [optional] 
**session_id** | **str** |  | 
**value** | **str** |  | [optional] 
**options** | [**List[SelectPlaceholdersInputOption]**](SelectPlaceholdersInputOption.md) |  | 
**left_option_type** | **str** |  | 
**right_option_type** | **str** |  | 
**left_options** | [**List[PairOptionsInputOptionDto]**](PairOptionsInputOptionDto.md) |  | 
**right_options** | [**List[PairOptionsInputOptionDto]**](PairOptionsInputOptionDto.md) |  | 
**feedback** | [**ExerciseInputFeedbackTextDto**](ExerciseInputFeedbackTextDto.md) |  | [optional] 
**url** | **str** |  | 
**option_type** | **str** |  | [optional] 
**parts** | [**List[WritePlaceholdersPartDto]**](WritePlaceholdersPartDto.md) |  | 
**rating** | **str** |  | 
**text** | **str** |  | 

## Example

```python
from openapi_client.models.submit_answer_request import SubmitAnswerRequest

# TODO update the JSON string below
json = "{}"
# create an instance of SubmitAnswerRequest from a JSON string
submit_answer_request_instance = SubmitAnswerRequest.from_json(json)
# print the JSON string representation of the object
print(SubmitAnswerRequest.to_json())

# convert the object into a dict
submit_answer_request_dict = submit_answer_request_instance.to_dict()
# create an instance of SubmitAnswerRequest from a dict
submit_answer_request_from_dict = SubmitAnswerRequest.from_dict(submit_answer_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


