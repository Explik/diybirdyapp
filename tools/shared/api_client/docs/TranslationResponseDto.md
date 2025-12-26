# TranslationResponseDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**translated_text** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.translation_response_dto import TranslationResponseDto

# TODO update the JSON string below
json = "{}"
# create an instance of TranslationResponseDto from a JSON string
translation_response_dto_instance = TranslationResponseDto.from_json(json)
# print the JSON string representation of the object
print(TranslationResponseDto.to_json())

# convert the object into a dict
translation_response_dto_dict = translation_response_dto_instance.to_dict()
# create an instance of TranslationResponseDto from a dict
translation_response_dto_from_dict = TranslationResponseDto.from_dict(translation_response_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


