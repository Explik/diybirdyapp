# FlashcardContentUploadAudioDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**audio_file_name** | **str** |  | [optional] 
**language_id** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.flashcard_content_upload_audio_dto import FlashcardContentUploadAudioDto

# TODO update the JSON string below
json = "{}"
# create an instance of FlashcardContentUploadAudioDto from a JSON string
flashcard_content_upload_audio_dto_instance = FlashcardContentUploadAudioDto.from_json(json)
# print the JSON string representation of the object
print(FlashcardContentUploadAudioDto.to_json())

# convert the object into a dict
flashcard_content_upload_audio_dto_dict = flashcard_content_upload_audio_dto_instance.to_dict()
# create an instance of FlashcardContentUploadAudioDto from a dict
flashcard_content_upload_audio_dto_from_dict = FlashcardContentUploadAudioDto.from_dict(flashcard_content_upload_audio_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


