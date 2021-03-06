# Login

Used by existing users to login.



**URL** : `/api/auth/login/`

**Method** : `POST`

**Authentication required** : No

**Permissions required** : None



## Request Body

___

```json
{
    "userName": "myUsername",
    "password": "mySecretPassword"
}
```



## Success Response

___

**Code** : 200

```json
{
    "userName": "Username used to login",
    "token": "Authentication token generated by the system"
}
```



## Bad Requests

___

**Scenario** : Missing request body.

**Code** : 400

```json
{
    "code": 400,
    "message": "Malformed JSON Suplied"
}
```

___

**Scenario** : Invalid request body.

**Code** : 400

```json
{
    "code": 400,
    "message": "Malformed JSON Suplied"
}
```

___

**Scenario** : Incorrect username.

**Code** : 403

```json
{
    "code": 403,
    "message": "Invalid username and/or password."
}
```

___

**Scenario** : Incorrect password.

**Code** : 403

```json
{
    "code": 403,
    "message": "Invalid username and/or password."
}
```

