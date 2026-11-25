import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

// Persists menu items locally as JSON so searches stay fast and survive missing or offline data.
public class MenuCache {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type ITEM_LIST_TYPE = new TypeToken<List<MenuItem>>() {}.getType();

    public static void saveToFile(String filePath, List<MenuItem> items) {
        try {
            String json = GSON.toJson(items);
            Files.writeString(Path.of(filePath), json);
        } catch (IOException e) {
            System.err.println("Could not save menu cache to " + filePath + ": " + e.getMessage());
        }
    }

    public static List<MenuItem> loadFromFile(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        try {
            String json = Files.readString(path);
            List<MenuItem> loaded = GSON.fromJson(json, ITEM_LIST_TYPE);
            return loaded != null ? loaded : Collections.emptyList();
        } catch (Exception e) {
            // Fall back cleanly if the cache file is empty or malformed JSON.
            System.err.println("Could not read menu cache from " + filePath + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}