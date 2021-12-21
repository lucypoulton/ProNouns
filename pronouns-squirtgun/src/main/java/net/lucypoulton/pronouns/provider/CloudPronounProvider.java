package net.lucypoulton.pronouns.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import net.lucypoulton.pronouns.ProNouns;
import net.lucypoulton.pronouns.api.provider.PronounProvider;
import net.lucypoulton.pronouns.api.set.PronounSet;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class CloudPronounProvider implements PronounProvider {

    public static class DatabaseFile {
        public String source;
        public Date updatedAt;
        public Set<PronounSet> sets;

        public DatabaseFile(String source, Date updatedAt, Set<PronounSet> sets) {
            this.source = source;
            this.updatedAt = updatedAt;
            this.sets = sets;
        }
    }

    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(PronounSet.class,
            (JsonDeserializer<PronounSet>) (json, typeOfT, context) -> PronounSet.parse(json.getAsString())
        );
        gson = builder.create();
    }

    private final HttpClient client = HttpClient.newHttpClient();
    private final ProNouns plugin;
    private final Path dataFilePath;
    private DatabaseFile dataFileContent;

    public CloudPronounProvider(ProNouns plugin) {
        this.plugin = plugin;
        dataFilePath = plugin.getPlatform().getConfigPath(plugin).resolve("cloud.json");
        update();
    }

    public void reload() {
        if (!Files.exists(dataFilePath)) {
            update();
        }

        try {
            String fileContent = Files.readString(dataFilePath);
            dataFileContent = gson.fromJson(fileContent, DatabaseFile.class);
        } catch (IOException e) {
            plugin.getPlatform().getLogger().warning("Failed to read from the cloud database file: " + e.getMessage());
        }

        if (dataFileContent == null) {
            dataFileContent = new DatabaseFile("Internal error loading file", new Date(), Set.of());
        }
    }

    /**
     * Updates the stored list. This method blocks while it makes HTTP requests.
     */
    public void update() {
        if (!plugin.getConfigHandler().shouldSyncWithCloud()) {
            return;
        }
        try {
            plugin.getPlatform().getLogger().info("Updating the cloud database...");
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pn.lucypoulton.net/api/"))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(3))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Files.writeString(dataFilePath, response.body());
            plugin.getPlatform().getLogger().info("Successfully updated the cloud database.");
            reload();
        } catch (Exception e) {
            plugin.getPlatform().getLogger().warning("There was an issue trying to update the cloud database: "
                + e.getMessage());
        }
    }

    public void submit(PronounSet set) {
        try {
            String body = gson.toJson(Map.of(
                "set", set.toString(),
                "source", plugin.getPluginName() + " " + plugin.getPluginVersion() + " (" + plugin.getPlatform().name() + ")"
            ));

            HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create("https://pn.lucypoulton.net/api/"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(3))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new IOException("Error " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            plugin.getPlatform().getLogger()
                .warning("There was an error submitting a set to the cloud database. Please report this!" +
                    e.getMessage());
        }
    }

    @Override
    public Set<PronounSet> get() {
        return dataFileContent == null ? Set.of() : dataFileContent.sets;
    }

    public DatabaseFile getDatabase() {
        return dataFileContent;
    }
}
