# VocabularyDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**words** | [**List[VocabularyContentTextDto]**](VocabularyContentTextDto.md) |  | [optional] 

## Example

```python
from openapi_client.models.vocabulary_dto import VocabularyDto

# TODO update the JSON string below
json = "{}"
# create an instance of VocabularyDto from a JSON string
vocabulary_dto_instance = VocabularyDto.from_json(json)
# print the JSON string representation of the object
print(VocabularyDto.to_json())

# convert the object into a dict
vocabulary_dto_dict = vocabulary_dto_instance.to_dict()
# create an instance of VocabularyDto from a dict
vocabulary_dto_from_dict = VocabularyDto.from_dict(vocabulary_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


