# LoginDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**email** | **str** |  | 
**password** | **str** |  | 

## Example

```python
from openapi_client.models.login_dto import LoginDto

# TODO update the JSON string below
json = "{}"
# create an instance of LoginDto from a JSON string
login_dto_instance = LoginDto.from_json(json)
# print the JSON string representation of the object
print(LoginDto.to_json())

# convert the object into a dict
login_dto_dict = login_dto_instance.to_dict()
# create an instance of LoginDto from a dict
login_dto_from_dict = LoginDto.from_dict(login_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


