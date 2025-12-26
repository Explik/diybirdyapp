# openapi_client.ExerciseControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get3**](ExerciseControllerApi.md#get3) | **GET** /exercise | 
[**get4**](ExerciseControllerApi.md#get4) | **GET** /exercise/{id} | 
[**get_types**](ExerciseControllerApi.md#get_types) | **GET** /exercise/types | 
[**submit_answer**](ExerciseControllerApi.md#submit_answer) | **POST** /exercise/{id}/answer | 
[**submit_answer_rich**](ExerciseControllerApi.md#submit_answer_rich) | **POST** /exercise/{id}/answer/rich | 


# **get3**
> List[ExerciseDto] get3()



### Example


```python
import openapi_client
from openapi_client.models.exercise_dto import ExerciseDto
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
    api_instance = openapi_client.ExerciseControllerApi(api_client)

    try:
        api_response = api_instance.get3()
        print("The response of ExerciseControllerApi->get3:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseControllerApi->get3: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[ExerciseDto]**](ExerciseDto.md)

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

# **get4**
> ExerciseDto get4(id, session_id=session_id)



### Example


```python
import openapi_client
from openapi_client.models.exercise_dto import ExerciseDto
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
    api_instance = openapi_client.ExerciseControllerApi(api_client)
    id = 'id_example' # str | 
    session_id = 'session_id_example' # str |  (optional)

    try:
        api_response = api_instance.get4(id, session_id=session_id)
        print("The response of ExerciseControllerApi->get4:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseControllerApi->get4: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **session_id** | **str**|  | [optional] 

### Return type

[**ExerciseDto**](ExerciseDto.md)

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

# **get_types**
> List[str] get_types()



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
    api_instance = openapi_client.ExerciseControllerApi(api_client)

    try:
        api_response = api_instance.get_types()
        print("The response of ExerciseControllerApi->get_types:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseControllerApi->get_types: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

**List[str]**

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

# **submit_answer**
> ExerciseDto submit_answer(id, submit_answer_request)



### Example


```python
import openapi_client
from openapi_client.models.exercise_dto import ExerciseDto
from openapi_client.models.submit_answer_request import SubmitAnswerRequest
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
    api_instance = openapi_client.ExerciseControllerApi(api_client)
    id = 'id_example' # str | 
    submit_answer_request = openapi_client.SubmitAnswerRequest() # SubmitAnswerRequest | 

    try:
        api_response = api_instance.submit_answer(id, submit_answer_request)
        print("The response of ExerciseControllerApi->submit_answer:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseControllerApi->submit_answer: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **submit_answer_request** | [**SubmitAnswerRequest**](SubmitAnswerRequest.md)|  | 

### Return type

[**ExerciseDto**](ExerciseDto.md)

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

# **submit_answer_rich**
> ExerciseDto submit_answer_rich(id, submit_answer_rich_request=submit_answer_rich_request)



### Example


```python
import openapi_client
from openapi_client.models.exercise_dto import ExerciseDto
from openapi_client.models.submit_answer_rich_request import SubmitAnswerRichRequest
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
    api_instance = openapi_client.ExerciseControllerApi(api_client)
    id = 'id_example' # str | 
    submit_answer_rich_request = openapi_client.SubmitAnswerRichRequest() # SubmitAnswerRichRequest |  (optional)

    try:
        api_response = api_instance.submit_answer_rich(id, submit_answer_rich_request=submit_answer_rich_request)
        print("The response of ExerciseControllerApi->submit_answer_rich:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExerciseControllerApi->submit_answer_rich: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 
 **submit_answer_rich_request** | [**SubmitAnswerRichRequest**](SubmitAnswerRichRequest.md)|  | [optional] 

### Return type

[**ExerciseDto**](ExerciseDto.md)

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

