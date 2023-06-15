package manager.services.searchproviders;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.image.BufferedImage;

import org.json.JSONArray;
import org.json.JSONObject;

import manager.interfaces.SearchProvider;
import manager.models.CoverSearchResult;
import manager.models.GameSearchResult;
import manager.models.Settings;
import manager.services.SettingsService;

public class IGDBUtil implements SearchProvider {
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private String OAUTH_TOKEN;
    private final String API_URL = "https://api.igdb.com/v4/games";
    private final static String OAUTH_URL = "https://id.twitch.tv/oauth2/token";

    public List<GameSearchResult> searchGames(String query) throws IOException {
        Settings settings = SettingsService.loadSettings();
        if (settings != null) {
            CLIENT_ID = settings.getApiSettings().getIgdbClientId();
            CLIENT_SECRET = settings.getApiSettings().getIgdbSecret();
        }

        OAUTH_TOKEN = getOAuthToken();

        String urlStr = API_URL;
        String requestBody = "fields name, id; search \"" + query + "\";";

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Client-ID", CLIENT_ID);
        conn.setRequestProperty("Authorization", "Bearer " + OAUTH_TOKEN);
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setDoOutput(true);
        conn.getOutputStream().write(requestBody.getBytes("UTF-8"));
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            String errorResponse = readErrorResponse(conn);
            return null; // Stop the method and return null
        }

        List<GameSearchResult> results = new ArrayList<>();
        Scanner scanner = new Scanner(conn.getInputStream());
        String responseBody = scanner.useDelimiter("\\A").next();
        scanner.close();

        JSONArray gamesArray = new JSONArray(responseBody);
        for (int i = 0; i < gamesArray.length(); i++) {
            JSONObject gameObj = gamesArray.getJSONObject(i);
            String name = gameObj.getString("name");
            int id = gameObj.getInt("id");
            GameSearchResult game = new GameSearchResult();
            game.setName(name);
            game.setId(id);
            game.setCoverImage(getSingleImage(game));
            results.add(game);
        }

       

        return results;
    }
    
    public List<CoverSearchResult> retriveCovers(GameSearchResult game) throws IOException {
        Settings settings = SettingsService.loadSettings();
        if (settings != null) {
            CLIENT_ID = settings.getApiSettings().getIgdbClientId();
            CLIENT_SECRET = settings.getApiSettings().getIgdbSecret();
        }
    
        OAUTH_TOKEN = getOAuthToken();
    
        String urlStr = API_URL;
        String requestBody = "fields cover.image_id, screenshots.image_id, artworks.image_id; where id = " + game.getId() + ";";
    
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Client-ID", CLIENT_ID);
        conn.setRequestProperty("Authorization", "Bearer " + OAUTH_TOKEN);
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setDoOutput(true);
        conn.getOutputStream().write(requestBody.getBytes("UTF-8"));
        conn.connect();
    
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            String errorResponse = readErrorResponse(conn);
            return null; // Stop the method and return null
        }
    
        Scanner scanner = new Scanner(conn.getInputStream());
        String responseBody = scanner.useDelimiter("\\A").next();
        scanner.close();
    
        JSONArray gamesArray = new JSONArray(responseBody);
        List<CoverSearchResult> results = new ArrayList<>();
    
        if (gamesArray.length() > 0) {
            JSONObject gameObj = gamesArray.getJSONObject(0);
    
            if (!gameObj.isNull("cover")) {
                JSONObject coverObj = gameObj.getJSONObject("cover");
                String coverImageId = coverObj.getString("image_id");
                String coverImageUrl = "https://images.igdb.com/igdb/image/upload/t_cover_big/" + coverImageId + ".jpg";
                BufferedImage coverImage = ImageIO.read(new URL(coverImageUrl));
                CoverSearchResult cover = new CoverSearchResult();
                cover.setImage(coverImage);
                cover.setId(coverImageId);
                cover.setName("Cover");
                results.add(cover);
            }
    
            if (!gameObj.isNull("screenshots")) {
                JSONArray screenshotsArray = gameObj.getJSONArray("screenshots");
                for (int i = 0; i < screenshotsArray.length(); i++) {
                    JSONObject screenshotObj = screenshotsArray.getJSONObject(i);
                    String screenshotImageId = screenshotObj.getString("image_id");
                    String screenshotImageUrl = "https://images.igdb.com/igdb/image/upload/t_screenshot_huge/" + screenshotImageId + ".jpg";
                    BufferedImage screenshotImage = ImageIO.read(new URL(screenshotImageUrl));
                    CoverSearchResult cover = new CoverSearchResult();
                    cover.setImage(screenshotImage);
                    cover.setId(screenshotImageId);
                    cover.setName("Screenshot");
                    results.add(cover);
                }
            }
    
            if (!gameObj.isNull("artworks")) {
                JSONArray artworksArray = gameObj.getJSONArray("artworks");
                for (int i = 0; i < artworksArray.length(); i++) {
                    JSONObject artworkObj = artworksArray.getJSONObject(i);
                    String artworkImageId = artworkObj.getString("image_id");
                    String artworkImageUrl = "https://images.igdb.com/igdb/image/upload/t_1080p/" + artworkImageId + ".jpg";
                    BufferedImage artworkImage = ImageIO.read(new URL(artworkImageUrl));
                    CoverSearchResult cover = new CoverSearchResult();
                    cover.setImage(artworkImage);
                    cover.setId(artworkImageId);
                    cover.setName("Artwork");
                    results.add(cover);
                }
            }
        }
    
        return results;
    }

    public BufferedImage getSingleImage(GameSearchResult game) throws IOException {
        Settings settings = SettingsService.loadSettings();
        if (settings != null) {
            CLIENT_ID = settings.getApiSettings().getIgdbClientId();
            CLIENT_SECRET = settings.getApiSettings().getIgdbSecret();
        }
    
        OAUTH_TOKEN = getOAuthToken();
    
        String urlStr = API_URL;
        String requestBody = "fields cover.image_id, screenshots.image_id, artworks.image_id; where id = " + game.getId() + ";";
    
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Client-ID", CLIENT_ID);
        conn.setRequestProperty("Authorization", "Bearer " + OAUTH_TOKEN);
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setDoOutput(true);
        conn.getOutputStream().write(requestBody.getBytes("UTF-8"));
        conn.connect();
    
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            String errorResponse = readErrorResponse(conn);
            return null; // Stop the method and return null
        }
    
        Scanner scanner = new Scanner(conn.getInputStream());
        String responseBody = scanner.useDelimiter("\\A").next();
        scanner.close();
    
        JSONArray gamesArray = new JSONArray(responseBody);
    
        if (gamesArray.length() > 0) {
            JSONObject gameObj = gamesArray.getJSONObject(0);
    
            if (!gameObj.isNull("cover")) {
                JSONObject coverObj = gameObj.getJSONObject("cover");
                String coverImageId = coverObj.getString("image_id");
                String coverImageUrl = "https://images.igdb.com/igdb/image/upload/t_cover_big/" + coverImageId + ".jpg";
                return ImageIO.read(new URL(coverImageUrl));
            }
    
            if (!gameObj.isNull("screenshots")) {
                JSONArray screenshotsArray = gameObj.getJSONArray("screenshots");
                if (screenshotsArray.length() > 0) {
                    JSONObject screenshotObj = screenshotsArray.getJSONObject(0);
                    String screenshotImageId = screenshotObj.getString("image_id");
                    String screenshotImageUrl = "https://images.igdb.com/igdb/image/upload/t_screenshot_huge/" + screenshotImageId + ".jpg";
                    return ImageIO.read(new URL(screenshotImageUrl));
                }
            }
    
            if (!gameObj.isNull("artworks")) {
                JSONArray artworksArray = gameObj.getJSONArray("artworks");
                if (artworksArray.length() > 0) {
                    JSONObject artworkObj = artworksArray.getJSONObject(0);
                    String artworkImageId = artworkObj.getString("image_id");
                    String artworkImageUrl = "https://images.igdb.com/igdb/image/upload/t_1080p/" + artworkImageId + ".jpg";
                    return ImageIO.read(new URL(artworkImageUrl));
                }
            }
        }
    
        return null; // Return null if no image is found
    }    

    private static String getOAuthToken() throws IOException {
        String requestBody = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&grant_type=client_credentials";
        URL url = new URL(OAUTH_URL + "?" + requestBody);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to get OAuth token: " + readErrorResponse(conn));
        }

        Scanner scanner = new Scanner(conn.getInputStream());
        String responseBody = scanner.useDelimiter("\\A").next();
        scanner.close();

        JSONObject responseObj = new JSONObject(responseBody);
        return responseObj.getString("access_token");
    }

    private static String readErrorResponse(HttpURLConnection conn) throws IOException {
        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(conn.getErrorStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
                response.append(System.lineSeparator());
            }
        }
        return response.toString();
    } 

}