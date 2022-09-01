# history-excerptor

This console application is used for generating an excerpt, which return all table data changes by the specific id.
Such functionality is available only for tables, which were marked as historical in registry regulation.
URL of generated excerpt can be found in correspondent K8s job metadata or, for local runs, in logs, and should be retrieved with a separate call

### Related components:

* PostgreSQL database for data persistence
* `excerpt-service-api` provides API for excerpt functionality
* `report-publisher` imports excerpt templates
* Ceph storage for storing required signatures

### Usage
###### mandatory arguments
* `tableName` - name of desirable registry table, example: `-DtableName=pd_processing_consent`
* `id` - id of entity in the registry table to look for, example: `-Did=47adfa00-e3ef-4aba-aa78-87efddbebc56`

### Local development:
###### Prerequisites:

* Postgres database is configured and running
  * Create excerpt db and run `/platform-db/changesets/excerpt/00010_initial-db-setup.sql` script from the `citus` repository
  * registry db has some data to look for
  * excerpt db with history excerpt template from `empty-template-registry-regulation/excerpts/HistoryExcerpt`
* `excerpt-service-api` is running with all its related services (`excerpt-service-worker` etc.)
* valid Keycloak token is generated for correct third-party systems calls
* Ceph/S3-like storage is configured and running

###### Configuration:
* `src/main/resources/application-local.yaml` with actual property values (thirdPartySystems.accessToken - Keycloak token from prerequisites, Ceph buckets and dso.url are mandatory to change)

###### Steps:

1. (Optional) Package application into jar file with `mvn clean package`
1. Add `--spring.profiles.active=local` to application run parameters
1. Add mandatory arguments to application run parameters
1. Run application with your favourite IDE or via `java -jar ...` with jar file, created above

### License
history-excerptor is Open Source software released under the Apache 2.0 license.