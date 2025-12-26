# openapi_client.ExerciseSessionControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create2**](ExerciseSessionControllerApi.md#create2) | **POST** /exercise-session | 
[**get5**](ExerciseSessionControllerApi.md#get5) | **GET** /exercise-session/{id} | 
[**get_config**](ExerciseSessionControllerApi.md#get_config) | **GET** /exercise-session/{id}/options | 
[**next_exercise**](ExerciseSessionControllerApi.md#next_exercise) | **POST** /exercise-session/{id}/next-exercise | 
[**skip_exercise**](ExerciseSessionControllerApi.md#skip_exercise) | **POST** /exercise-session/{id}/skip-exercise | 
[**update_config**](ExerciseSessionControllerApi.md#update_config) | **POST** /exercise-session/{id}/apply-options | 


# **create2**
> ExerciseSessionDto create2(exercise_session_dto)



### Example


```python
import openapi_client
from openapi_client.models.exercise_session_dto import ExerciseSessionDto
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
    api_instance = openapi_client.ExerciseSessionControllerApi(api_client)
    exercise_session_dto = openapi_client.ExerciseSessionDto() # ExerciseSessionDto | 

    try:
        api_response = api_instance.create2(exercise_session_dto)
        print("The response of ExerciseSessionControllerApi->create2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseSessionControllerApi->create2: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **exercise_session_dto** | [**ExerciseSessionDto**](ExerciseSessionDto.md)|  | 

### Return type

[**ExerciseSessionDto**](ExerciseSessionDto.md)

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

# **get5**
> ExerciseSessionDto get5(id)



### Example


```python
import openapi_client
from openapi_client.models.exercise_session_dto import ExerciseSessionDto
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
    api_instance = openapi_client.ExerciseSessionControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get5(id)
        print("The response of ExerciseSessionControllerApi->get5:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseSessionControllerApi->get5: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**ExerciseSessionDto**](ExerciseSessionDto.md)

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

# **get_config**
> UpdateConfigRequest get_config(id)



### Example


```python
import openapi_client
from openapi_client.models.update_config_request import UpdateConfigRequest
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
    api_instance = openapi_client.ExerciseSessionControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_config(id)
        print("The response of ExerciseSessionControllerApi->get_config:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseSessionControllerApi->get_config: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**UpdateConfigRequest**](UpdateConfigRequest.md)

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

# **next_exercise**
> ExerciseSessionDto next_exercise(id)



### Example


```python
import openapi_client
from openapi_client.models.exercise_session_dto import ExerciseSessionDto
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
    api_instance = openapi_client.ExerciseSessionControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.next_exercise(id)
        print("The response of ExerciseSessionControllerApi->next_exercise:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseSessionControllerApi->next_exercise: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**ExerciseSessionDto**](ExerciseSessionDto.md)

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

# **skip_exercise**
> ExerciseSessionDto skip_exercise(id)



### Example


```python
import openapi_client
from openapi_client.models.exercise_session_dto import ExerciseSessionDto
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
    api_instance = openapi_client.ExerciseSessionControllerApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.skip_exercise(id)
        print("The response of ExerciseSessionControllerApi->skip_exercise:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseSessionControllerApi->skip_exercise: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**ExerciseSessionDto**](ExerciseSessionDto.md)

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

# **update_config**
> ExerciseSessionDto update_config(id, update_config_request)



### Example


```python
import openapi_client
from openapi_client.models.exercise_session_dto import ExerciseSessionDto
from openapi_client.models.update_config_request import UpdateConfigRequest
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
    api_instance = openapi_client.ExerciseSessionControllerApi(api_client)
    id = 'id_example' # str | 
    update_config_request = openapi_client.UpdateConfigRequest() # UpdateConfigRequest | 

    try:
        api_response = api_instance.update_config(id, update_config_request)
        print("The response of ExerciseSessionControllerApi->update_config:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseSessionControllerApi->update_config: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **update_config_request** | [**UpdateConfigRequest**](UpdateConfigRequest.md)|  | 

### Return type

[**ExerciseSessionDto**](ExerciseSessionDto.md)

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

