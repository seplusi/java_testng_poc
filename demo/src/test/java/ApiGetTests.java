import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ApiGetTests {
    
    HttpClient httpClient;
    String url = "https://doaj.org/api/";

    @BeforeClass
    public void setUp() {
        System.out.println("BeforeClass actions");
        httpClient = HttpClient.newHttpClient();
    }

    @AfterClass
    public void tearDown() {
        System.out.println("AfterClass actions");
    }

    @Test
    public void filterJournalsByTitle() throws IOException, InterruptedException {
        System.out.println("Test api call with title filter");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "search/journals/title:fuel")).GET().build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject jsonObject = new JSONObject(response.body());
        assert jsonObject.getJSONArray("results").length() == 5;
        JSONArray listValues = jsonObject.getJSONArray("results");
        // Assert all journals titles have the word fuel
        for (int i = 0; i < listValues.length(); i++) {
            assert listValues.getJSONObject(i).getJSONObject("bibjson").getString("title").toLowerCase().contains("fuel");
        }
    }

    @Test
    public void filterJournalsByTitleAndSortByLastUpdated() throws IOException, InterruptedException {
        System.out.println("Test api call with title filter and sort last_update asc");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "search/journals/title:fuel?sort=last_updated:asc")).GET().build();
        var response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject jsonObject = new JSONObject(response.body());
        assert jsonObject.getJSONArray("results").length() == 5;
        JSONArray listValues = jsonObject.getJSONArray("results");
        // Assert all journals titles have the word fuel
        for (int i = 0; i < listValues.length(); i++) {
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            assert listValues.getJSONObject(i).getJSONObject("bibjson").getString("title").toLowerCase().contains("fuel");
            if ( i < listValues.length() - 1) {
                String currentArticleLastUpdate = listValues.getJSONObject(i).getString("last_updated");
                String nextArticleLastUpdate = listValues.getJSONObject(i + 1).getString("last_updated");
                assert Instant.parse(currentArticleLastUpdate).getEpochSecond() < Instant.parse(nextArticleLastUpdate).getEpochSecond();
            }
        }
    }

    @Test
    public void filterArticlesByTitle() throws IOException, InterruptedException {
        System.out.println("Test api call to get articles with title filter");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "search/articles/title:fuel")).GET().build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray listValues = jsonObject.getJSONArray("results");
        assert listValues.length() == 10;
        assert jsonObject.getInt("pageSize") == 10;
        // Assert all journals titles have the word fuel
        for (int i = 0; i < listValues.length(); i++) {
            assert listValues.getJSONObject(i).getJSONObject("bibjson").getString("title").toLowerCase().contains("fuel");
        }
    }

    @Test
    public void filterArticlesByTitleAndSortByLastUpdated() throws IOException, InterruptedException {
        System.out.println("Test api call to get articles with title filter and sorting by last_update asc");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "search/articles/title:fuel?sort=last_updated:asc")).GET().build();
        var response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray listValues = jsonObject.getJSONArray("results");
        assert listValues.length() == 10;
        assert jsonObject.getInt("pageSize") == 10;
        // Assert all journals titles have the word fuel
        for (int i = 0; i < listValues.length(); i++) {
            assert listValues.getJSONObject(i).getJSONObject("bibjson").getString("title").toLowerCase().contains("fuel");
            // Verify order by last_updated
            if ( i < listValues.length() - 1) {
                String currentArticleLastUpdate = listValues.getJSONObject(i).getString("last_updated");
                String nextArticleLastUpdate = listValues.getJSONObject(i + 1).getString("last_updated");
                assert Instant.parse(currentArticleLastUpdate).getEpochSecond() <= Instant.parse(nextArticleLastUpdate).getEpochSecond();
            }
        }
    }

    @Test
    public void testMaxPageSize() throws IOException, InterruptedException {
        System.out.println("Test api call with title filter and check max page size");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "search/articles/title:fuel?sort=last_updated:asc&pageSize=1000")).GET().build();
        var response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray listValues = jsonObject.getJSONArray("results");
        assert listValues.length() == 100;
        assert jsonObject.getInt("pageSize") == 100;
        assert jsonObject.getInt("total") > jsonObject.getInt("pageSize");

    }

    @Test
    public void testDefaultPageSize() throws IOException, InterruptedException {
        System.out.println("Test api call with title filter and check default page size");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "search/articles/title:fuel?sort=last_updated:asc")).GET().build();
        var response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject jsonObject = new JSONObject(response.body());
        JSONArray listValues = jsonObject.getJSONArray("results");
        assert listValues.length() == 10;
        assert jsonObject.getInt("pageSize") == 10;
        assert jsonObject.getInt("total") > jsonObject.getInt("pageSize");
    }

    @Test
    public void testNextPages() throws IOException, InterruptedException {
        System.out.println("Test api call with title filter and check all next pages");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "search/articles/title:Biomass+gasification?sort=last_updated:asc&pageSize=100")).GET().build();
        var response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        JSONObject jsonObject = new JSONObject(response.body());
        int totalArticls = jsonObject.getInt("total");
        int numPages = totalArticls / 100;
        int lastPageNumItems = totalArticls % 100;
        List<Long> listLastUpdateTs = new ArrayList<>();
        // Go through all the pages that have max page size
        for (int i = 1; i < numPages + 1; i++) {
            request = HttpRequest.newBuilder(URI.create(url + "search/articles/title:Biomass+gasification?sort=last_updated:asc&pageSize=100&page=" + i)).GET().build();
            response = httpClient.send(request, BodyHandlers.ofString());
            assert response.statusCode() == 200;            
            jsonObject = new JSONObject(response.body());
            assert jsonObject.getInt("total") == totalArticls;
            assert jsonObject.getInt("page") == i;
            assert jsonObject.getInt("pageSize") == 100;
            assert jsonObject.get("query").equals("title:Biomass+gasification");
            JSONArray listResults = jsonObject.getJSONArray("results");
            assert listResults.length() == 100;
            for (int j = 0; j < 100; j++) {
                // Assert title contains the right strings
                JSONObject result = listResults.getJSONObject(j);
                String title = result.getJSONObject("bibjson").getString("title").toLowerCase();
                assert title.contains("biomass");
                assert title.contains("gasification");
                String currentArticleLastUpdate = listResults.getJSONObject(j).getString("last_updated");
                // Assert the last_update is ordered in the current page
                if ( j < listResults.length() - 1) {
                    //String currentArticleLastUpdate = listResults.getJSONObject(j).getString("last_updated");
                    String nextArticleLastUpdate = listResults.getJSONObject(j + 1).getString("last_updated");
                    assert Instant.parse(currentArticleLastUpdate).getEpochSecond() <= Instant.parse(nextArticleLastUpdate).getEpochSecond();
                }
                listLastUpdateTs.add(Instant.parse(currentArticleLastUpdate).getEpochSecond());
            }
        }
        // Assert the last page that has less than 100 items
        request = HttpRequest.newBuilder(URI.create(url + "search/articles/title:Biomass+gasification?sort=last_updated:asc&pageSize=100&page=" + (numPages + 1))).GET().build();
        response = httpClient.send(request, BodyHandlers.ofString());
        assert response.statusCode() == 200;
        jsonObject = new JSONObject(response.body());    
        assert jsonObject.getInt("total") == totalArticls;
        assert jsonObject.getInt("page") == numPages + 1;
        assert jsonObject.getInt("pageSize") == 100;
        assert jsonObject.get("query").equals("title:Biomass+gasification");
        //assert jsobObject.get("query").equals("title:fuel");
        JSONArray listResults = jsonObject.getJSONArray("results");
        assert listResults.length() == lastPageNumItems;
        for (int k = 0; k < lastPageNumItems; k++) {
            JSONObject result = listResults.getJSONObject(k);
            String title = result.getJSONObject("bibjson").getString("title").toLowerCase();
            assert title.contains("biomass");
            assert title.contains("gasification");
            String currentArticleLastUpdate = listResults.getJSONObject(k).getString("last_updated");
            if ( k < listResults.length() - 1) {
                String nextArticleLastUpdate = listResults.getJSONObject(k + 1).getString("last_updated");
                assert Instant.parse(currentArticleLastUpdate).getEpochSecond() <= Instant.parse(nextArticleLastUpdate).getEpochSecond();
            }
            listLastUpdateTs.add(Instant.parse(currentArticleLastUpdate).getEpochSecond());
        }
        for (int z = 0; z < listLastUpdateTs.size() - 1; z++) {
            assert listLastUpdateTs.get(z) <= listLastUpdateTs.get(z + 1);
        }
    }
}
