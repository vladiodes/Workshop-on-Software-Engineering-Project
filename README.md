# Workshop-on-Software-Engineering-Project
List of dependencies to install:
- org.slf4j:slf4j-simple:1.7.26
- com.j2html:j2html:1.5.0
- org.apache.velocity:velocity-engine-examples:2.0
- com.google.guava:guava:18.0
- net.stickycode.mockwire:sticky-mockwire-mockito:5.1
- org.mockito:mockito-all:1.10.19
- io.javalin:javalin:3.13.7
- com.fasterxml.jackson.core:jackson-databind:2.0.2
- org.hibernate:hibernate-core:5.2.12.Final
- mysql:mysql-connector-java:8.0.29
- javax.xml.bind:jaxb-api:2.2.2
# How to work with the database:
You have to open an mySQL server and create 2 databases with the following names:
- mydb
- mydb_tests
* The server's username and password are (or you can choose your own and change persistance.xml config file):
* username: root
* password: admin

# How the Config file should be written:
you need to pass a JSON object stating the following properties:
- adminUsername: the user name of the default admin
- adminPassword: the password of the default admin
- persistence_unit: Market for regular functionality
                   MarketTests for tests functionality
- shouldPersist: True/False if you want to persist/not
- paymentSystem: Path to the payment system class you want to be used
- supplyingSystem: Path to the supplying system class you want to be used

# How the State file should be written:
you need to pass an array of JSON objects stating the following properites:
- command: Path to the command you want to execute
- params: The parameters you want to pass to the command (as a string representing JSON object)

# How to initialize the system with Script and Configuration file:
you need to pass 2 parameters to the program:
- param0 - Path to the Script file
- param1 - path to the Configuration file
