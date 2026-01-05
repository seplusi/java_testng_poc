import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ApiPostTests {
    HttpClient httpClient;
    String url = "https://api.restful-api.dev/objects";
    String id = "";

    @BeforeClass
    public void setUp() {
        System.out.println("BeforeClass actions");
        httpClient = HttpClient.newHttpClient();
    }

    @AfterClass
    public void tearDown() throws IOException, InterruptedException {
        System.out.println("AfterClass actions");
        if (!id.equals("")) {            
            HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/" + id)).header("Content-Type", "application/json").DELETE().build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            assert response.statusCode() == 200;
        }
    }

    @Test
    public void testCreateObjectUsingPostApiCall() throws IOException, InterruptedException {
        System.out.println("");
        String jsonPayLoad = "{\"name\": \"S69\",\"data\": {\"year\": 2019,\"price\": 69,\"CPU model\": \"Intel Core i9\",\"Hard disk size\": \"1 TB\"}}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayLoad)).build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject body = new JSONObject(response.body());
        id = body.get("id").toString();
        assert body.get("id").toString().matches("^[0-9a-f]*$");
        assert body.get("name").equals("S69");
        assert body.get("createdAt").toString().matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}\\+00:00$");
        assert (Integer)(((JSONObject) body.get("data")).get("year")) == 2019;
        assert (Integer)(((JSONObject) body.get("data")).get("price")) == 69;
        assert ((JSONObject) body.get("data")).get("CPU model").toString().equals("Intel Core i9");
        assert ((JSONObject) body.get("data")).get("Hard disk size").toString().equals("1 TB");
        System.out.println(body.get("id"));
    }

    @Test
    public void testVerifyCreateObject() throws IOException, InterruptedException {
        System.out.println("");
        String jsonPayLoad = "{\"name\": \"S69\",\"data\": {\"year\": 2019,\"price\": 69,\"CPU model\": \"Intel Core i9\",\"Hard disk size\": \"1 TB\"}}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayLoad)).build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject body = new JSONObject(response.body());
        id = body.get("id").toString();
        assert body.get("id").toString().matches("^[0-9a-f]*$");
        assert body.get("name").equals("S69");
        assert body.get("createdAt").toString().matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}\\+00:00$");
        assert (Integer)(((JSONObject) body.get("data")).get("year")) == 2019;
        assert (Integer)(((JSONObject) body.get("data")).get("price")) == 69;
        assert ((JSONObject) body.get("data")).get("CPU model").toString().equals("Intel Core i9");
        assert ((JSONObject) body.get("data")).get("Hard disk size").toString().equals("1 TB");
        
        request = HttpRequest.newBuilder(URI.create(url + "/" + id)).header("Content-Type", "application/json").GET().build();
        response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        body = new JSONObject(response.body());
        assert body.get("id").toString().matches("^[0-9a-f]*$");
        assert body.get("name").equals("S69");
        assert (Integer)(((JSONObject) body.get("data")).get("year")) == 2019;
        assert (Integer)(((JSONObject) body.get("data")).get("price")) == 69;
        assert ((JSONObject) body.get("data")).get("CPU model").toString().equals("Intel Core i9");
        assert ((JSONObject) body.get("data")).get("Hard disk size").toString().equals("1 TB");

        System.out.println(body.get("id"));
    }

    @Test
    public void testDeleteCreateObject() throws IOException, InterruptedException {
        String jsonPayLoad = "{\"name\": \"S69\",\"data\": {\"year\": 2019,\"price\": 69,\"CPU model\": \"Intel Core i9\",\"Hard disk size\": \"1 TB\"}}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonPayLoad)).build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject body = new JSONObject(response.body());
        id = body.get("id").toString();
        System.out.println(body.get("id"));
        
        request = HttpRequest.newBuilder(URI.create(url + "/" + id)).header("Content-Type", "application/json").DELETE().build();
        response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        body = new JSONObject(response.body());
        assert body.get("message").toString().equals("Object with id = " + id + " has been deleted.");
        
        request = HttpRequest.newBuilder(URI.create(url + "/" + id)).header("Content-Type", "application/json").GET().build();
        response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 404;
        body = new JSONObject(response.body());
        assert body.get("error").toString().equals("Oject with id=" + id + " was not found.");
        id = "";

        
    }

}
