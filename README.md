# README
### Description
This project is a technical test for calculating Interconnecting Flights

### Requirements
- *Java* version >= 17.
- *Gradle* version >= 7.6.

### Compile & Run Unit Tests 
In order to compile the project you should run the following command from project root path: `./gradlew clean build`

To run the JAR file just: `java -jar ./build/libs/finterconnection-0.0.1-SNAPSHOT.jar` from the project root path.

## Interconnection Flights

### Architecture

#### Service layer
There is one service defined, responsible for retrieving flight details. Two main operations:
* Get direct flights from origin to destination based on time schedule definitions.
* Get "one-stop" flights from origin to destination.

### Possible improvements

* Define a service which returns the available and updated IATA codes from airports as enum constants.
* Reduce complexity when reckoning one-step flights.
* Better parameter validation. At the moment it only checks that it fits in number and if all
  required are present.

### Run the Application
To start the application just type the following from the project root path
`java -jar ./build/libs/finterconnection-0.0.1-SNAPSHOT.jar`

The exec file "finterconnection-0.0.1-SNAPSHOT.jar" file has been generated
after compiling the repo and placed into the "build/libs" folder.

#### Examples
Type the following curl while the application is running:

`curl --location --request GET 'http://localhost:8282/ryanair/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2023-06-01T07:00&arrivalDateTime=2023-06-01T21:00'`

The application Log will show something similar to these output:

```
[{"stops":1,"legs":[{"departureAirport":"DUB","arrivalAirport":"MLA","departureDataTime":"2023-06-01T07:35","arrivalDateTime":"2023-06-01T12:20"},{"departureAirport":"MLA","arrivalAirport":"WRO","departureDataTime":"2023-06-01T17:30","arrivalDateTime":"2023-06-01T20:15"}]}]
```

If you do not type all the required parameters or any of them is missing/wrong formatted,
you will see something similar to the following log:

```
{"timestamp":"2023-01-16T00:11:43.366+00:00","status":400,"error":"Bad Request","path":"/ryanair/flights/interconnections"}
```

