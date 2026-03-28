# Copilot Instructions - Shady Search (X4 Black Marketeer Finder)

## Project Overview

Desktop JavaFX application that parses **X4: Foundations** game save files (gzipped XML) to locate Black Marketeer vendors across all sectors. Built with Spring Boot for DI/data and JavaFX + MaterialFX for the UI, wired together via JavaFX-Weaver.

- **Group:** `eu.foxxbl.x4.gameparser`
- **Main class:** `eu.foxxbl.x4.gameparser.shady.ShadySearchApplication`
- **Module:** `shady.search` (JPMS)

## Tech Stack and Versions

| Technology | Version | Notes |
|---|---|---|
| Java | 25 (Azul Zulu FX) | Toolchain-managed via Gradle |
| Gradle | 9.4.1 | Wrapper, Groovy DSL, version catalog `gradle/libs.versions.toml` |
| Spring Boot | 4.0.x | Non-web (`web-application-type: none`), Spring Framework 7.0 |
| Spring Data JPA | 4.0.x | `ListCrudRepository` interfaces |
| Hibernate | 7.2.x | Via `spring-boot-hibernate` module |
| JavaFX | 25 | `javafx.controls`, `javafx.fxml` modules |
| MaterialFX | 11.17.0 | UI theming and controls (MFXTableView, MFXButton, etc.) |
| JavaFX-Weaver | 2.0.1 | `net.rgielen:javafx-weaver-spring-boot-starter` for Spring DI in FXML controllers |
| JAXB | Jakarta XML Bind 4.0 | XML unmarshalling of game save components |
| HSQLDB | 2.7.x | Embedded read-only DB (`jdbc:hsqldb:res:`) for sector/faction translations |
| Lombok | via `io.freefair.lombok` 9.2.0 plugin | `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j` |
| Log4j2 | 2.25.x | Replaces default Logback (`spring-boot-starter-logging` excluded globally) |
| jpackage plugin | `org.panteleyev.jpackageplugin` 2.0.1 | Native installers (MSI/DEB/PKG) |

## Project Structure

```
src/main/java/
  module-info.java                          # JPMS module descriptor (shady.search)
  eu/foxxbl/x4/gameparser/shady/
    ShadySearchApplication.java             # @SpringBootApplication entry point
    config/
      ShadySearchConfig.java                # @ConfigurationProperties record (prefix: shady-search)
      UiConfig.java                         # Nested config record (title, width, height)
    model/
      entity/                               # JPA entities (HSQLDB-backed, read-only at runtime)
        FactionEntity.java                  # Faction ID -> name/shortName/mapType
        MapSectorEntity.java                # Sector macro -> sectorName/mapType
        MapType.java                        # Enum: DEFAULT, SPLIT, TERRAN, PIRATE, BORON, TIMELINES, MINI_01, MINI_02, UNKNOWN
      gamesave/                             # JAXB-annotated XML model (X4 save game structure)
        Component.java                      # Central XML element - sector/zone/station/NPC hierarchy
        ComponentClass.java                 # Enum with lookup map (GALAXY..SIGNALLEAK, OTHER)
        ComponentClassAdapter.java          # XmlAdapter for ComponentClass
        Connection.java                     # XML connection element
        ConnectionType.java                 # Enum with lookup map and ignore flags
        ConnectionTypeAdapter.java          # XmlAdapter for ConnectionType
        Connections.java                    # Wrapper for List<Connection>
        Control.java                        # Station control block (contains Post list)
        Post.java                           # Post element - identifies shady guy role
        Source.java                         # Station source/entry element
        Traits.java                         # NPC traits flags (tradesvisible = unlocked)
      parse/                                # Intermediate parsing results
        BlackMarketeer.java                 # Mutable class: station code, name, status, voice leaks
        ParsedMapSector.java                # @Builder record: per-sector parsing output
        ShadyGuyStatus.java                 # Enum: ACTIVE, INACTIVE, NONE
      ui/
        MapSector.java                      # @Builder record: UI model combining parsed + DB data
    parse/xml/
      X4SaveGameParser.java                 # StAX + JAXB streaming parser for .xml.gz game saves
      ShadyGuyParser.java                   # Recursive component tree walker for black marketeers
      filter/
        SectorFilter.java                   # StreamFilter record for XMLInputFactory.createFilteredReader
    repository/
      FactionRepository.java                # ListCrudRepository<FactionEntity, String>
      MapSectorRepository.java              # ListCrudRepository<MapSectorEntity, String>
    service/
      GameParsingService.java               # Orchestrates parsing, progress reporting, timing
      MapSectorService.java                 # Merges parsed data with DB sector/faction translations
    ui/
      application/
        SpringBootJavaFxApplication.java    # JavaFX Application subclass - boots Spring in init()
        PrimaryStageInitializer.java        # ApplicationListener<StageReadyEvent> - MaterialFX theme + stage
      controller/
        MainWindow.java                     # @FxmlView main controller - file chooser, table, DLC checkboxes
        SectorDialog.java                   # @FxmlView detail dialog - black marketeer table per sector
        ParseSaveGameTask.java              # javafx.concurrent.Task - off-FX-thread parsing with progress

src/main/resources/
  application.yaml                          # Spring config (shady-search.*, spring.datasource.*, spring.jpa.*)
  log4j2.xml                               # Console appender, debug for eu.foxxbl package
  shady-search.ico / .png                   # Application icons
  db/x4db.*                                 # HSQLDB embedded resource database (sector names, factions)
  eu/.../ui/controller/
    MainWindow.fxml                         # Main window layout (MaterialFX controls)
    SectorDialog.fxml                       # Sector detail dialog layout

src/test/
  module-info.java                          # Test module (shady.search.test)
  ShadySearchApplicationTests.java          # @SpringBootTest context load test

test/
  X4-save-sample.xml.gz                     # Sample game save for manual testing
```

## Architecture and Key Patterns

### Application Bootstrap

`Application.launch()` calls `SpringBootJavaFxApplication.init()` which boots the Spring context via `SpringApplicationBuilder`. Then `start()` publishes a `StageReadyEvent`. `PrimaryStageInitializer` (a Spring `@Component` implementing `ApplicationListener`) receives the event, applies MaterialFX theming, and loads the main window via `FxWeaver`.

### Dependency Injection in FXML Controllers

Controllers are Spring `@Component` beans annotated with `@FxmlView`. JavaFX-Weaver's `FxWeaver` loads FXML and injects Spring-managed dependencies. `FxControllerAndView<C,V>` provides lazy controller+view pairs (used for SectorDialog injection in MainWindow).

### XML Parsing Pipeline

1. `GameParsingService` orchestrates the parse
2. `X4SaveGameParser` opens `.xml.gz` via GZIPInputStream then StAX `XMLStreamReader` with `SectorFilter`
3. For each `<component class="sector">`, JAXB unmarshals the subtree into a `Component` graph
4. `ShadyGuyParser.findShadyGuys()` recursively walks zones, stations, NPCs to find black marketeers, check unlock status via traits, and count voice leaks
5. Results: `List<ParsedMapSector>` -> `MapSectorService.populateSectors()` joins with DB -> `List<MapSector>` for UI

### Threading Model

Parsing runs on a daemon `Thread` via `ParseSaveGameTask` (extends `javafx.concurrent.Task`). Progress is reported back to the FX thread via `updateProgress()`. Success/failure handlers update the UI on the FX application thread.

### Database

Read-only embedded HSQLDB (`jdbc:hsqldb:res:`) with two tables:
- `FACTION_ENTITY` - faction ID, translated name/shortName, MapType ordinal (TINYINT)
- `MAP_SECTOR_ENTITY` - sector macro, translated sector name, MapType ordinal (TINYINT)

MapType enum ordinals are stored as `TINYINT`. Entities use `@Enumerated(EnumType.ORDINAL)`.

## Build and Gradle Conventions

- **Version catalog:** All plugin and external library versions in `gradle/libs.versions.toml`
- **Spring Boot BOM:** Applied via `platform(SpringBootPlugin.BOM_COORDINATES)` - no explicit Spring dependency versions
- **Logging:** `spring-boot-starter-logging` globally excluded; `spring-boot-starter-log4j2` used instead
- **bootJar:** Produces `x4-black-marketeer-finder.jar` with implementation version/title from `gradle.properties`
- **jpackage 2.0.1:** Platform-specific `type.set()` inside `windows {}`, `linux {}`, `mac {}` blocks
- **Properties:** Build properties in `gradle.properties` (mainAppClass, packageAppName, packageVendor, etc.)

### Key Commands

```bash
./gradlew clean test              # Build and test
./gradlew clean bootRun           # Run the application
./gradlew clean jpackage -i       # Build native installer
./gradlew jpackage --dry-run      # Dry-run jpackage
```

## JPMS Module System

The project uses `module-info.java`. When adding new dependencies, add the corresponding `requires` directive. Current requires:
- `jakarta.persistence`, `jakarta.xml.bind`, `java.xml`
- `javafx.controls`, `javafx.fxml`
- `MaterialFX`, `net.rgielen.fxweaver.core`
- `org.slf4j`, `spring.boot`, `spring.boot.autoconfigure`, `spring.context`, `spring.core`
- `static lombok`

## Coding Conventions

- **Java records** for immutable data: configuration (`ShadySearchConfig`, `UiConfig`), parsing results (`ParsedMapSector`, `MapSector`), filters (`SectorFilter`)
- **Lombok** for boilerplate: `@Data` on mutable classes/entities, `@RequiredArgsConstructor` for constructor injection, `@Builder` for complex construction, `@Slf4j` for logging
- **Constructor injection** everywhere via `@RequiredArgsConstructor` - no field `@Autowired`
- **Enums with lookup maps** - `ComponentClass` and `ConnectionType` use `EnumSet`-based `Map<String, Enum>` with `fromString()` factory methods defaulting to `OTHER`
- **Spring stereotypes:** `@Service` for business/parsing logic, `@Repository` for data access, `@Component` for UI controllers and listeners
- **FXML controllers** annotated with `@Component` + `@FxmlView`; `@FXML` fields match `fx:id` in FXML files at the same package path under `src/main/resources/`
- **MaterialFX controls** throughout (not standard JavaFX controls): MFXTableView, MFXButton, MFXCheckbox, MFXProgressBar, MFXTextField, MFXTableRow, MFXTableRowCell
- **2-space indentation** in Java source files
- **Javadoc** on service methods and record parameters; inline comments for parsing logic

## Important Constraints

1. **HSQLDB schema is read-only at runtime** (`ddl-auto: none`). Schema changes require editing `src/main/resources/db/x4db.script` directly.
2. **MapType ordinals are persisted** as TINYINT - never reorder or insert enum values before existing ones; only append new values before `UNKNOWN`.
3. **FXML files must mirror controller package path** - e.g., `MainWindow.java` in `eu.foxxbl.x4.gameparser.shady.ui.controller` requires `MainWindow.fxml` at the same path under `src/main/resources/`.
4. **Game save XML structure** - the JAXB model in `model.gamesave` maps to the X4 save file format. Changes must match actual game save XML schema.
5. **Spring Boot 4.0 property paths** - `spring.jpa.database-platform` lives at the `spring.jpa` level (not under `spring.jpa.hibernate`). Under `spring.jpa.hibernate` only `ddl-auto` and `naming.*` are valid.
6. **jpackage plugin 2.0.1** - use `type.set()` inside platform blocks (`windows {}`, `linux {}`, `mac {}`), not at the outer jpackage level.
7. **No web layer** - this is a desktop application; `spring.main.web-application-type=none`.
8. **Module system** - any new dependency requires a `requires` entry in `module-info.java`.
