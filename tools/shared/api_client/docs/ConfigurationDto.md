# ConfigurationDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**type** | **str** |  | [optional] 
**language_id** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.configuration_dto import ConfigurationDto

# TODO update the JSON string below
json = "{}"
# create an instance of ConfigurationDto from a JSON string
configuration_dto_instance = ConfigurationDto.from_json(json)
# print the JSON string representation of the object
print(ConfigurationDto.to_json())

# convert the object into a dict
configuration_dto_dict = configuration_dto_instance.to_dict()
# create an instance of ConfigurationDto from a dict
configuration_dto_from_dict = ConfigurationDto.from_dict(configuration_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


