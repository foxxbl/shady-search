# Black Marketeer Search
GUI for parsing the X4: Foundations Game Save to allow easier search for Black Marketer vendors.

After loading the game save and selecting the sector, tool shows the stations in the sector and the status of black marketeers on them:
- Active (black marketeer unlocked)
- Inactive (black marketeer still locked)
- N/A (black marketeer not available on the station)

---

X4: Foundations is the registered trademark of the EGOSOFT GmbH 

---

## Development notes

- Application is based on Spring Boot 3.3 and JavaFX
- It uses JavaFX-Weaver for dependency injection support for JavaFX and FXML
- javafx plugin adds required JavaFx dependencies to the created boot jar based on the current os platform
- jpackage plugin creates the installation package

## Build and Run Shady Search - no debug
```bash
./gradlew clean bootRun
```

## package Shady Search
### Note - increase the version each time as update won't rewrite existing version 
```bash
./gradlew clean jpackage -i
```

### Windows requirements
Install WIX https://github.com/wixtoolset/wix3/releases

## run jpackage dry run
```bash
./gradlew jpackage  -i -PjvmArgs="-Djpackage.dryRun=true"

```

## Run Shady Search  with debug
```bash
./gradlew clean bootRun -PjvmArgs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
```
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -jar .\build\libs\shady-search-0.0.2-SNAPSHOT.jar
```

## Version tracking
```bash
git lfs track "*.msi" "*.deb"

```

# Links

JWeaver:
- https://github.com/rgielen/javafx-weaver

JavaFx Gradle plugin:
- https://github.com/openjfx/javafx-gradle-plugin

JPackage:
- https://github.com/petr-panteleyev/jpackage-gradle-plugin
- https://docs.oracle.com/en/java/javase/21/jpackage/packaging-tool-user-guide.pdf

