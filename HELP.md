# save-organizer-interface

Vorlage für Microservices mit Spring Boot und Gradle
Das Interface kann in Kombination mit dem Save Organizer ganz ohne Konfiguration verwendet werden. Mit dem Befehl in Kapitel 'Gradle' kann die App
gestartet werden und im Hintergrund laufen.
Das Interface könnte auch für andere Service, Websiten usw. benutzt werden, die mit den Endunkten kommunizieren kann. Zu beachten ist der SaveFilePath
der entweder hier in den application.properties eingefügt werden muss oder über die Save Organizer Weboberfläche geändert werden kann.

## Voraussetzungen

* Ein lokal installiertes Java 17 JDK.
* *Optional: Eine lokale Gradle Installation. Es wird aber empfohlen den Gradle Wrapper (Version 8.7) zu nutzen, um Inkompatibilitäten zu
  vermeiden.*

## Einrichtung

### Gradle

* Die Anwendung kann mit Gradle per `gradlew bootRun` gestartet werden oder indem man `SaveOrganizerInterfaceApplication.java` in der IDE ausführt.

### IDE

* Das Projekt ist IDE-Unabhängig und sollte in jede IDE importiert werden können, getestet wurde die Funktion in IntelliJ.
* In IntelliJ kann das Projekt über die Importfunktion als Gradle Projekt importiert werden.
    * Per `File->New->Project from Existing Sources...` mit den Standardeinstellungen.
* In Eclipse kann das Projekt über die Importfunktion als Gradle Projekt importiert werden.
    * Per `File->Import...->Gradle->Existing Gradle Project` mit den Standardeinstellungen.
* In Eclipse kann das Boot Dashboard der Spring Tools zum Starten genutzt werden, alternativ muss man sich eine Runtime Configuration
  für `SaveOrganizerInterfaceApplication.java` erstellen.

## Konfiguration

* Kann in application.properties vorgenommen werden.

## Deployment

* Diese App kann nicht deployed und für andere Personen zur Verfügung gestellt werden. Es sollen keine Änderungen an einem anderen Computer passieren,
  sondern am Computer dessen, der die App einsetzen will.

## API Documentation

Swagger-UI erreichbar unter folgenden Adressen:

* http://localhost:8080/save-organizer-interface/swagger-ui/index.html

## Tech Stack

* Gradle für das Build-Management
* Java 17
* Spring Boot mit Spring MVC als Web-Framework
* Kafka
* S3
* Swagger zur API-Dokumentation
* LexConfig für Zugriff auf Vault
* SonarQube zur Code-Analyse
