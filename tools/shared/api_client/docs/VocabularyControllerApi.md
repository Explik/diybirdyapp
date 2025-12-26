# openapi_client.VocabularyControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get**](VocabularyControllerApi.md#get) | **GET** /vocabulary | 


# **get**
> VocabularyDto get()



### Example


```python
import openapi_client
from openapi_client.models.vocabulary_dto import VocabularyDto
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
    api_instance = openapi_client.VocabularyControllerApi(api_client)

    try:
        api_response = api_instance.get()
        print("The response of VocabularyControllerApi->get:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling VocabularyControllerApi->get: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**VocabularyDto**](VocabularyDto.md)

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

