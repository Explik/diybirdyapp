# openapi_client.FlashcardControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create**](FlashcardControllerApi.md#create) | **POST** /flashcard | 
[**create_rich**](FlashcardControllerApi.md#create_rich) | **POST** /flashcard/rich | 
[**delete**](FlashcardControllerApi.md#delete) | **DELETE** /flashcard/{id} | 
[**get1**](FlashcardControllerApi.md#get1) | **GET** /flashcard/{id} | 
[**get_all1**](FlashcardControllerApi.md#get_all1) | **GET** /flashcard | 
[**update**](FlashcardControllerApi.md#update) | **PUT** /flashcard | 
[**update1**](FlashcardControllerApi.md#update1) | **PUT** /flashcard/rich | 


# **create**
> FlashcardDto create(flashcard_dto)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_dto import FlashcardDto
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
    api_instance = openapi_client.FlashcardControllerApi(api_client)
    flashcard_dto = openapi_client.FlashcardDto() # FlashcardDto | 

    try:
        api_response = api_instance.create(flashcard_dto)
        print("The response of FlashcardControllerApi->create:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardControllerApi->create: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flashcard_dto** | [**FlashcardDto**](FlashcardDto.md)|  | 

### Return type

[**FlashcardDto**](FlashcardDto.md)

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

# **create_rich**
> FlashcardDto create_rich(update1_request=update1_request)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_dto import FlashcardDto
from openapi_client.models.update1_request import Update1Request
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
    api_instance = openapi_client.FlashcardControllerApi(api_client)
    update1_request = openapi_client.Update1Request() # Update1Request |  (optional)

    try:
        api_response = api_instance.create_rich(update1_request=update1_request)
        print("The response of FlashcardControllerApi->create_rich:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardControllerApi->create_rich: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **update1_request** | [**Update1Request**](Update1Request.md)|  | [optional] 

### Return type

[**FlashcardDto**](FlashcardDto.md)

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

# **delete**
> delete(id)



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
    api_instance = openapi_client.FlashcardControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_instance.delete(id)
    except Exception as e:
        print("Exception when calling FlashcardControllerApi->delete: %s\n" % e)
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

# **get1**
> FlashcardDto get1(id)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_dto import FlashcardDto
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
    api_instance = openapi_client.FlashcardControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get1(id)
        print("The response of FlashcardControllerApi->get1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardControllerApi->get1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**FlashcardDto**](FlashcardDto.md)

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

# **get_all1**
> List[FlashcardDto] get_all1(deck_id=deck_id)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_dto import FlashcardDto
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
    api_instance = openapi_client.FlashcardControllerApi(api_client)
    deck_id = 'deck_id_example' # str |  (optional)

    try:
        api_response = api_instance.get_all1(deck_id=deck_id)
        print("The response of FlashcardControllerApi->get_all1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardControllerApi->get_all1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **deck_id** | **str**|  | [optional] 

### Return type

[**List[FlashcardDto]**](FlashcardDto.md)

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

# **update**
> FlashcardDto update(flashcard_dto)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_dto import FlashcardDto
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
    api_instance = openapi_client.FlashcardControllerApi(api_client)
    flashcard_dto = openapi_client.FlashcardDto() # FlashcardDto | 

    try:
        api_response = api_instance.update(flashcard_dto)
        print("The response of FlashcardControllerApi->update:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardControllerApi->update: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flashcard_dto** | [**FlashcardDto**](FlashcardDto.md)|  | 

### Return type

[**FlashcardDto**](FlashcardDto.md)

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

# **update1**
> FlashcardDto update1(update1_request=update1_request)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_dto import FlashcardDto
from openapi_client.models.update1_request import Update1Request
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
    api_instance = openapi_client.FlashcardControllerApi(api_client)
    update1_request = openapi_client.Update1Request() # Update1Request |  (optional)

    try:
        api_response = api_instance.update1(update1_request=update1_request)
        print("The response of FlashcardControllerApi->update1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlashcardControllerApi->update1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **update1_request** | [**Update1Request**](Update1Request.md)|  | [optional] 

### Return type

[**FlashcardDto**](FlashcardDto.md)

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

