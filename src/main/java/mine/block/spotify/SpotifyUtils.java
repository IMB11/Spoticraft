package mine.block.spotify;

import mine.block.spoticraft.client.SpoticraftClient;
import mine.block.spoticraft.client.ui.SpotifyScreen;
import mine.block.spoticraft.client.ui.SpotifyToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SpotifyUtils {

    public static boolean MC_LOADED = false;
    public static CurrentlyPlaying NOW_PLAYING = null;
    public static NativeImage NOW_ART = null;
    public static Identifier NOW_ID = null;
    public static HashMap<Identifier, NativeImage> TEXTURE = new HashMap<>();

    public static InputStream loadHTMLFile(String id) throws IOException {
        var path = "/assets/spoticraft/web/" + id + ".html";
        var result = SpotifyUtils.class.getResourceAsStream(path);
        if (result == null) {
            throw new IOException("Could not find resource: " + path);
        }
        return result;
    }

    public static boolean netIsAvailable() {
        try {
            return InetAddress.getByName("www.google.com").isReachable(8000);
        } catch (IOException e) {
            return false;
        }
    }

    public static Map<String, String> queryToMap(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public static void run(CurrentlyPlaying currentlyPlaying) {
        if(!MC_LOADED) return;
        if (NOW_PLAYING == null || !NOW_PLAYING.getItem().getId().equals(currentlyPlaying.getItem().getId())) {
            NOW_PLAYING = currentlyPlaying;
            MinecraftClient.getInstance().submit(() -> {
                if(SpoticraftClient.MOD_CONFIG.autoMuteIngameMusic() && SpotifyUtils.NOW_PLAYING != null && SpotifyUtils.NOW_PLAYING.getIs_playing()) {
                    MinecraftClient.getInstance().getSoundManager().stopSounds(null, SoundCategory.MUSIC);
                }
            });

            var item = currentlyPlaying.getItem();

            Identifier texture = new Identifier("spotify", currentlyPlaying.getItem().getId().toLowerCase());

            if (!TEXTURE.containsKey(texture)) {
                if (item instanceof Track track) {
                    try {
                        NOW_ART = NativeImage.read(new URL(track.getAlbum().getImages()[0].getUrl()).openStream());
                        TEXTURE.put(texture, NOW_ART);
                        NOW_ID = texture;

                        MinecraftClient.getInstance().getTextureManager().registerTexture(NOW_ID, new NativeImageBackedTexture(NOW_ART));
                    } catch (IOException e) {
                        return;
                    }
                } else {
                    try {
                        NOW_ART = NativeImage.read(new URL(((Episode) currentlyPlaying.getItem()).getImages()[0].getUrl()).openStream());
                        TEXTURE.put(texture, NOW_ART);
                        NOW_ID = texture;
                        MinecraftClient.getInstance().getTextureManager().registerTexture(NOW_ID, new NativeImageBackedTexture(NOW_ART));
                    } catch (IOException e) {
                        return;
                    }
                }
            } else {
                NOW_ART = TEXTURE.get(texture);
                NOW_ID = texture;
            }


            if (MinecraftClient.getInstance().inGameHud != null && !(MinecraftClient.getInstance().currentScreen instanceof SpotifyScreen) && NOW_ART != null) {
                MinecraftClient.getInstance().getToastManager().add(new SpotifyToast(currentlyPlaying));
            }
        } else if (NOW_PLAYING.getIs_playing() != currentlyPlaying.getIs_playing()) {
            NOW_PLAYING = currentlyPlaying;
        }


        if (MinecraftClient.getInstance().currentScreen instanceof SpotifyScreen spotifyScreen) {
            spotifyScreen.progress = (float) currentlyPlaying.getProgress_ms() / (float) currentlyPlaying.getItem().getDurationMs();
        }
    }
}
