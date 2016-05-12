/**
 * Geoip.java.
 
 * Copyright (C) 2016 kapatel dhruv,
 * https://github.com/kapateldhruv
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package kapatel.dhruv.piggeoip;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;


import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AnonymousIpResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.ConnectionTypeResponse;
import com.maxmind.geoip2.model.ConnectionTypeResponse.ConnectionType;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.model.DomainResponse;
import com.maxmind.geoip2.model.IspResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;

/**
 * This is a UDF to look a property of an IP address using MaxMind GeoIP2
 * library.
 *
 * The function will need three arguments. 
 * 1. IP Address in string format.
 * 2. IP attribute (e.g. COUNTRY, CITY, REGION, etc)
 * 3. Database file name.
 *
 * This is a derived from hive udf version from https://github.com/Spuul/hive-udfs
 * 
 */

public class Geoip extends EvalFunc<String> {
    public String exec(Tuple input) throws IOException {
        if (input == null ||  input.size() == 0)
            return null;
        try {
        	
        	if(input.size() != 3)
        	{
        		throw new IllegalArgumentException("This function takes three arguments as input.");
        	}
        	
        	String ip = input.get(0).toString();
            String attributeName = input.get(1).toString();
            String databaseName = input.get(2).toString();

            //Just in case there are more than one database filename attached.
            //We will just assume that two file with same filename are identical.
            File database = new File(databaseName);

            String retVal = "";

            try {
                    // This creates the DatabaseReader object, which should be reused across
                    // lookups.
                    DatabaseReader reader = new DatabaseReader.Builder(database).build();
                    String databaseType = reader.getMetadata().getDatabaseType();
                    InetAddress ipAddress = InetAddress.getByName(ip);

                    switch (databaseType) {
                            case "GeoIP2-Country":
                            case "GeoLite2-Country":
                                    retVal = getVal(attributeName, reader.country(ipAddress));
                                    break;
                            case "GeoIP2-City":
                            case "GeoLite2-City":
                                    retVal = getVal(attributeName, reader.city(ipAddress));
                                    break;
                            case "GeoIP2-Anonymous-IP":
                                    retVal = getVal(attributeName, reader.anonymousIp(ipAddress));
                                    break;
                            case "GeoIP2-Connection-Type":
                                    retVal = getVal(attributeName, reader.connectionType(ipAddress));
                                    break;
                            case "GeoIP2-Domain":
                                    retVal = getVal(attributeName, reader.domain(ipAddress));
                                    break;
                            case "GeoIP2-ISP":
                                    retVal = getVal(attributeName, reader.isp(ipAddress));
                                    break;
                            default:
                                    throw new UnsupportedOperationException("Unknown database type " + databaseType);
                    }
                    return retVal;
            }
            catch(Exception e) {
                    return new String("");
            }

        } catch (Exception e) {
            // TODO: handle exception
        	 throw new IOException("Caught exception processing input row ", e);
        }
    }

    public static String getVal(String dataType, CountryResponse response) throws IOException, GeoIp2Exception {
        if (dataType.equals("COUNTRY_CODE") || dataType.equals("COUNTRY_NAME")) {
                Country country = response.getCountry();
                if (dataType.equals("COUNTRY_CODE")) {
                        return country.getIsoCode();
                }
                else {
                        return country.getName();
                }
        }
        else {
                throw new UnsupportedOperationException("Unable get " + dataType + " for Database Type " + response.getClass().getSimpleName());
        }
}

public static String getVal(String dataType, CityResponse response) throws IOException, GeoIp2Exception {
        if (dataType.equals("COUNTRY_CODE")
            || dataType.equals("COUNTRY_NAME")
            || dataType.equals("SUBDIVISION_NAME")
            || dataType.equals("SUBDIVISION_CODE")
            || dataType.equals("CITY")
            || dataType.equals("POSTAL_CODE")
            || dataType.equals("LONGITUDE")
            || dataType.equals("LATITUDE")
        ) {
                if (dataType.equals("COUNTRY_CODE") || dataType.equals("COUNTRY_NAME")) {
                        Country country = response.getCountry();
                        if (dataType.equals("COUNTRY_CODE")) {
                                return country.getIsoCode();
                        }
                        else {
                                return country.getName();
                        }
                }
                if (dataType.equals("SUBDIVISION_CODE") || dataType.equals("SUBDIVISION_NAME")) {
                        Subdivision subdivision = response.getMostSpecificSubdivision();
                        if (dataType.equals("SUBDIVISION_CODE")) {
                                return subdivision.getIsoCode();
                        }
                        else {
                                return subdivision.getName();
                        }
                }
                if (dataType.equals("CITY")) {
                        City city = response.getCity();
                        return city.getName();
                }
                if (dataType.equals("POSTAL_CODE")) {
                        Postal postal = response.getPostal();
                        return postal.getCode();
                }
                if (dataType.equals("LONGITUDE") || dataType.equals("LATITUDE")) {
                        Location location = response.getLocation();
                        if (dataType.equals("LONGITUDE")) {
                                return location.getLongitude().toString();
                        }
                        else {
                                return location.getLatitude().toString();
                        }
                }
                return "";
        }
        else {
                throw new UnsupportedOperationException("Unable get " + dataType + " for Database Type " + response.getClass().getSimpleName());
        }
}

public static String getVal(String dataType, IspResponse response) throws IOException, GeoIp2Exception {
        String retVal = "";
        switch (dataType) {
                case "ASN":
                        retVal = response.getAutonomousSystemNumber().toString();
                        break;
                case "ASN_ORG":
                        retVal = response.getAutonomousSystemOrganization();
                        break;
                case "ISP":
                        retVal = response.getIsp();
                        break;
                case "ORG":
                        retVal = response.getOrganization();
                        break;
                default:
                        throw new UnsupportedOperationException("Unable get " + dataType + " for Database Type " + response.getClass().getSimpleName());
        }
        return retVal;
}

public static String getVal(String dataType, AnonymousIpResponse response) throws IOException, GeoIp2Exception {
        Boolean retVal = false;
        switch (dataType) {
                case "IS_ANONYMOUS":
                        retVal = response.isAnonymous();
                        break;
                case "IS_ANONYMOUS_VPN":
                        retVal = response.isAnonymousVpn();
                        break;
                case "IS_ISP":
                        retVal = response.isHostingProvider();
                        break;
                case "IS_PUBLIC_PROXY":
                        retVal = response.isPublicProxy();
                        break;
                case "IS_TOR_EXIT_NODE":
                        retVal = response.isTorExitNode();
                        break;
                default:
                        throw new UnsupportedOperationException("Unable get " + dataType + " for Database Type " + response.getClass().getSimpleName());
        }
        return retVal ? "true" : "false";
}

public static String getVal(String dataType, DomainResponse response) throws IOException, GeoIp2Exception {
        if (dataType.equals("DOMAIN")) {
                return response.getDomain();
        }
        else {
                throw new UnsupportedOperationException("Unable get " + dataType + " for Database Type " + response.getClass().getSimpleName());
        }
}

public static String getVal(String dataType, ConnectionTypeResponse response) throws IOException, GeoIp2Exception {
        if (dataType.equals("CONNECTION")) {
                return response.getConnectionType().toString();
        }
        else {
                throw new UnsupportedOperationException("Unable get " + dataType + " for Database Type " + response.getClass().getSimpleName());
        }
}
    
}
