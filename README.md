# dmb-boxscore-parser
Boxscore parse for Diamond Mind Baseball - used by HoFL

## Building

This project previously built via NetBeans (`build.xml` importing the generated
`nbproject/build-impl.xml`, which isn't checked in). It now builds with Maven instead:

```
mvn clean package
```

This compiles `src/`, pulls dependencies (Jackson 1.x, MySQL Connector/J) from Maven
Central instead of the jars vendored under `lib/`, and produces a self-contained,
executable jar at `target/dmb-boxscore-parser-1.0-SNAPSHOT.jar`.

## Running

```
java -jar target/dmb-boxscore-parser-1.0-SNAPSHOT.jar <org> <date> <overwrite> ...
```

See `com.hofl.parser.v2.JSONParserService.main` for the full argument signature.
Database and boxscore-location settings are read from `application.properties`
on the classpath (see `application.properties` in the project root for the
expected keys).
