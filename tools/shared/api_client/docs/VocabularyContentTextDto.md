# VocabularyContentTextDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**text** | **str** |  | [optional] 
**pronunciation_url** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.vocabulary_content_text_dto import VocabularyContentTextDto

# TODO update the JSON string below
json = "{}"
# create an instance of VocabularyContentTextDto from a JSON string
vocabulary_content_text_dto_instance = VocabularyContentTextDto.from_json(json)
# print the JSON string representation of the object
print(VocabularyContentTextDto.to_json())

# convert the object into a dict
vocabulary_content_text_dto_dict = vocabulary_content_text_dto_instance.to_dict()
# create an instance of VocabularyContentTextDto from a dict
vocabulary_content_text_dto_from_dict = VocabularyContentTextDto.from_dict(vocabulary_content_text_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


