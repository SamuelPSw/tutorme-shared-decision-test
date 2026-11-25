# TutorMe backend

Install JDK 21, then run the following from this directory.

| Task | PowerShell | Linux or macOS |
| --- | --- | --- |
| Start | `./mvnw.cmd spring-boot:run` | `sh ./mvnw spring-boot:run` |
| Test and package | `./mvnw.cmd verify` | `sh ./mvnw verify` |

The REST API is available at `http://localhost:8080/api`. The H2 database starts empty and resets when the application stops.

New notes start unapproved. The family endpoint returns all notes for the requested family, including unapproved notes. Creating a note requires demo family ID 1 or 2 and a nonblank title and content. A numeric family ID without notes returns an empty array.
