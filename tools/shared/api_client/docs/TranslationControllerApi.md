# openapi_client.TranslationControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**translate_text**](TranslationControllerApi.md#translate_text) | **POST** /translation/translate | 


# **translate_text**
> TranslationResponseDto translate_text(translation_request_dto)



### Example


```python
import openapi_client
from openapi_client.models.translation_request_dto import TranslationRequestDto
from openapi_client.models.translation_response_dto import TranslationResponseDto
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost:8080"
)


# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.TranslationControllerApi(api_client)
    translation_request_dto = openapi_client.TranslationRequestDto() # TranslationRequestDto | 

    try:
        api_response = api_instance.translate_text(translation_request_dto)
        print("The response of TranslationControllerApi->translate_text:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TranslationControllerApi->translate_text: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **translation_request_dto** | [**TranslationRequestDto**](TranslationRequestDto.md)|  | 

### Return type

[**TranslationResponseDto**](TranslationResponseDto.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

