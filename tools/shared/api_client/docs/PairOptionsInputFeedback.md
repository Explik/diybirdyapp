# PairOptionsInputFeedback


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**correct_pairs** | [**List[PairOptionFeedbackPair]**](PairOptionFeedbackPair.md) |  | 
**incorrect_pairs** | [**List[PairOptionFeedbackPair]**](PairOptionFeedbackPair.md) |  | 

## Example

```python
from openapi_client.models.pair_options_input_feedback import PairOptionsInputFeedback

# TODO update the JSON string below
json = "{}"
# create an instance of PairOptionsInputFeedback from a JSON string
pair_options_input_feedback_instance = PairOptionsInputFeedback.from_json(json)
# print the JSON string representation of the object
print(PairOptionsInputFeedback.to_json())

# convert the object into a dict
pair_options_input_feedback_dict = pair_options_input_feedback_instance.to_dict()
# create an instance of PairOptionsInputFeedback from a dict
pair_options_input_feedback_from_dict = PairOptionsInputFeedback.from_dict(pair_options_input_feedback_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


