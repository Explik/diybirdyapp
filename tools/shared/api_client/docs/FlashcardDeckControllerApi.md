# openapi_client.FlashcardDeckControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create1**](FlashcardDeckControllerApi.md#create1) | **POST** /flashcard-deck | 
[**delete1**](FlashcardDeckControllerApi.md#delete1) | **DELETE** /flashcard-deck/{id} | 
[**get2**](FlashcardDeckControllerApi.md#get2) | **GET** /flashcard-deck/{id} | 
[**get_all2**](FlashcardDeckControllerApi.md#get_all2) | **GET** /flashcard-deck | 
[**update2**](FlashcardDeckControllerApi.md#update2) | **PUT** /flashcard-deck | 


# **create1**
> FlashcardDeckDto create1(flashcard_deck_dto)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_deck_dto import FlashcardDeckDto
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
    api_instance = openapi_client.FlashcardDeckControllerApi(api_client)
    flashcard_deck_dto = openapi_client.FlashcardDeckDto() # FlashcardDeckDto | 

    try:
        api_response = api_instance.create1(flashcard_deck_dto)
        print("The response of FlashcardDeckControllerApi->create1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardDeckControllerApi->create1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flashcard_deck_dto** | [**FlashcardDeckDto**](FlashcardDeckDto.md)|  | 

### Return type

[**FlashcardDeckDto**](FlashcardDeckDto.md)

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

# **delete1**
> delete1(id)



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
    api_instance = openapi_client.FlashcardDeckControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_instance.delete1(id)
    except Exception as e:
        print("Exception when calling FlashcardDeckControllerApi->delete1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get2**
> FlashcardDeckDto get2(id)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_deck_dto import FlashcardDeckDto
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
    api_instance = openapi_client.FlashcardDeckControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get2(id)
        print("The response of FlashcardDeckControllerApi->get2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardDeckControllerApi->get2: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**FlashcardDeckDto**](FlashcardDeckDto.md)

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

# **get_all2**
> List[FlashcardDeckDto] get_all2()



### Example


```python
import openapi_client
from openapi_client.models.flashcard_deck_dto import FlashcardDeckDto
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
    api_instance = openapi_client.FlashcardDeckControllerApi(api_client)

    try:
        api_response = api_instance.get_all2()
        print("The response of FlashcardDeckControllerApi->get_all2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardDeckControllerApi->get_all2: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[FlashcardDeckDto]**](FlashcardDeckDto.md)

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

# **update2**
> FlashcardDeckDto update2(flashcard_deck_dto)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_deck_dto import FlashcardDeckDto
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
    api_instance = openapi_client.FlashcardDeckControllerApi(api_client)
    flashcard_deck_dto = openapi_client.FlashcardDeckDto() # FlashcardDeckDto | 

    try:
        api_response = api_instance.update2(flashcard_deck_dto)
        print("The response of FlashcardDeckControllerApi->update2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardDeckControllerApi->update2: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flashcard_deck_dto** | [**FlashcardDeckDto**](FlashcardDeckDto.md)|  | 

### Return type

[**FlashcardDeckDto**](FlashcardDeckDto.md)

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

