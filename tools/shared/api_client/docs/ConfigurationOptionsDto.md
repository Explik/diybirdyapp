# ConfigurationOptionsDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**selection** | **str** |  | [optional] 
**selected_options** | **List[str]** |  | [optional] 
**available_options** | [**List[Option]**](Option.md) |  | [optional] 
**last_selection** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.configuration_options_dto import ConfigurationOptionsDto

# TODO update the JSON string below
json = "{}"
# create an instance of ConfigurationOptionsDto from a JSON string
configuration_options_dto_instance = ConfigurationOptionsDto.from_json(json)
# print the JSON string representation of the object
print(ConfigurationOptionsDto.to_json())

# convert the object into a dict
configuration_options_dto_dict = configuration_options_dto_instance.to_dict()
# create an instance of ConfigurationOptionsDto from a dict
configuration_options_dto_from_dict = ConfigurationOptionsDto.from_dict(configuration_options_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


