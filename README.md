# StudentManagementSystem

- ***To create new student***

Request :
```
POST /student HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 60
{
    "firstName": "Mesut",
    "lastName": "Cetinkaya"
}
```
Response :
```
{
    "id": 1,
    "firstName": "Mesut",
    "lastName": "Cetinkaya",
    "updateDate": "2022-02-07T00:10:47.279+00:00"
}
```
NOT : Header -> Message : Student is added succesfully

-  ***To get all students (If any student does not exist )***

Request:
```
GET /students HTTP/1.1
Host: localhost:8080
```

Response:
```
{
    "timestamp": "2022-02-07T00:07:51.708+00:00",
    "message": "Any Students not found",
    "details": "uri=/students"
}
```
- ***To get all students***

Request :
```
GET /students HTTP/1.1
Host: localhost:8080
```

Response :
```
[
    {
        "id": 1,
        "firstName": "Mesut",
        "lastName": "Cetinkaya",
        "updateDate": "2022-02-07T00:10:47.279+00:00"
    },
    {
        "id": 2,
        "firstName": "Omer",
        "lastName": "Cetinkaya",
        "updateDate": "2022-02-07T00:13:08.880+00:00"
    },
    {
        "id": 3,
        "firstName": "Nazım",
        "lastName": "Cetinkaya",
        "updateDate": "2022-02-07T00:13:13.905+00:00"
    }
]
```

-  ***To get student by ID***
Request :
```
GET /student/3 HTTP/1.1
Host: localhost:8080
```

Response :
```
{
    "id": 3,
    "firstName": "Nazım",
    "lastName": "Cetinkaya",
    "updateDate": "2022-02-07T00:13:13.905+00:00"
}
```

- ***Get Student by ID (If Student that given id does not exist)***

Request :
```
GET /student/5 HTTP/1.1
Host: localhost:8080
```

Response :
```
{
    "timestamp": "2022-02-07T00:16:59.890+00:00",
    "message": "Student not found by 5",
    "details": "uri=/student/5"
}
```

- ***To update student with ID***

Request :
```
PUT /student/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 60
{
    "firstName": "Süleyman",
    "lastName": "Yüksel"
}
```
Response :
```
{
    "id": 1,
    "firstName": "Süleyman",
    "lastName": "Yüksel",
    "updateDate": "2022-02-07T00:19:24.817+00:00"
}
```
NOT : Header -> Message : Student is updated succesfully with id : 1

- ***To create new Lesson***

Request :
```
POST /lesson HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 61
{
    "id": 1,
    "lessonName": "Math",
    "quota": 3
}
```
Response :
```
{
    "id": 1,
    "lessonName": "Math",
    "quota": 3,
    "updateDate": "2022-02-07T00:23:19.777+00:00"
}
```
NOT : Header -> Message : Lesson is added succesfully

- ***To get all lessons*** 

Request :
```
GET /lessons HTTP/1.1
Host: localhost:8080
```

Response :
```
[
    {
        "id": 1,
        "lessonName": "Math",
        "quota": 3,
        "updateDate": "2022-02-07T00:23:19.777+00:00"
    },
    {
        "id": 2,
        "lessonName": "Geography",
        "quota": 3,
        "updateDate": "2022-02-07T00:24:42.821+00:00"
    },
    {
        "id": 3,
        "lessonName": "Physic",
        "quota": 3,
        "updateDate": "2022-02-07T00:24:55.305+00:00"
    },
    {
        "id": 4,
        "lessonName": "Music",
        "quota": 3,
        "updateDate": "2022-02-07T00:25:03.045+00:00"
    }
]
```

- ***To get lesson with ID (3)***

Request :
```
GET /lesson/3 HTTP/1.1
Host: localhost:8080
```

Response :
```
{
    "id": 3,
    "lessonName": "Physic",
    "quota": 3,
    "updateDate": "2022-02-07T00:24:55.305+00:00"
}
```

- ***To update lesson with ID (3)***

Request:
```
PUT /lesson/3 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 73
{
    "id": 3,
    "lessonName": "Computer Science",
    "quota": 3
}
```
Response :
```
{
    "id": 3,
    "lessonName": "Computer Science",
    "quota": 3,
    "updateDate": "2022-02-07T00:43:36.405+00:00"
}
```
NOT : Header -> Message : Lesson is updated succesfully with id : 3

- ***To add lesson with ID (1) to student with ID (1)***

Request :
```
PUT /student/2/lesson/1 HTTP/1.1
Host: localhost:8080
Response :
```
NOT : Header -> Message : Lesson Math is added to Omer

- ***To delete lesson from Student with ID (3)***

Request :
```
DELETE /student/3/lesson/1 HTTP/1.1
Host: localhost:8080
```

Response :
NOT : Header -> Message : Lesson Math is removed from Nazim

- ***To list all lessons of Student with ID (3)***

Request :
```
GET /student/3/lessons HTTP/1.1
Host: localhost:8080
```

Response :
```
[
    {
        "id": 2,
        "lessonName": "Geography",
        "quota": 3,
        "updateDate": "2022-02-07T00:24:42.821+00:00"
    },
    {
        "id": 1,
        "lessonName": "Math",
        "quota": 3,
        "updateDate": "2022-02-07T00:23:19.777+00:00"
    }
]
```
- ***To list all students of lesson with ID (2)***

Request :
```
GET /lesson/2/students HTTP/1.1
Host: localhost:8080
```

Response :
```
[
    {
        "id": 1,
        "firstName": "Süleyman",
        "lastName": "Yüksel",
        "updateDate": "2022-02-07T00:19:24.817+00:00"
    },
    {
        "id": 3,
        "firstName": "Nazım",
        "lastName": "Cetinkaya",
        "updateDate": "2022-02-07T00:13:13.905+00:00"
    },
    {
        "id": 3,
        "firstName": "Nazım",
        "lastName": "Cetinkaya",
        "updateDate": "2022-02-07T00:13:13.905+00:00"
    }
]
```

- ***To prevent student take lesson that is reached limit of quota.***

Request :
```
PUT /student/4/lesson/2 HTTP/1.1
Host: localhost:8080
```

Response :
```
{
    "timestamp": "2022-02-07T00:33:06.708+00:00",
    "message": "Student Limit exceeded. The quota of Geography is : 3",
    "details": "uri=/student/4/lesson/2"
}
```
