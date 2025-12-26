# FlashcardDtoFrontContent


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** |  | 
**type** | **str** |  | 
**audio_url** | **str** |  | 
**image_url** | **str** |  | 
**text** | **str** |  | 
**language_id** | **str** |  | 
**audio_file_name** | **str** |  | [optional] 
**image_file_name** | **str** |  | [optional] 
**video_file_name** | **str** |  | [optional] 
**video_url** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.flashcard_dto_front_content import FlashcardDtoFrontContent

# TODO update the JSON string below
json = "{}"
# create an instance of FlashcardDtoFrontContent from a JSON string
flashcard_dto_front_content_instance = FlashcardDtoFrontContent.from_json(json)
# print the JSON string representation of the object
print(FlashcardDtoFrontContent.to_json())

# convert the object into a dict
flashcard_dto_front_content_dict = flashcard_dto_front_content_instance.to_dict()
# create an instance of FlashcardDtoFrontContent from a dict
flashcard_dto_front_content_from_dict = FlashcardDtoFrontContent.from_dict(flashcard_dto_front_content_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


