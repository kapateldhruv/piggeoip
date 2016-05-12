# piggeoip

Allows you to extract geo specific properties from ip address.

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

### FUNC(String ip, String attributeName, String databasePath)
Allows you to retrive a specific information from the database. 
All the required databases to extract specific property are mentioned below. 


| attributeName  | Required database|
| ------------- | ------------- |
| COUNTRY_CODE  |[Country](https://www.maxmind.com/en/geoip2-country-database "GeoIP2 Country Database") or [City](https://www.maxmind.com/en/geoip2-city "GeoIP2 city Database")|
|COUNTRY_NAME  | [Country](https://www.maxmind.com/en/geoip2-country-database "GeoIP2 Country Database") or [City](https://www.maxmind.com/en/geoip2-city "GeoIP2 city Database") |
|CITY  | [City](https://www.maxmind.com/en/geoip2-city "GeoIP2 city Database") |
|POSTAL_CODE  | [City](https://www.maxmind.com/en/geoip2-city "GeoIP2 city Database") |
|LONGITUDE  | [City](https://www.maxmind.com/en/geoip2-city "GeoIP2 city Database") |
|LATITUDE   | [City](https://www.maxmind.com/en/geoip2-city "GeoIP2 city Database") |
|ASN   | [ISP](https://www.maxmind.com/en/geoip2-isp-database "GeoIP2 ISP Database") |
|ASN_ORG  | [ISP](https://www.maxmind.com/en/geoip2-isp-database "GeoIP2 ISP Database") |
|ISP | [ISP](https://www.maxmind.com/en/geoip2-isp-database "GeoIP2 ISP Database") |
|ORG  | [ISP](https://www.maxmind.com/en/geoip2-isp-database "GeoIP2 ISP Database") |
|IS_ANONYMOUS  | [Anonymous IP](https://www.maxmind.com/en/geoip2-anonymous-ip-database "GeoIP2 Anonymous IP Database") |
|IS_ANONYMOUS_VPN  | [Anonymous IP](https://www.maxmind.com/en/geoip2-anonymous-ip-database"GeoIP2 Anonymous IP Database) |
|IS_ISP  | [Anonymous IP](https://www.maxmind.com/en/geoip2-anonymous-ip-database "GeoIP2 Anonymous IP Database") |
|IS_PUBLIC_PROXY  | [Anonymous IP](https://www.maxmind.com/en/geoip2-anonymous-ip-database "GeoIP2 Anonymous IP Database") |
|IS_TOR_EXIT_NODE  | [Anonymous IP](https://www.maxmind.com/en/geoip2-anonymous-ip-database "GeoIP2 Anonymous IP Database") |
|DOMAIN  | [City](https://www.maxmind.com/en/geoip2-domain-name-database "GeoIP2 Domain Name Database") |
|CONNECTION  | [City](https://www.maxmind.com/en/geoip2-connection-type-database "GeoIP2 Connection Type Database") |


##### Note
```
I've not tested this for paid version of GeoIP2 databases.   
```



