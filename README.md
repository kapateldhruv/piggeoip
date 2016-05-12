# piggeoip

Allows you to extract geo specific from ip address.

## Credits
Derived from hive version  https://github.com/Spuul/hive-udfs

## Compiling
This project uses Maven. Create your own jar by running following command.

```
cd piggeoip
mvn package
```
## Usage

It uses MaxMinds GeoIP2 Database. Databases can be downloaded from following links.
+ [Free](http://dev.maxmind.com/geoip/geoip2/geolite2 "Free GeoIP2 Databases")
+ [Paid](https://www.maxmind.com/en/geoip2-databases "Paid GeoIP2 Databases")

```
REGISTER piggeoip-0.0.1-SNAPSHOT.jar;
DEFINE geoip kapatel.dhruv.piggeoip.Geoip();
A = LOAD 'iplist.txt' AS (host:chararray);
B = FOREACH A GENERATE geoip(host,'COUNTRY_CODE','GeoLite2-City.mmdb');
```

### FUNC(String ip, String dataType, String databasePath)
Allows you to retrive a specific information from the database. 
The wanted information needs to be available in the used database. 
You can't retrieve an city information from a country database.


