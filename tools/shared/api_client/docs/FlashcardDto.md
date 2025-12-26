# FlashcardDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | 
**deck_id** | **str** |  | [optional] 
**deck_order** | **int** |  | [optional] 
**front_content** | [**FlashcardDtoFrontContent**](FlashcardDtoFrontContent.md) |  | [optional] 
**back_content** | [**FlashcardDtoFrontContent**](FlashcardDtoFrontContent.md) |  | [optional] 

## Example

```python
from openapi_client.models.flashcard_dto import FlashcardDto

# TODO update the JSON string below
json = "{}"
# create an instance of FlashcardDto from a JSON string
flashcard_dto_instance = FlashcardDto.from_json(json)
# print the JSON string representation of the object
print(FlashcardDto.to_json())

# convert the object into a dict
flashcard_dto_dict = flashcard_dto_instance.to_dict()
# create an instance of FlashcardDto from a dict
flashcard_dto_from_dict = FlashcardDto.from_dict(flashcard_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


