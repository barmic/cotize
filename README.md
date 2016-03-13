# How start the project

## Prerequises

- Java 8
- MongoDB

## Actions

- Require : JDK8, Apache Maven
- To build run : mvn clean package
- Run application : java -jar target/BonsComptes-jar-with-dependencies.jar -conf run-config.json
- Access http://localhost:5000/

### Example of run-config.json

```json
{
	"root_secret" : "abcdefghijkl",
  "base_url": "http://localhost:5000",
  "mail" : {
    "user" : "user@example.com",
    "password" : "<password>",
    "port" : 465,
    "host" : "smtp.example.com"
  },
  "mongo" : {
  	"host" : "localhost",
  	"port" : 27017,
  	"collection" : "CotizeEvents",
  	"dbname" : "bonscomptes"
  }
}
```

- root_secret : id (used in url) to access to root page
- base_url : url used for link send by mail
- mail : informations for send email
- mongo : informations to access to mongodb
