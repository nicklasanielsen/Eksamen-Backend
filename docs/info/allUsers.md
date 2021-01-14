# Get all users

Used to get all registered users.



**URL** : `/api/info/allUsers/`

**Method** : `GET`

**Authentication required** : Yes

**Permissions required** : Admin



## Success Response

___

**Code** : 200

```json
[
    {
        "created": "yyyy-MM-dd HH:mm:ss Time zone",
        "userName": "Users username",
        "fullName": "Users fullname",
        "roleList": [
            {
                "roleName": "User"
            },
            {
                "roleName": "Admin"
            }
        ]
    },
    {
        "created": "yyyy-MM-dd HH:mm:ss Time zone",
        "userName": "Users username",
        "fullName": "Users fullname",
        "roleList": [
            {
                "roleName": "User"
            }
        ]
    },
    ...
]
```



## Bad Requests

___

**Scenario** : Missing authentication.

**Code** : 403

```json
{
    "code": 403,
    "message": "Not authenticated - do login"
}
```


___

**Scenario** : Missing permissions.

**Code** : 401

```json
{
    "code": 401,
    "message": "You are not authorized to perform the requested operation"
}
```
