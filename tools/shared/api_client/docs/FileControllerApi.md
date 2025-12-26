# openapi_client.FileControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_file**](FileControllerApi.md#get_file) | **GET** /{filename} | 
[**upload_file**](FileControllerApi.md#upload_file) | **POST** /upload | 


# **get_file**
> bytearray get_file(filename)



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
    api_instance = openapi_client.FileControllerApi(api_client)
    filename = 'filename_example' # str | 

    try:
        api_response = api_instance.get_file(filename)
        print("The response of FileControllerApi->get_file:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FileControllerApi->get_file: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **filename** | **str**|  | 

### Return type

**bytearray**

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

# **upload_file**
> FileUploadResultDto upload_file(upload_file_request=upload_file_request)



### Example


```python
import openapi_client
from openapi_client.models.file_upload_result_dto import FileUploadResultDto
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
    api_instance = openapi_client.FileControllerApi(api_client)
    upload_file_request = openapi_client.UploadFileRequest() # UploadFileRequest |  (optional)

    try:
        api_response = api_instance.upload_file(upload_file_request=upload_file_request)
        print("The response of FileControllerApi->upload_file:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FileControllerApi->upload_file: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **upload_file_request** | [**UploadFileRequest**](UploadFileRequest.md)|  | [optional] 

### Return type

[**FileUploadResultDto**](FileUploadResultDto.md)

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

