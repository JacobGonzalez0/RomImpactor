package manager.services.searchproviders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import manager.interfaces.SearchProvider;
import manager.models.CoverSearchResult;
import manager.models.GameSearchResult;
import manager.services.SSLService;

public class CoverArtProjectUtil implements SearchProvider{

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public List<GameSearchResult> searchGames(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        String url = "https://www.thecoverproject.net/view.php?searchstring=" + encodedQuery;
        List<GameSearchResult> results = new ArrayList<>();

        Document doc = SSLService.getConnection(url).userAgent(USER_AGENT).get();
        Elements tableRows = doc.select("table tr");

        for (Element row : tableRows) {
            Element headerCell = row.selectFirst("td.newsHeader");
            if (headerCell != null && headerCell.text().contains("Search Results")) {
                Elements gameLinks = row.select("a[href^=\"view.php?game_id=\"]");
                for (Element link : gameLinks) {
                    GameSearchResult item = new GameSearchResult();

                    // The link text is the name of the game
                    item.setName(link.text());

                    // We get the link id itself to get the game id
                    String linkHref = link.attr("href");
                    int gameId = Integer.parseInt(linkHref.substring(linkHref.lastIndexOf('=') + 1));
                    item.setId(gameId);

                    BufferedImage image = getSingleImage(item);
                    item.setCoverImage(image);
                    results.add(item);
                }
                break; 
            }
        }

        return results;
    }

    @Override
    public BufferedImage getSingleImage(GameSearchResult game) throws IOException{

        String url = "https://www.thecoverproject.net/view.php?game_id=" + game.getId();
        Document doc = SSLService.getConnection(url).userAgent(USER_AGENT).get();

        Elements links = doc.select("a[href*=view.php?cover_id=]");

        Pattern p = Pattern.compile("cover_id=(\\d+)");

        for (Element link : links) {
            Matcher m = p.matcher(link.attr("href"));
            if (m.find()) {
                int coverId = Integer.parseInt(m.group(1));
                return downloadImage(coverId);
            }
        }
        return null;
    }

    public BufferedImage downloadImage(int gameId) throws IOException {
        String imageUrl = "https://www.thecoverproject.net/download_cover.php?src=cdn&cover_id=" + gameId;
        Connection.Response response = SSLService.getConnection(imageUrl).ignoreContentType(true).execute();
        
        InputStream inputStream = response.bodyStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    
        byte[] imageBytes = output.toByteArray();
        ByteArrayInputStream input = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(input);
    
        inputStream.close();
        return image;
    }
    

    @Override
    public List<CoverSearchResult> retriveCovers(GameSearchResult game) throws IOException {
        List<CoverSearchResult> results = new ArrayList<>();

        String url = "https://www.thecoverproject.net/view.php?game_id=" + game.getId();
        Document doc = SSLService.getConnection(url).userAgent(USER_AGENT).get();

        Element div = doc.getElementById("covers");

        if (div != null) {
            // Get the list
            Element ul = div.selectFirst("ul");

            if (ul != null) {
                //get all the items, skip the first one since its the title
                Elements lis = ul.select("li");

                for(int i = 1; i < lis.size(); i++){
                    Element link = lis.get(i).selectFirst("a[href*=view.php?cover_id=]");
                    Pattern p = Pattern.compile("cover_id=(\\d+)");
                    Matcher m = p.matcher(link.attr("href"));
                
                    if (m.find()) {
                        int coverId = Integer.parseInt(m.group(1));
                        String coverName = link.text();
                
                        CoverSearchResult item = new CoverSearchResult();
                        item.setId(coverId);
                        item.setImage(downloadImage(coverId));
                        item.setName(coverName);
                
                        results.add(item);
                    }
                }
                
               
            }
        }

        return results;
    }
    


}
