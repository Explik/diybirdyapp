# ConfigurationIdentifierDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**type** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.configuration_identifier_dto import ConfigurationIdentifierDto

# TODO update the JSON string below
json = "{}"
# create an instance of ConfigurationIdentifierDto from a JSON string
configuration_identifier_dto_instance = ConfigurationIdentifierDto.from_json(json)
# print the JSON string representation of the object
print(ConfigurationIdentifierDto.to_json())

# convert the object into a dict
configuration_identifier_dto_dict = configuration_identifier_dto_instance.to_dict()
# create an instance of ConfigurationIdentifierDto from a dict
configuration_identifier_dto_from_dict = ConfigurationIdentifierDto.from_dict(configuration_identifier_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


