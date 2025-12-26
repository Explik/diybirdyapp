# FlashcardContentUploadVideoDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**video_file_name** | **str** |  | [optional] 
**language_id** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.flashcard_content_upload_video_dto import FlashcardContentUploadVideoDto

# TODO update the JSON string below
json = "{}"
# create an instance of FlashcardContentUploadVideoDto from a JSON string
flashcard_content_upload_video_dto_instance = FlashcardContentUploadVideoDto.from_json(json)
# print the JSON string representation of the object
print(FlashcardContentUploadVideoDto.to_json())

# convert the object into a dict
flashcard_content_upload_video_dto_dict = flashcard_content_upload_video_dto_instance.to_dict()
# create an instance of FlashcardContentUploadVideoDto from a dict
flashcard_content_upload_video_dto_from_dict = FlashcardContentUploadVideoDto.from_dict(flashcard_content_upload_video_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


