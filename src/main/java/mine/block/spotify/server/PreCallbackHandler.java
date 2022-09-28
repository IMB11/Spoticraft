package mine.block.spotify.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import mine.block.spoticraft.client.SpoticraftClient;
import mine.block.spotify.SpotifyHandler;
import mine.block.spotify.SpotifyUtils;
import se.michaelthelin.spotify.SpotifyApi;

import java.io.IOException;
import java.net.URI;

import static mine.block.spoticraft.client.SpoticraftClient.LOGGER;

public class PreCallbackHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        LOGGER.info(requestMethod + " " + exchange.getRequestURI());
        if (requestMethod.equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(200, 0);

            var queries = SpotifyUtils.queryToMap(exchange.getRequestURI().getQuery());
            var clientID = queries.get("clientid");
            var clientSecret = queries.get("clientsecret");

            SpotifyHandler.SPOTIFY_API = SpotifyApi.builder()
                    .setClientId(clientID)
                    .setClientSecret(clientSecret)
                    .setRedirectUri(URI.create("http://localhost:23435/callback"))
                    .build();

            SpoticraftClient.CONFIG.put("client-id", clientID);
            SpoticraftClient.CONFIG.put("client-secret", clientSecret);

            exchange.close();
        }
    }
}
