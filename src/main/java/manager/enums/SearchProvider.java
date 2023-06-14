package manager.enums;

public enum SearchProvider {
    CAP("Cover Art Project",
     "The Cover Art Project is a collective initiative to archive and share album cover art. It provides a platform for exploration of various eras of music through their cover art, serving as an open resource for music enthusiasts and researchers alike."),
    SGDB("SteamGridDB",
     "SteamGridDB is a community-driven platform that collects and shares visual assets related to games on Steam and GOG. It hosts a variety of assets including box art, banners, and high-resolution landscape art, contributed by users and sourced from across the internet."),
    IGDB("Internet Game Database",
     "The Internet Game Database (IGDB) is a comprehensive resource for video game information. With hundreds of thousands of entries, it provides detailed information about video games, the companies that produce them, and their cast and crew. IGDB aims to foster a community where users can rate, list, and review games.");

    private final String name;
    private final String description;

    SearchProvider(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}

