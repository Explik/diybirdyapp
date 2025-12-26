# openapi_client.AuthenticationControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**login**](AuthenticationControllerApi.md#login) | **POST** /auth/login | 
[**logout**](AuthenticationControllerApi.md#logout) | **POST** /auth/logout | 
[**register_user**](AuthenticationControllerApi.md#register_user) | **POST** /auth/signup | 


# **login**
> str login(login_dto)



### Example


```python
import openapi_client
from openapi_client.models.login_dto import LoginDto
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
    api_instance = openapi_client.AuthenticationControllerApi(api_client)
    login_dto = openapi_client.LoginDto() # LoginDto | 

    try:
        api_response = api_instance.login(login_dto)
        print("The response of AuthenticationControllerApi->login:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthenticationControllerApi->login: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **login_dto** | [**LoginDto**](LoginDto.md)|  | 

### Return type

**str**

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

# **logout**
> object logout()



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
    api_instance = openapi_client.AuthenticationControllerApi(api_client)

    try:
        api_response = api_instance.logout()
        print("The response of AuthenticationControllerApi->logout:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthenticationControllerApi->logout: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

**object**

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

# **register_user**
> object register_user(signup_dto)



### Example


```python
import openapi_client
from openapi_client.models.signup_dto import SignupDto
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
    api_instance = openapi_client.AuthenticationControllerApi(api_client)
    signup_dto = openapi_client.SignupDto() # SignupDto | 

    try:
        api_response = api_instance.register_user(signup_dto)
        print("The response of AuthenticationControllerApi->register_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthenticationControllerApi->register_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **signup_dto** | [**SignupDto**](SignupDto.md)|  | 

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

