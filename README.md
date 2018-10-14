## Cotize

Also known as "Bons comptes" is a java web app that helps friends to collect pledges in order to buy gift for birthday or other events.
- Cotize do not collect any money, just pledges. So make sure your friends are trustworthy :)
- Cotize is released under a derivate licence of the [WTFPL](http://www.wtfpl.net). So you can do anything you want to with it

### Installation
#### Prerequises
- JDK8
- Maven
- MongoDB

#### Get, Build, Run
1. Get: `git clone https://github.com/barmic/cotize.git`
2. Go to main folder: `cd cotize`
3. Configure by editing file: `./infra/run-config.json` (See below for config examples)
4. Buid: `mvn clean package`
5. Run: `java -jar target/BonsComptes-jar-with-dependencies.jar -conf run-config.json`
6. Test by accessing: http://localhost:5000/

#### Example of run-config.json

```json
{
  "root_secret" : "abcdefghijkl",
  "base_url": "http://localhost:5000",
  "mail" : {
    "user" : "user@example.com",
    "password" : "<password>",
    "port" : 465,
    "host" : "smtp.example.com",
    "use_ssl" : true
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

#### Mail settings in run-config.js

|          | Gmail          | Lilo          |
|----------|----------------|---------------|
| user     | user@gmail.com | user@lilo.org |
| password | password       | password      |
| port     | 465            | 587           |
| host     | smtp.gmail.com | mail.lilo.org |
| use_ssl  | true           | false         |

### Install Cotize as a Linux service
TODO
