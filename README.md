# API для Task service
>ver: 0.0.2

### Базовый URL: http://localhost:8080

### Стек<br>
* Java 21
* Spring boot
* Reactor
* JdbcClient
* PostgreSql
* Docker

<br>


### Сущность Task

```json
{
"id": 1,
"title": "string",
"description": "string",
"status": "NEW | IN_PROGRESS | DONE | CANCELLED",
"createdAt": "2025-03-31T10:15:30",
"updatedAt": "2025-03-31T10:15:30"
}
```
<br>

## Endpoints



### **1. Создание задачи**

#### **POST**  `/api/tasks`

#### Request body

```json
{
    "title": "{{$randomJobTitle}}",
    "description": "{{$randomJobDescriptor}}"
}
```
<br>

#### Успешный ответ "200 Created"

```json
{
    "id": 1,
    "title": "string",
    "description": "string",
    "status": "NEW",
    "createdAt": "2025-03-31T10:15:30",
    "updatedAt": "2025-03-31T10:15:30"
}
```
<br>

#### Ошибка валидации (400 Bad Request):

```json
{
    "statusCode": 400,
    "message": "Create task request parameters not valid",
    "zonedDateTime": "2026-03-31T20:57:12.747Z"
}
```

<br>

### **2. Получение задачи по ID**

#### **POST**  `/api/tasks/{id}`

#### Параметры url

* id(long) - идентификатор задачи

<br>

#### Успешный ответ "200 Ok"


> http://localhost:8080/api/tasks/1

```json
{
    "id": 1,
    "title": "string",
    "description": "string",
    "status": "NEW",
    "createdAt": "2025-03-31T10:15:30",
    "updatedAt": "2025-03-31T10:15:30"
}
```
<br>

#### Задача не найдена "404 Not Found"

> http://localhost:8080/api/tasks/-1

```json
{
    "statusCode": 404,
    "message": "Task not found with id: -1",
    "zonedDateTime": "2026-03-31T21:04:41.637Z"
}
```
<br>

### **3. Получение списка задач с пагинацией**

#### **POST**  `/api/tasks?page=0&size=2`

#### Параметры url

* page(int) - номер страницы (default value = 0)
* size(int) - размер страницы (default value = 10)

<br>

#### Успешный ответ "200 Ok"

> http://localhost:8080/api/tasks?page=0&size=2

```json
{
  "content": [
    {
      "id": 38,
      "title": "Human Communications Agent",
      "description": "Human",
      "status": "NEW",
      "createdAt": "2026-03-31T19:15:21.951332",
      "updatedAt": "2026-03-31T19:15:21.951343"
    },
    {
      "id": 37,
      "title": "Legacy Markets Analyst",
      "description": "Product",
      "status": "NEW",
      "createdAt": "2026-03-31T19:15:19.071955",
      "updatedAt": "2026-03-31T19:15:19.071962"
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 38,
  "totalPages": 19
}
```

<br>

#### Ошибка валидации "400 Bad Request"

> http://localhost:8080/api/tasks?page=0&size=-1

```json
{
    "statusCode": 400,
    "message": "Page size must not be less than one",
    "zonedDateTime": "2026-03-31T21:20:03.597Z"
}
```

<br>

### **4. Обновление статуса задачи**

#### **PATCH**  `/api/tasks/{id}/status`

#### Параметры url

* id(long) - идентификатор задачи

#### Request body

```json
{
  "status": "NEW | IN_PROGRESS | DONE | CANCELLED"
}
```

<br>

#### Успешный ответ "200 Ok"

```json
{
    "id": 1,
    "title": "Forward Group Designer",
    "description": "Forward",
    "status": "DONE",
    "createdAt": "2026-03-30T01:19:42.379635",
    "updatedAt": "2026-04-01T00:27:39.307641"
}
```

<br>

#### Ошибка валидации "404 Not Found"

```json
{
    "statusCode": 404,
    "message": "Task not found with id: -1",
    "zonedDateTime": "2026-03-31T21:33:13.727Z"
}
```
<br>


### **5. Удаление задачи**

#### **DELETE**  `/api/tasks/{id}`

#### Параметры url

* id(long) - идентификатор задачи

<br>

#### Успещный ответ "204 No Content"

> http://localhost:8080/api/tasks/{id}

> Путое тело ответа

<br>

#### Ошибка валидации "404 Not Found"

```json
{
    "statusCode": 404,
    "message": "Task not found with id: -1",
    "zonedDateTime": "2026-03-31T21:33:13.727Z"
}
```

<br>

## Рекомендации по запуску

### Docker file
> #### docker build --no-cache -t taskservice:0.0.2 .

```dockerfile
ARG IMAGE_TAG_MAVEN=maven:3.9.9-eclipse-temurin-21-alpine
ARG IMAGE_TAG_JRE=eclipse-temurin:21-jre-alpine
#Build stage
FROM $IMAGE_TAG_MAVEN AS build
WORKDIR /build/
COPY  pom.xml .
COPY src /build/src/
RUN mvn package -DskipTests clean package

#EXPOSE 8090

#Run stage
FROM $IMAGE_TAG_JRE
LABEL authors="onforus"
RUN mkdir -p /opt/taskservice/some
COPY --from=build /build/target/*.jar /opt/taskservice/app.jar
ENTRYPOINT ["java", "-jar", "/opt/taskservice/app.jar"]
```

### Docker compose
> #### docker-compose up -d --build

```dockerfile
services:
  db:
    image: postgres:17-alpine
    container_name: taskservice
    restart: always

    environment:
      POSTGRES_DB: taskservice
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin
    ports:
      - 5432:5432
    volumes:
      - taskservice:/var/lib/postgresql/data

  app:
    build: .
    container_name: taskservice-app
    env_file:
      - .env
    ports:
      - "8080:8080"
    depends_on:
      - db

volumes:
  taskservice:
```