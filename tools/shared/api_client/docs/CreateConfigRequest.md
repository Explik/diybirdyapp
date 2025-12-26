# CreateConfigRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**type** | **str** |  | [optional] 
**language_id** | **str** |  | [optional] 
**language_code** | **str** |  | [optional] 
**voice_name** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.create_config_request import CreateConfigRequest

# TODO update the JSON string below
json = "{}"
# create an instance of CreateConfigRequest from a JSON string
create_config_request_instance = CreateConfigRequest.from_json(json)
# print the JSON string representation of the object
print(CreateConfigRequest.to_json())

# convert the object into a dict
create_config_request_dict = create_config_request_instance.to_dict()
# create an instance of CreateConfigRequest from a dict
create_config_request_from_dict = CreateConfigRequest.from_dict(create_config_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


