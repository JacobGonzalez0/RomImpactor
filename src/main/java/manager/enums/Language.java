package manager.enums;

public enum Language {
    ENGLISH("en", "English"),
    SPANISH("es", "Spanish");

    private final String code;
    private final String name;

    Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

