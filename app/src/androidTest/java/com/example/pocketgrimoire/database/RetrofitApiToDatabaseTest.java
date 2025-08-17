package com.example.pocketgrimoire.database;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.seeding.AbilitiesSeeder;
import com.example.pocketgrimoire.database.seeding.ItemsSeeder;
import com.example.pocketgrimoire.database.seeding.SpellsSeeder;
import com.example.pocketgrimoire.network.loaders.AbilitiesNetworkLoader;
import com.example.pocketgrimoire.network.loaders.ItemsNetworkLoader;
import com.example.pocketgrimoire.network.loaders.SpellsNetworkLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class RetrofitApiToDatabaseTest {

    private MockWebServer mockWebServer;
    private PocketGrimoireDatabase db;
    private ItemsDAO itemsDao;
    private SpellsDAO spellsDao;
    private AbilitiesDAO abilitiesDao;
    private ItemsSeeder itemsSeeder;
    private SpellsSeeder spellsSeeder;
    private AbilitiesSeeder abilitiesSeeder;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, PocketGrimoireDatabase.class)
                .allowMainThreadQueries() // Allowing main thread queries for simplicity in tests.
                .build();

        itemsDao = db.itemsDAO();
        spellsDao = db.spellsDAO();
        abilitiesDao = db.abilitiesDAO();

        // Configure Retrofit to use the mock server's URL
        DndApiService apiService = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(DndApiService.class);

        // Instantiate Loaders and Seeders with mock/in-memory dependencies
        ItemsNetworkLoader itemsLoader = new ItemsNetworkLoader(apiService);
        SpellsNetworkLoader spellsLoader = new SpellsNetworkLoader(apiService);
        AbilitiesNetworkLoader abilitiesLoader = new AbilitiesNetworkLoader(apiService);

        itemsSeeder = new ItemsSeeder(itemsLoader, itemsDao);
        spellsSeeder = new SpellsSeeder(spellsLoader, spellsDao);
        abilitiesSeeder = new AbilitiesSeeder(abilitiesLoader, abilitiesDao);
    }

    @After
    public void tearDown() throws IOException {
        db.close();
        mockWebServer.shutdown();
    }

    /**
     * Helper to read a JSON file from the test assets folder
     */
    private String readJsonFromAssets(String fileName) throws IOException {
        AssetManager am = InstrumentationRegistry.getInstrumentation()
                .getContext()                 // <- test APK context
                .getAssets();
        try (InputStream is = am.open("json/" + fileName)) {
            byte[] buffer = new byte[is.available()];
            int read = is.read(buffer);
            return new String(buffer, 0, read, StandardCharsets.UTF_8);
        }
    }

    private String addShieldToCategory(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        // DnD 5e “equipment-category” payloads usually have an array called "equipment".
        // If yours is "results" instead, adjust the key below.
        JsonArray arr = root.getAsJsonArray("equipment");
        if (arr == null) {
            // fall back if your asset uses "results"
            arr = root.getAsJsonArray("results");
        }
        if (arr == null) return json; // nothing to add to

        JsonObject shield = new JsonObject();
        shield.addProperty("index", "shield");
        shield.addProperty("name", "Shield");
        shield.addProperty("url", "/api/2014/equipment/shield"); // keep the /2014 prefix
        arr.add(shield);

        return root.toString();
    }

    private void logAllAssets(String path) {
        System.out.println("--- Listing Assets at path: '" + path + "' ---");
        try {
            AssetManager am = InstrumentationRegistry.getInstrumentation()
                    .getContext().getAssets();
            String[] assets = am.list(path);
            if (assets == null || assets.length == 0) {
                System.out.println("... found no assets.");
            } else {
                for (String a : assets) {
                    System.out.println("Found asset: " + a);
                    logAllAssets(path.isEmpty() ? a : path + "/" + a);
                }
            }
        } catch (IOException ex) {
            System.err.println("Could not list assets for path: " + path);
            ex.printStackTrace();
        }
        System.out.println("--- Finished listing assets for path: '" + path + "' ---");
    }

    @Test
    public void fullEtlWorkflow_LoadsAndTransformsDataCorrectly() throws Exception {
        logAllAssets("");
        // Set up the MockWebServer to return specific JSON files for specific URLs
        final Dispatcher dispatcher = new Dispatcher() {
            // This is a generic, empty response for API calls we don't care about in this test.
            private final String EMPTY_LIST_RESPONSE = "{\"count\":0,\"results\":[]}";
            @NonNull
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String path = request.getPath();
                System.out.println("MockWebServer dispatching for: " + path);

                if (path == null) return new MockResponse().setResponseCode(404);

                try {
                    if (path.equals("/api/2014/equipment-categories")) {
                        return new MockResponse().setResponseCode(200).setBody(readJsonFromAssets("equipment_categories.json"));
                    }
                    if (path.contains("/equipment-categories/heavy-armor")) {
                        String body = addShieldToCategory(readJsonFromAssets("category_heavy_armor.json"));
                        return new MockResponse().setResponseCode(200).setBody(body);
                    }
                    if (path.contains("/equipment-categories/adventuring-gear")) {
                        String body = addShieldToCategory(readJsonFromAssets("category_adventuring_gear.json"));
                        return new MockResponse().setResponseCode(200).setBody(body);
                    }
                    if (path.equals("/api/2014/equipment/shield")) {
                        return new MockResponse().setResponseCode(200)
                                .setBody(readJsonFromAssets("equipment_shield.json"));
                    }
                    if (path.equals("/api/2014/spells")) {
                        return new MockResponse().setResponseCode(200).setBody(readJsonFromAssets("spells.json"));
                    }
                    if (path.contains("/spells/fire-bolt")) {
                        // Added whitespace to test normalization
                        String body = readJsonFromAssets("spell_fire_bolt.json").replace("\"name\":\"Fire Bolt\"", "\"name\":\"  Fire Bolt  \"");
                        return new MockResponse().setResponseCode(200).setBody(body);
                    }
                    if (path.contains("/spells/magic-missile")) {
                        return new MockResponse().setResponseCode(200).setBody(readJsonFromAssets("spell_magic_missile.json"));
                    }
                    if (path.equals("/api/2014/features")) {
                        return new MockResponse().setResponseCode(200).setBody(readJsonFromAssets("features.json"));
                    }
                    if (path.equals("/api/2014/traits")) {
                        return new MockResponse().setResponseCode(200).setBody(readJsonFromAssets("traits.json"));
                    }
                    if (path.contains("/classes/fighter/features")) {
                        return new MockResponse().setResponseCode(200).setBody(readJsonFromAssets("features_fighter.json"));
                    }
                    if (path.contains("/classes/barbarian/features")) {
                        return new MockResponse().setResponseCode(200).setBody(readJsonFromAssets("features_barbarian.json"));
                    }
                    if (path.contains("/races/dwarf/traits")) {
                        return new MockResponse().setResponseCode(200).setBody(readJsonFromAssets("traits_dwarf.json"));
                    }
                    // Gracefully handle all other class/race requests
                    if (path.startsWith("/api/2014/classes/") || path.startsWith("/api/2014/races/")) {
                        return new MockResponse().setResponseCode(200).setBody(EMPTY_LIST_RESPONSE);
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // This will print the exact file-not-found error to the log.
                    // A 500 error probably means a typo in one of the filenames above
                    System.err.println("IOException for path: " + path + ". Check for typos in asset filenames.");
                    return new MockResponse().setResponseCode(500);
                }
                // If a request falls through, it's an unexpected URL
                System.err.println("Unhandled request for path: " + path);
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);

        // Run the entire seeding process and wait for it to complete.
        Completable fullSeed = itemsSeeder.seed()
                .andThen(spellsSeeder.seed())
                .andThen(abilitiesSeeder.seed());

        fullSeed.blockingAwait();

        // Query the in-memory database and verify the data.
        // Correct Item Count & De-duplication
        List<Items> allItems = itemsDao.getAllItems().blockingFirst();
        assertEquals("Should contain items from both categories, plus one unique Shield", 121, allItems.size());

        long shieldCount = allItems.stream().filter(item -> "Shield".equals(item.getName())).count();
        assertEquals("De-duplication should result in only one Shield", 1, shieldCount);

        // Spell & Ability Counts
        assertEquals("Should contain all spells from the trimmed mock list", 2, spellsDao.getAllSpells().blockingFirst().size());
        assertEquals("Should contain all abilities from the trimmed mock lists", 2, abilitiesDao.getAllAbilities().blockingFirst().size());

        // Whitespace Normalization
        Spells fireBolt = spellsDao.getAllSpells().blockingFirst().stream()
                .filter(s -> s.getName().contains("Fire Bolt")).findFirst().orElse(null);
        assertNotNull("Fire Bolt should be in the database", fireBolt);
        assertEquals("Name should be trimmed of whitespace", "Fire Bolt", fireBolt.getName());

        // Test isEquippable boolean
        Items ringMail = itemsDao.getAllItems().blockingFirst().stream()
                .filter(i -> "Ring Mail".equals(i.getName())).findFirst().orElse(null);
        Items abacus = itemsDao.getAllItems().blockingFirst().stream()
                .filter(i -> "Abacus".equals(i.getName())).findFirst().orElse(null);
        assertNotNull(ringMail);
        assertNotNull(abacus);
        assertTrue("Ring Mail should be equippable", ringMail.isEquippable());
        assertFalse("Abacus should not be equippable", abacus.isEquippable());

        // Test that Mappers/NetworkLoaders correctly get class dependencies
        Spells magicMissile = spellsDao.getAllSpells().blockingFirst().stream()
                .filter(s -> "Magic Missile".equals(s.getName())).findFirst().orElse(null);
        assertNotNull(magicMissile);
        assertEquals("Should have 2 classes", 2, magicMissile.getAvailableToClass().size());
        assertThat(magicMissile.getAvailableToClass(), hasItem("Sorcerer"));
        assertThat(magicMissile.getAvailableToClass(), hasItem("Wizard"));

        // Test that class dependencies are working for Abilities as well
        Abilities actionSurge = abilitiesDao.getAllAbilities().blockingFirst().stream()
                .filter(a -> a.getName().equals("Action Surge (1 use)")).findFirst().orElse(null);
        assertNotNull(actionSurge);
        // Using the display name from `seeding.ClassRaceMaps`
        assertThat(actionSurge.getAvailableToClass(), hasItem("Fighter"));
        assertThat(actionSurge.getAvailableToClass(), not(hasItem("Barbarian")));
    }
}
