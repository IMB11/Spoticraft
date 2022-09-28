package mine.block.spotify.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import mine.block.spotify.SpotifyUtils;

import java.io.IOException;
import java.io.OutputStream;

public class SetupHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, 0);
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(SpotifyUtils.loadHTMLFile("setup").readAllBytes());
        responseBody.close();
    }
}
