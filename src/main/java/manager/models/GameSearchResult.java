package manager.models;

import java.awt.image.BufferedImage;
import java.util.List;

public class GameSearchResult {
    private String name;
    private int id;
    private BufferedImage coverImage;
    private List<CoverSearchResult> coverSearchResults;

    public GameSearchResult() {
    }

    public GameSearchResult(String name, int id, BufferedImage coverImage, List<CoverSearchResult> coverSearchResults) {
        this.name = name;
        this.id = id;
        this.coverImage = coverImage;
        this.coverSearchResults = coverSearchResults;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BufferedImage getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(BufferedImage coverImage) {
        this.coverImage = coverImage;
    }

    public List<CoverSearchResult> getCoverSearchResults() {
        return coverSearchResults;
    }

    public void setCoverSearchResults(List<CoverSearchResult> coverSearchResults) {
        this.coverSearchResults = coverSearchResults;
    }

}

       
