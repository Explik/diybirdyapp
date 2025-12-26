# FlashcardContentTextDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**text** | **str** |  | 
**language_id** | **str** |  | 

## Example

```python
from openapi_client.models.flashcard_content_text_dto import FlashcardContentTextDto

# TODO update the JSON string below
json = "{}"
# create an instance of FlashcardContentTextDto from a JSON string
flashcard_content_text_dto_instance = FlashcardContentTextDto.from_json(json)
# print the JSON string representation of the object
print(FlashcardContentTextDto.to_json())

# convert the object into a dict
flashcard_content_text_dto_dict = flashcard_content_text_dto_instance.to_dict()
# create an instance of FlashcardContentTextDto from a dict
flashcard_content_text_dto_from_dict = FlashcardContentTextDto.from_dict(flashcard_content_text_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


