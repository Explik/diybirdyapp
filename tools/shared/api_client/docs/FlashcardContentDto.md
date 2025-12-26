# FlashcardContentDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | 
**type** | **str** |  | 

## Example

```python
from openapi_client.models.flashcard_content_dto import FlashcardContentDto

# TODO update the JSON string below
json = "{}"
# create an instance of FlashcardContentDto from a JSON string
flashcard_content_dto_instance = FlashcardContentDto.from_json(json)
# print the JSON string representation of the object
print(FlashcardContentDto.to_json())

# convert the object into a dict
flashcard_content_dto_dict = flashcard_content_dto_instance.to_dict()
# create an instance of FlashcardContentDto from a dict
flashcard_content_dto_from_dict = FlashcardContentDto.from_dict(flashcard_content_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


