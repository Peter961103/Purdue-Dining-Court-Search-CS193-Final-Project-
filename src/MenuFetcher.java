import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MenuFetcher {
    private static final String GRAPHQL_URL = "https://api.hfs.purdue.edu/menus/v3/GraphQL";
    private static final List<String> DINING_COURTS = List.of(
            "Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor"
    );

    private static final String QUERY =
            "query getLocationMenu($name: String!, $date: Date!) {" +
                    "  diningCourtByName(name: $name) {" +
                    "    name" +
                    "    dailyMenu(date: $date) {" +
                    "      meals {" +
                    "        name" +
                    "        stations {" +
                    "          name" +
                    "          items {" +
                    "            item { name }" +
                    "          }" +
                    "        }" +
                    "      }" +
                    "    }" +
                    "  }" +
                    "}";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // Fetch all 5 main dining courts for a given date
    public List<MenuItem> fetchAllMenus(LocalDate date) {
        List<MenuItem> allItems = new ArrayList<>();
        for (String court : DINING_COURTS) {
            try {
                allItems.addAll(fetchMenuForCourt(court, date));
            } catch (Exception e) {
                System.err.println("Failed to fetch menu for " + court + ": " + e.getMessage());
            }
        }
        return allItems;
    }

    // Fetch a single dining court's menu for a given date
    public List<MenuItem> fetchMenuForCourt(String diningCourtName, LocalDate date) throws Exception {
        JsonObject variables = new JsonObject();
        variables.addProperty("name", diningCourtName);
        variables.addProperty("date", date.toString()); // Formats as YYYY-MM-DD

        JsonObject payload = new JsonObject();
        payload.addProperty("operationName", "getLocationMenu");
        payload.add("variables", variables);
        payload.addProperty("query", QUERY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GRAPHQL_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP error " + response.statusCode());
        }

        return parseMenuItems(response.body());
    }

    private List<MenuItem> parseMenuItems(String jsonBody) {
        List<MenuItem> results = new ArrayList<>();

        JsonObject root = JsonParser.parseString(jsonBody).getAsJsonObject();
        JsonObject data = root.getAsJsonObject("data");
        if (data == null || data.get("diningCourtByName").isJsonNull()) {
            return results;
        }

        JsonObject courtObj = data.getAsJsonObject("diningCourtByName");
        String courtName = courtObj.get("name").getAsString();

        if (courtObj.get("dailyMenu").isJsonNull()) {
            return results;
        }
        JsonObject dailyMenu = courtObj.getAsJsonObject("dailyMenu");
        JsonArray meals = dailyMenu.getAsJsonArray("meals");
        if (meals == null) {
            return results;
        }

        for (JsonElement mealEl : meals) {
            JsonObject mealObj = mealEl.getAsJsonObject();
            String mealName = mealObj.get("name").getAsString();
            JsonArray stations = mealObj.getAsJsonArray("stations");
            if (stations == null) continue;

            for (JsonElement stationEl : stations) {
                JsonObject stationObj = stationEl.getAsJsonObject();
                String stationName = stationObj.get("name").getAsString();
                JsonArray items = stationObj.getAsJsonArray("items");
                if (items == null) continue;

                for (JsonElement itemWrapperEl : items) {
                    JsonObject itemWrapper = itemWrapperEl.getAsJsonObject();
                    if (!itemWrapper.has("item") || itemWrapper.get("item").isJsonNull()) continue;

                    JsonObject itemObj = itemWrapper.getAsJsonObject("item");
                    if (itemObj.has("name") && !itemObj.get("name").isJsonNull()) {
                        results.add(new MenuItem(
                                courtName,
                                mealName,
                                stationName,
                                itemObj.get("name").getAsString()
                        ));
                    }
                }
            }
        }

        return results;
    }
}