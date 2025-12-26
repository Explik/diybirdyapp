# openapi_client.LanguageControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**attach_config**](LanguageControllerApi.md#attach_config) | **POST** /language/{id}/attach-config | 
[**create_config**](LanguageControllerApi.md#create_config) | **POST** /language/{id}/create-config | 
[**create_language**](LanguageControllerApi.md#create_language) | **POST** /language | 
[**detach_config**](LanguageControllerApi.md#detach_config) | **POST** /language/{id}/detach-config | 
[**get_all**](LanguageControllerApi.md#get_all) | **GET** /language | 
[**get_configs**](LanguageControllerApi.md#get_configs) | **GET** /language/{id}/config | 
[**update_language**](LanguageControllerApi.md#update_language) | **PUT** /language/{id} | 


# **attach_config**
> object attach_config(id, configuration_identifier_dto)



### Example


```python
import openapi_client
from openapi_client.models.configuration_identifier_dto import ConfigurationIdentifierDto
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
    api_instance = openapi_client.LanguageControllerApi(api_client)
    id = 'id_example' # str | 
    configuration_identifier_dto = openapi_client.ConfigurationIdentifierDto() # ConfigurationIdentifierDto | 

    try:
        api_response = api_instance.attach_config(id, configuration_identifier_dto)
        print("The response of LanguageControllerApi->attach_config:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LanguageControllerApi->attach_config: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **configuration_identifier_dto** | [**ConfigurationIdentifierDto**](ConfigurationIdentifierDto.md)|  | 

### Return type

**object**

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

# **create_config**
> object create_config(id, get_config_by_id200_response)



### Example


```python
import openapi_client
from openapi_client.models.get_config_by_id200_response import GetConfigById200Response
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
    api_instance = openapi_client.LanguageControllerApi(api_client)
    id = 'id_example' # str | 
    get_config_by_id200_response = openapi_client.GetConfigById200Response() # GetConfigById200Response | 

    try:
        api_response = api_instance.create_config(id, get_config_by_id200_response)
        print("The response of LanguageControllerApi->create_config:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LanguageControllerApi->create_config: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **get_config_by_id200_response** | [**GetConfigById200Response**](GetConfigById200Response.md)|  | 

### Return type

**object**

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

# **create_language**
> object create_language(flashcard_language_dto)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_language_dto import FlashcardLanguageDto
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
    api_instance = openapi_client.LanguageControllerApi(api_client)
    flashcard_language_dto = openapi_client.FlashcardLanguageDto() # FlashcardLanguageDto | 

    try:
        api_response = api_instance.create_language(flashcard_language_dto)
        print("The response of LanguageControllerApi->create_language:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LanguageControllerApi->create_language: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flashcard_language_dto** | [**FlashcardLanguageDto**](FlashcardLanguageDto.md)|  | 

### Return type

**object**

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

# **detach_config**
> object detach_config(id, configuration_identifier_dto)



### Example


```python
import openapi_client
from openapi_client.models.configuration_identifier_dto import ConfigurationIdentifierDto
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
    api_instance = openapi_client.LanguageControllerApi(api_client)
    id = 'id_example' # str | 
    configuration_identifier_dto = openapi_client.ConfigurationIdentifierDto() # ConfigurationIdentifierDto | 

    try:
        api_response = api_instance.detach_config(id, configuration_identifier_dto)
        print("The response of LanguageControllerApi->detach_config:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LanguageControllerApi->detach_config: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **configuration_identifier_dto** | [**ConfigurationIdentifierDto**](ConfigurationIdentifierDto.md)|  | 

### Return type

**object**

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

# **get_all**
> List[FlashcardLanguageDto] get_all()



### Example


```python
import openapi_client
from openapi_client.models.flashcard_language_dto import FlashcardLanguageDto
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
    api_instance = openapi_client.LanguageControllerApi(api_client)

    try:
        api_response = api_instance.get_all()
        print("The response of LanguageControllerApi->get_all:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LanguageControllerApi->get_all: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[FlashcardLanguageDto]**](FlashcardLanguageDto.md)

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

# **get_configs**
> List[GetConfigById200Response] get_configs(id, type=type)



### Example


```python
import openapi_client
from openapi_client.models.get_config_by_id200_response import GetConfigById200Response
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
    api_instance = openapi_client.LanguageControllerApi(api_client)
    id = 'id_example' # str | 
    type = 'type_example' # str |  (optional)

    try:
        api_response = api_instance.get_configs(id, type=type)
        print("The response of LanguageControllerApi->get_configs:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LanguageControllerApi->get_configs: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **type** | **str**|  | [optional] 

### Return type

[**List[GetConfigById200Response]**](GetConfigById200Response.md)

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

# **update_language**
> object update_language(id, flashcard_language_dto)



### Example


```python
import openapi_client
from openapi_client.models.flashcard_language_dto import FlashcardLanguageDto
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
    api_instance = openapi_client.LanguageControllerApi(api_client)
    id = 'id_example' # str | 
    flashcard_language_dto = openapi_client.FlashcardLanguageDto() # FlashcardLanguageDto | 

    try:
        api_response = api_instance.update_language(id, flashcard_language_dto)
        print("The response of LanguageControllerApi->update_language:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling LanguageControllerApi->update_language: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **flashcard_language_dto** | [**FlashcardLanguageDto**](FlashcardLanguageDto.md)|  | 

### Return type

**object**

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

