# Get info about user by username

Used to get information about a specific user.



**URL** : `/api/info/user/{username}/`

**Method** : `GET`

**Authentication required** : Yes

**Permissions required** : User



## Success Response

___

**Code** : 200

```json
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
            },
            ...
        ]
    }
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

___

**Scenario** : User not found.

**Code** : 400

```json
{
    "code": 400,
    "message": "Couldn't find user with username: {username}"
}
```

