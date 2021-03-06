# MK:Smart entity-centric API configuration

This is a Maven project. Not a single JAR file has to be manually downloaded to assemble the project and its dependencies. Yep that's right.

## Dataset configuration

Every dataset that should contribute to a service generated by the ECAPI has to be of the following form:

```json
{
   /*
    * mnemonic URI (URL or URN)
    * mandatory
	*/
   "_id": "uri.mnemonic.dataset.identifier",
   /*
    * mandatory
    */
   "type": "provider-spec",
   /*
    * long integer
    * optional, maximum duration of a cached document in milliseconds.
	* Negative value means never cache, no value means cache forever.
	*/
   "mks:cache-lifetime": -1,
   /*
    * URL
	* mandatory if query_text or fetch_query is set
	*/
   "http://rdfs.org/ns/void#sparqlEndpoint": "http://dataset.name/sparql",
   "mks:types": {
       /* ... */
   }
}
```