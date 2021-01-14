# Get random jokes

Used to fetch random jokes from external joke API's.



**URL** : `/api/fun/jokes/`

**Method** : `GET`

**Authentication required** : Yes

**Permissions required** : User



## Success Response

___

**Code** : 200

```json
[
    {
        "joke": "Super Funny Joke",
        "ref": "URL Reference"
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

___

**Scenario** : External service timed out.

**Code** : 503

```json
{
    "code": 503,
    "message": "One or more of our partners did not respond when we tried to load one or more jokes, please try again later."
}
```

___

**Scenario** : Internal server error caused by threads.

**Code** : 500

```json
{
    "code": 500,
    "message": "A system error occurred when we first loaded one or more jokes from our partners, please contact us regarding the error, or try again later."
}
```

___

**Scenario** : Internal server error caused by invalid JSON received by external service.

**Code** : 500

```json
{
    "code": 500,
    "message": "A system error occurred while converting one or more jokes from our partners, please contact us regarding the error."
}
```

