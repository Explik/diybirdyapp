# TextContentTranscriptionDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**transcription** | **str** |  | 
**transcription_system** | **str** |  | 

## Example

```python
from openapi_client.models.text_content_transcription_dto import TextContentTranscriptionDto

# TODO update the JSON string below
json = "{}"
# create an instance of TextContentTranscriptionDto from a JSON string
text_content_transcription_dto_instance = TextContentTranscriptionDto.from_json(json)
# print the JSON string representation of the object
print(TextContentTranscriptionDto.to_json())

# convert the object into a dict
text_content_transcription_dto_dict = text_content_transcription_dto_instance.to_dict()
# create an instance of TextContentTranscriptionDto from a dict
text_content_transcription_dto_from_dict = TextContentTranscriptionDto.from_dict(text_content_transcription_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


