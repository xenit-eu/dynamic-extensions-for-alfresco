===============================================================================
JSON REST API
===============================================================================

This is an experimental Dynamic Extension implementating a JSON-based REST API. 
This API is an alternative to CMIS and the standard Alfresco API for accessing 
the repository and is intended for JavaScript client-side development.

Build this extensions using Maven: mvn install 

To install this extension, place the file target/json-rest-api-<version>.jar in
the repository folder /Data Dictionary/Dynamic Extensions/Extension Bundles.

(Previous versions required you to upload the Jackson JSON library separately, but
this is no longer necessary, as this library is now embedded in the bundle.)