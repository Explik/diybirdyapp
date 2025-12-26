# SignupDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** |  | [optional] 
**email** | **str** |  | 
**password** | **str** |  | 

## Example

```python
from openapi_client.models.signup_dto import SignupDto

# TODO update the JSON string below
json = "{}"
# create an instance of SignupDto from a JSON string
signup_dto_instance = SignupDto.from_json(json)
# print the JSON string representation of the object
print(SignupDto.to_json())

# convert the object into a dict
signup_dto_dict = signup_dto_instance.to_dict()
# create an instance of SignupDto from a dict
signup_dto_from_dict = SignupDto.from_dict(signup_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


