# Development notes

- Application is based on Spring Boot 3.3, Java and JavaFX 21
- It uses JavaFX-Weaver for dependency injection support for JavaFX and FXML
- javafx plugin adds required JavaFx dependencies to the created boot jar based on the current os platform
- jpackage plugin creates the installation package(s)
- sector names and macros are stored into HSQLDB embedded databases (a bit overkill atm but will be required if application will support translation).

## Configuration
* Application parses game save data with XMLStreamReader and can parse in two ways
  * XML Filtered search (shady-search.filtered-stream-search-enabled=true) searching using createFilteredReader API based on the Component sector macro.
  * XML Stream search (shady-search.filtered-stream-search-enabled=false) goes over XML stream until it finds the Component sector macro searched.

Note: both methods perform very similar, some smaller gains with filtered stream search.

## Build and Run - filtered
```bash
./gradlew clean bootRun --args='--shady-search.filtered-stream-search-enabled=true'
```

## Build and Run - standard
```bash
./gradlew clean bootRun --args='--shady-search.filtered-stream-search-enabled=false'
```

## Run with debug
```bash
./gradlew clean bootRun -PjvmArgs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
```
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -jar .\build\libs\shady-search-0.0.2-SNAPSHOT.jar
```

## Package
Note - increase the version each time as update won't rewrite existing version
```bash
./gradlew clean jpackage -i
```

### jpackage dry-run
```bash
./gradlew jpackage  -i -PjvmArgs="-Djpackage.dryRun=true"
```

# Links

JWeaver:
- https://github.com/rgielen/javafx-weaver

JavaFx Gradle plugin:
- https://github.com/openjfx/javafx-gradle-plugin

JPackage:
- https://github.com/petr-panteleyev/jpackage-gradle-plugin
- https://docs.oracle.com/en/java/javase/21/jpackage/packaging-tool-user-guide.pdf

Windows installer package (required for JPackage on Windows):
- https://github.com/wixtoolset/wix3/releases

JavaFx Scene builder:
- https://gluonhq.com/products/scene-builder/
