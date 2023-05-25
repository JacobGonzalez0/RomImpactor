package manager.models;

public class ApiSettings {
    private boolean steamGridDb;
    private String steamGridDbKey;
    private boolean igdb;
    private String igdbClientId;
    private String igdbSecret;
    public ApiSettings(boolean steamGridDb, String steamGridDbKey, boolean igdb, String igdbClientId,
            String igdbSecret) {
        this.steamGridDb = steamGridDb;
        this.steamGridDbKey = steamGridDbKey;
        this.igdb = igdb;
        this.igdbClientId = igdbClientId;
        this.igdbSecret = igdbSecret;
    }
    public ApiSettings() {
    }
    
    public boolean isSteamGridDb() {
        return steamGridDb;
    }
    public void setSteamGridDb(boolean steamGridDb) {
        this.steamGridDb = steamGridDb;
    }
    public String getSteamGridDbKey() {
        return steamGridDbKey;
    }
    public void setSteamGridDbKey(String steamGridDbKey) {
        this.steamGridDbKey = steamGridDbKey;
    }
    public boolean isIgdb() {
        return igdb;
    }
    public void setIgdb(boolean igdb) {
        this.igdb = igdb;
    }
    public String getIgdbClientId() {
        return igdbClientId;
    }
    public void setIgdbClientId(String igdbClientId) {
        this.igdbClientId = igdbClientId;
    }
    public String getIgdbSecret() {
        return igdbSecret;
    }
    public void setIgdbSecret(String igdbSecret) {
        this.igdbSecret = igdbSecret;
    }

    
}