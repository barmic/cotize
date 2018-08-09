# How start the project

## Prerequises

- Java 8
- MongoDB

## Actions

- Require : JDK8, Apache Maven
- To build run : mvn clean package
- Run application : java -jar target/BonsComptes-jar-with-dependencies.jar
- Access http://localhost:5000/

## Configuration

Configuration use environment variable:

| Var name | usage | default value | type |
| --- | --- | --- | --- |
| `PORT` | port of http server | 5000 | integer |
| `ROOT_SECRET` | secret used in admin url | - | string |
| `MAIL_HOST` | host for send mail | - | string |
| `MAIL_PORT` | port for send mail | - | integer |
| `MAIL_USER` | user for send mail | - | string |
| `MAIL_PASSWORD` | password for send mail | - | string |
| `MONGO_HOST` | mongo | - | string |
| `MONGO_PORT` | mongo | - | string |
| `MONGO_DBNAME` | mongo | - | string |
| `MONGO_USER` | mongo | - | string |
| `MONGO_PASSWORD` | mongo | - | string |
