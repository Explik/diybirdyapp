# openapi_client.TextContentControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**add_transcription**](TextContentControllerApi.md#add_transcription) | **POST** /text-content/{id}/add-transcription | 
[**get_pronunciation**](TextContentControllerApi.md#get_pronunciation) | **GET** /text-content/{id}/pronunciation | 
[**upload_pronunciation**](TextContentControllerApi.md#upload_pronunciation) | **POST** /text-content/{id}/upload-pronunciation | 


# **add_transcription**
> add_transcription(id, text_content_transcription_dto)



### Example


```python
import openapi_client
from openapi_client.models.text_content_transcription_dto import TextContentTranscriptionDto
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
    api_instance = openapi_client.TextContentControllerApi(api_client)
    id = 'id_example' # str | 
    text_content_transcription_dto = openapi_client.TextContentTranscriptionDto() # TextContentTranscriptionDto | 

    try:
        api_instance.add_transcription(id, text_content_transcription_dto)
    except Exception as e:
        print("Exception when calling TextContentControllerApi->add_transcription: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **text_content_transcription_dto** | [**TextContentTranscriptionDto**](TextContentTranscriptionDto.md)|  | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_pronunciation**
> List[bytearray] get_pronunciation(id)



### Example


```python
import openapi_client
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
    api_instance = openapi_client.TextContentControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_pronunciation(id)
        print("The response of TextContentControllerApi->get_pronunciation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TextContentControllerApi->get_pronunciation: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

**List[bytearray]**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **upload_pronunciation**
> upload_pronunciation(id, upload_file_request=upload_file_request)



### Example


```python
import openapi_client
from openapi_client.models.upload_file_request import UploadFileRequest
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
    api_instance = openapi_client.TextContentControllerApi(api_client)
    id = 'id_example' # str | 
    upload_file_request = openapi_client.UploadFileRequest() # UploadFileRequest |  (optional)

    try:
        api_instance.upload_pronunciation(id, upload_file_request=upload_file_request)
    except Exception as e:
        print("Exception when calling TextContentControllerApi->upload_pronunciation: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **upload_file_request** | [**UploadFileRequest**](UploadFileRequest.md)|  | [optional] 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

