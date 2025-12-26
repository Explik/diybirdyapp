# SubmitAnswerRichRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**answer** | [**SubmitAnswerRequest**](SubmitAnswerRequest.md) |  | 
**files** | **List[bytearray]** |  | [optional] 

## Example

```python
from openapi_client.models.submit_answer_rich_request import SubmitAnswerRichRequest

# TODO update the JSON string below
json = "{}"
# create an instance of SubmitAnswerRichRequest from a JSON string
submit_answer_rich_request_instance = SubmitAnswerRichRequest.from_json(json)
# print the JSON string representation of the object
print(SubmitAnswerRichRequest.to_json())

# convert the object into a dict
submit_answer_rich_request_dict = submit_answer_rich_request_instance.to_dict()
# create an instance of SubmitAnswerRichRequest from a dict
submit_answer_rich_request_from_dict = SubmitAnswerRichRequest.from_dict(submit_answer_rich_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


