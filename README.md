# X4 Black Marketeer Finder
GUI for parsing the X4: Foundations Game Save to allow easier search for Black Marketer vendors.

After loading the game save and selecting the sector, tool shows the stations in the sector and the status of black marketeers on them:
- Active (black marketeer unlocked)
- Inactive (black marketeer still locked)
- N/A (black marketeer not available on the station)

---
# Disclaimer
X4: Foundations and its DLCS (X4 DLCs:  X4: Split Vendetta, X4: Cradle of Humanity, X4: Tides of Avarice,X4: Kingdom End, X4: Timelines) are the registered trademarks of the EGOSOFT GmbH (https://www.egosoft.com/).

This project is not affiliated with, endorsed by, or in any way officially connected to Egosoft GmbH. 

It is an independent tool created by the game fan, to assist gamers by parsing X4 Foundation Game saves.

All trademarks, service marks, and company names are the property of their respective owners.
---

## Development notes

- Application is based on Spring Boot 3.3 and JavaFX
- It uses JavaFX-Weaver for dependency injection support for JavaFX and FXML
- javafx plugin adds required JavaFx dependencies to the created boot jar based on the current os platform
- jpackage plugin creates the installation package

## Build and Run - no debug
```bash
./gradlew clean bootRun
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


## Add support for Github LFS
### Install the LFS package (https://git-lfs.com/)
```bash
git lfs install
cd dist
git lfs track "*.msi" "*.deb"
git add .gitattributes
```

### Adding new large files
```bash
git lfs migrate import --include="*.msi, *.deb"
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
https://gluonhq.com/products/scene-builder/


