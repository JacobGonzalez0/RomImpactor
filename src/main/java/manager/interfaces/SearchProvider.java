package manager.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import manager.models.CoverSearchResult;
import manager.models.GameSearchResult;

public interface SearchProvider {
    public abstract List<GameSearchResult> searchGames(String query) throws IOException;
    
    public abstract List<CoverSearchResult> retriveCovers(int gameId) throws IOException;
    
    public abstract BufferedImage getSingleImage(GameSearchResult game) throws IOException;
}
