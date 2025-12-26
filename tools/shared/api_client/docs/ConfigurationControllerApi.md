# openapi_client.ConfigurationControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**delete_config_by_id**](ConfigurationControllerApi.md#delete_config_by_id) | **DELETE** /config/{id} | 
[**get_available_options**](ConfigurationControllerApi.md#get_available_options) | **POST** /config/available-options | 
[**get_config_by_id**](ConfigurationControllerApi.md#get_config_by_id) | **GET** /config/{id} | 
[**update_config_by_id**](ConfigurationControllerApi.md#update_config_by_id) | **PUT** /config/{id} | 


# **delete_config_by_id**
> delete_config_by_id(id)



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
    api_instance = openapi_client.ConfigurationControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_instance.delete_config_by_id(id)
    except Exception as e:
        print("Exception when calling ConfigurationControllerApi->delete_config_by_id: %s\n" % e)
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

# **get_available_options**
> ConfigurationOptionsDto get_available_options(configuration_options_dto)



### Example


```python
import openapi_client
from openapi_client.models.configuration_options_dto import ConfigurationOptionsDto
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
    api_instance = openapi_client.ConfigurationControllerApi(api_client)
    configuration_options_dto = openapi_client.ConfigurationOptionsDto() # ConfigurationOptionsDto | 

    try:
        api_response = api_instance.get_available_options(configuration_options_dto)
        print("The response of ConfigurationControllerApi->get_available_options:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ConfigurationControllerApi->get_available_options: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **configuration_options_dto** | [**ConfigurationOptionsDto**](ConfigurationOptionsDto.md)|  | 

### Return type

[**ConfigurationOptionsDto**](ConfigurationOptionsDto.md)

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

# **get_config_by_id**
> GetConfigById200Response get_config_by_id(id)



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
    api_instance = openapi_client.ConfigurationControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_config_by_id(id)
        print("The response of ConfigurationControllerApi->get_config_by_id:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ConfigurationControllerApi->get_config_by_id: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**GetConfigById200Response**](GetConfigById200Response.md)

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

# **update_config_by_id**
> GetConfigById200Response update_config_by_id(id, get_config_by_id200_response)



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
    api_instance = openapi_client.ConfigurationControllerApi(api_client)
    id = 'id_example' # str | 
    get_config_by_id200_response = openapi_client.GetConfigById200Response() # GetConfigById200Response | 

    try:
        api_response = api_instance.update_config_by_id(id, get_config_by_id200_response)
        print("The response of ConfigurationControllerApi->update_config_by_id:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ConfigurationControllerApi->update_config_by_id: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **get_config_by_id200_response** | [**GetConfigById200Response**](GetConfigById200Response.md)|  | 

### Return type

[**GetConfigById200Response**](GetConfigById200Response.md)

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

