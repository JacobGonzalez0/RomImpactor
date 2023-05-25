package manager.models;

import java.io.Serializable;

public class Settings implements Serializable {
    private General general;
    private ApiSettings apiSettings;

    public General getGeneral() {
        return general;
    }

    public void setGeneral(General general) {
        this.general = general;
    }

    public ApiSettings getApiSettings() {
        return apiSettings;
    }

    public void setApiSettings(ApiSettings apiSettings) {
        this.apiSettings = apiSettings;
    }
}