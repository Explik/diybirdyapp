# WritePlaceholdersPartDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | 
**type** | **str** |  | 
**value** | **str** |  | [optional] 
**size** | **float** |  | [optional] 

## Example

```python
from openapi_client.models.write_placeholders_part_dto import WritePlaceholdersPartDto

# TODO update the JSON string below
json = "{}"
# create an instance of WritePlaceholdersPartDto from a JSON string
write_placeholders_part_dto_instance = WritePlaceholdersPartDto.from_json(json)
# print the JSON string representation of the object
print(WritePlaceholdersPartDto.to_json())

# convert the object into a dict
write_placeholders_part_dto_dict = write_placeholders_part_dto_instance.to_dict()
# create an instance of WritePlaceholdersPartDto from a dict
write_placeholders_part_dto_from_dict = WritePlaceholdersPartDto.from_dict(write_placeholders_part_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


