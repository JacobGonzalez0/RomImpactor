package manager.services.searchproviders;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.json.JSONArray;
import org.json.JSONObject;

import manager.interfaces.SearchProvider;
import manager.models.CoverSearchResult;
import manager.models.GameSearchResult;
import manager.models.Settings;
import manager.services.SettingsService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SGDBUtil implements SearchProvider {
    private String API_KEY;
    private int API_LIMIT = 10;
    private final String API_URL = "https://www.steamgriddb.com/api/v2/search/autocomplete/";
    private final String IMAGE_URL = "https://www.steamgriddb.com/api/v2/grids/game/";
    
    public List<GameSearchResult> searchGames(String query) throws IOException {
        Settings settings = SettingsService.loadSettings();
        if (settings != null) {
            API_KEY = settings.getApiSettings().getSteamGridDbKey();
        }
    
        query = URLEncoder.encode(query, "UTF-8");
        String urlStr = API_URL + query;
    
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(urlStr)
            .addHeader("Authorization", "Bearer " + API_KEY)
            .build();
    
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
    
            String responseBody = response.body().string();
            JSONObject responseObj = new JSONObject(responseBody);
            JSONArray gamesArray = responseObj.getJSONArray("data");
    
            List<GameSearchResult> results = new ArrayList<>();
            for (int i = 0; i < gamesArray.length(); i++) {
                if(API_LIMIT > i){
                    continue;
                }
                JSONObject gameObj = gamesArray.getJSONObject(i);
                String name = gameObj.getString("name");
                int id = gameObj.getInt("id");
                GameSearchResult game = new GameSearchResult();
                game.setId(id);
                game.setName(name);
                game.setCoverImage(getSingleImage(game));
                
                results.add(game);
            }
    
            return results;
        }
    }
    
    
    private String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append(System.lineSeparator());
            }
        }
        return response.toString();
    }
    
    public List<CoverSearchResult> retriveCovers(GameSearchResult game) throws IOException {
        Settings settings = SettingsService.loadSettings();
        if (settings != null) {
            API_KEY = settings.getApiSettings().getSteamGridDbKey();
        }
    
        String urlStr = IMAGE_URL + game.getId();
    
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(urlStr)
            .addHeader("Authorization", "Bearer " + API_KEY)
            .build();
    
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null; // Stop the method and return null
            }
            
            String responseBody = response.body().string();
            JSONObject responseObj = new JSONObject(responseBody);
            JSONArray dataArr = responseObj.getJSONArray("data");
    
            List<CoverSearchResult> results = new ArrayList<>();
            for (int i = 0; i < dataArr.length(); i++) {
                if(API_LIMIT > i){
                    continue;
                }
                JSONObject dataObj = dataArr.getJSONObject(i);
                String imageUrl = dataObj.getString("url");
    
                Request imageRequest = new Request.Builder().url(imageUrl).build();
                try (Response imageResponse = client.newCall(imageRequest).execute()) {
                    if (imageResponse.isSuccessful()) {
                        try (InputStream inputStream = imageResponse.body().byteStream()) {
                            BufferedImage img = ImageIO.read(inputStream);
                            CoverSearchResult cover = new CoverSearchResult();
                            cover.setImage(img);
                            cover.setName("Cover");
                            cover.setId("");
                            results.add(cover);
                        }
                    } else {
                        System.out.println("Error loading image: " + imageUrl);
                    }
                }
            }
    
            return results;
        }
    }

    @Override
    public BufferedImage getSingleImage(GameSearchResult game) throws IOException {
        Settings settings = SettingsService.loadSettings();
        if (settings != null) {
            API_KEY = settings.getApiSettings().getSteamGridDbKey();
        }
        String urlStr = IMAGE_URL + game.getId();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(urlStr)
            .addHeader("Authorization", "Bearer " + API_KEY)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null; // Stop the method and return null
            }

            String responseBody = response.body().string();
            JSONObject responseObj = new JSONObject(responseBody);
            JSONArray dataArr = responseObj.getJSONArray("data");
            if (dataArr.length() == 0) {
                return null;
            }

            JSONObject dataObj = dataArr.getJSONObject(0);
            String imageUrl = dataObj.getString("url");

            Request imageRequest = new Request.Builder().url(imageUrl).build();
            try (Response imageResponse = client.newCall(imageRequest).execute()) {
                if (imageResponse.isSuccessful()) {
                    try (InputStream inputStream = imageResponse.body().byteStream()) {
                        BufferedImage img = ImageIO.read(inputStream);
                        return img;
                    }
                } else {
                    return null;
                }
            }
        }
    }


   
}
