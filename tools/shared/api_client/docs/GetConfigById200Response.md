# GetConfigById200Response


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
from openapi_client.models.get_config_by_id200_response import GetConfigById200Response

# TODO update the JSON string below
json = "{}"
# create an instance of GetConfigById200Response from a JSON string
get_config_by_id200_response_instance = GetConfigById200Response.from_json(json)
# print the JSON string representation of the object
print(GetConfigById200Response.to_json())

# convert the object into a dict
get_config_by_id200_response_dict = get_config_by_id200_response_instance.to_dict()
# create an instance of GetConfigById200Response from a dict
get_config_by_id200_response_from_dict = GetConfigById200Response.from_dict(get_config_by_id200_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


