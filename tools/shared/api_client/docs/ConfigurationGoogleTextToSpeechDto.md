# ConfigurationGoogleTextToSpeechDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**language_code** | **str** |  | [optional] 
**voice_name** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.configuration_google_text_to_speech_dto import ConfigurationGoogleTextToSpeechDto

# TODO update the JSON string below
json = "{}"
# create an instance of ConfigurationGoogleTextToSpeechDto from a JSON string
configuration_google_text_to_speech_dto_instance = ConfigurationGoogleTextToSpeechDto.from_json(json)
# print the JSON string representation of the object
print(ConfigurationGoogleTextToSpeechDto.to_json())

# convert the object into a dict
configuration_google_text_to_speech_dto_dict = configuration_google_text_to_speech_dto_instance.to_dict()
# create an instance of ConfigurationGoogleTextToSpeechDto from a dict
configuration_google_text_to_speech_dto_from_dict = ConfigurationGoogleTextToSpeechDto.from_dict(configuration_google_text_to_speech_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


