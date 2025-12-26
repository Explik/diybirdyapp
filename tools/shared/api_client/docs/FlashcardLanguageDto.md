# FlashcardLanguageDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | 
**name** | **str** |  | 
**iso_code** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.flashcard_language_dto import FlashcardLanguageDto

# TODO update the JSON string below
json = "{}"
# create an instance of FlashcardLanguageDto from a JSON string
flashcard_language_dto_instance = FlashcardLanguageDto.from_json(json)
# print the JSON string representation of the object
print(FlashcardLanguageDto.to_json())

# convert the object into a dict
flashcard_language_dto_dict = flashcard_language_dto_instance.to_dict()
# create an instance of FlashcardLanguageDto from a dict
flashcard_language_dto_from_dict = FlashcardLanguageDto.from_dict(flashcard_language_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


