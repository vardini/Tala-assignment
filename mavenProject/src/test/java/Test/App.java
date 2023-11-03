package Test;


import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.path.json.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.*;
import java.lang.*;

public class App {
String NOPERM_API_KEY = "AIzaSyBltatf3KXe8eIwxQ3dimBh4mrvO2N3r7g";
String INVALID_API_KEY = "AIzaSyCnTiF4rwIbp1uKIXkCkvUuSzwLrfNRpkU";
String VALID_API_KEY = "AIzaSyCnTiF4rwIbp1uKIXkCkvUuSzwLrfNRpkU";

String baseURI = ("https://www.googleapis.com/geolocation/v1/geolocate");

JSONParser parser = new JSONParser();

@Test
public void keyWihoutPermission() throws ParseException {
		
		
		JSONObject request = new JSONObject();
		request.put("radioType","gsm");

		Response getResponse = 
			 given()
				.queryParam("key", NOPERM_API_KEY)
				.body(request.toJSONString())
		        
			.when()
				.post(baseURI)
			
			.then()
				.statusCode(403)
				.extract().response();
				
				
		//System.out.print(getResponse.getBody().asString());
		
		JSONObject json = (JSONObject) parser.parse(getResponse.getBody().asString());
		String message=((JSONObject)json.get("error")).get("message").toString();
		
		
		
		Assert.assertTrue(message.equalsIgnoreCase("PERMISSION_DENIED: You must enable Billing on the Google Cloud Project"));							
	
}



@Test
public void invalidKey() throws ParseException {
		
		
		JSONObject request = new JSONObject();
		request.put("homeMobileCountryCode",310);
		request.put("homeMobileNetworkCode",410);
		request.put("radioType","gsm");
		request.put("carrier","Vodafone");
		request.put("considerIp",true);
		

		Response getResponse = 
			 given()
				.queryParam("key", INVALID_API_KEY)
				.body(request.toJSONString())
		        
			.when()
				.post(baseURI)
			
			.then()
				.statusCode(400)
				//.body("message", equalTo("PERMISSION_DENIED: You must enable Billing on the Google Cloud Project"))
				.extract().response();
				
				
		System.out.print(getResponse.getBody().asString());
		
		
		JSONObject json = (JSONObject) parser.parse(getResponse.getBody().asString());
		JSONObject errorJSON = (JSONObject)json.get("error");
		JSONObject detailsJSON = (JSONObject) ((JSONArray)errorJSON.get("details")).get(0);
		
		String message=errorJSON.get("message").toString();
		String reason=detailsJSON.get("reason").toString();
		
		
		
		Assert.assertTrue(message.equalsIgnoreCase("API key not valid. Please pass a valid API key."));	
		Assert.assertTrue(reason.equals("API_KEY_INVALID"));	
	
}




@Test
public void validKey() throws ParseException {
	  	
		JSONArray array = new JSONArray();
	    array.add("macAddress:94:b4:0f:fd:c1:40");
	    array.add("ignalStrength:-35");
	    array.add("signalToNoiseRatio:0");
		
		JSONObject request = new JSONObject();
		request.put("wifiAccessPoints",array);
		request.put("considerIp",false);
		

		Response getResponse = 
			 given()
				.queryParam("key", VALID_API_KEY)
				.body(request.toJSONString())
		        
			.when()
				.post(baseURI)
			
			.then()
				.statusCode(200)
				.extract().response();
				
				
		//System.out.print(getResponse.getBody().asString());
		
		
		JSONObject json = (JSONObject) parser.parse(getResponse.getBody().asString());
		String lat=((JSONObject)json.get("location")).get("lat").toString();
		String lng=((JSONObject)json.get("location")).get("lng").toString();
		String accuracy=((JSONObject)json.get("accuracy")).toString();
		
		float floatlat=Float.parseFloat(lat);
		float floatlng=Float.parseFloat(lng);
		float floataccuracy=Float.parseFloat(accuracy);
		
			
		Assert.assertTrue(floatlat>36 && floatlat<38);
		Assert.assertTrue(floatlng>-120 && floatlng<-124);
		Assert.assertTrue(floataccuracy<30);
		

}
}



