# FlashcardDeckDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | [optional] 
**name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.flashcard_deck_dto import FlashcardDeckDto

# TODO update the JSON string below
json = "{}"
# create an instance of FlashcardDeckDto from a JSON string
flashcard_deck_dto_instance = FlashcardDeckDto.from_json(json)
# print the JSON string representation of the object
print(FlashcardDeckDto.to_json())

# convert the object into a dict
flashcard_deck_dto_dict = flashcard_deck_dto_instance.to_dict()
# create an instance of FlashcardDeckDto from a dict
flashcard_deck_dto_from_dict = FlashcardDeckDto.from_dict(flashcard_deck_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


