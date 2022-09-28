package mine.block.spoticraft.client;

import mine.block.spoticraft.client.ui.SpotifyScreen;
import mine.block.spoticraft.client.ui.SpotifyToast;
import mine.block.spotify.SpotifyHandler;
import mine.block.utils.LiveWriteProperties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.*;

@Environment(EnvType.CLIENT)
public class SpoticraftClient implements ClientModInitializer {
    
    public static final LiveWriteProperties CONFIG = new LiveWriteProperties();
    public static final Logger LOGGER = LoggerFactory.getLogger("Spoticraft");
    public static CurrentlyPlaying NOW_PLAYING = null;
    public static NativeImage NOW_ART = null;
    public static Identifier NOW_ID = null;
    private static HashMap<Identifier, NativeImage> TEXTURE = new HashMap<>();

    @Override
    public void onInitializeClient() {
        SpotifyHandler.setup();

        SpotifyHandler.songChangeEvent = (CurrentlyPlaying currentlyPlaying) -> {
            NOW_PLAYING = currentlyPlaying;

            var item = currentlyPlaying.getItem();

            Identifier texture = new Identifier("spotify", currentlyPlaying.getItem().getId().toLowerCase());

            if(!TEXTURE.containsKey(texture)) {
                if(item instanceof Track track) {
                    try {
                        NOW_ART = NativeImage.read(new URL(track.getAlbum().getImages()[0].getUrl()).openStream());
                    } catch (IOException e) {
                        return;
                    }
                } else {
                    try {
                        NOW_ART = NativeImage.read(new URL(((Episode) currentlyPlaying.getItem()).getImages()[0].getUrl()).openStream());
                    } catch (IOException e) {
                        return;
                    }
                }

                TEXTURE.put(texture, NOW_ART);
            } else {
                NOW_ART = TEXTURE.get(texture);
            }

            NOW_ID = texture;

            MinecraftClient.getInstance().getTextureManager().registerTexture(NOW_ID, new NativeImageBackedTexture(NOW_ART));

            if(MinecraftClient.getInstance().inGameHud != null && !(MinecraftClient.getInstance().currentScreen instanceof SpotifyScreen)) {
                MinecraftClient.getInstance().getToastManager().add(new SpotifyToast(currentlyPlaying));
            }
        };
    }
}
