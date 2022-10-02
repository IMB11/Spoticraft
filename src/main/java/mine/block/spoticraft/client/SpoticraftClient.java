package mine.block.spoticraft.client;

import mine.block.spoticraft.client.ui.SpotifyScreen;
import mine.block.spoticraft.client.ui.SpotifyToast;
import mine.block.spotify.SpotifyHandler;
import mine.block.utils.LiveWriteProperties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.*;

@Environment(EnvType.CLIENT)
public class SpoticraftClient implements ClientModInitializer {
    
    public static final LiveWriteProperties CONFIG = new LiveWriteProperties();
    public static final Logger LOGGER = LoggerFactory.getLogger("Spoticraft");
    public static final String VERSION = "1.1.0";
    public static boolean MC_LOADED = false;
    public static CurrentlyPlaying NOW_PLAYING = null;
    public static NativeImage NOW_ART = null;
    public static Identifier NOW_ID = null;
    public static HashMap<Identifier, NativeImage> TEXTURE = new HashMap<>();

    public static void run(CurrentlyPlaying currentlyPlaying) {
        if(!MC_LOADED) return;
        if (NOW_PLAYING == null || !NOW_PLAYING.getItem().getId().equals(currentlyPlaying.getItem().getId())) {
            NOW_PLAYING = currentlyPlaying;

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
        }


        if (MinecraftClient.getInstance().currentScreen instanceof SpotifyScreen spotifyScreen) {
            spotifyScreen.progress = (float) currentlyPlaying.getProgress_ms() / (float) currentlyPlaying.getItem().getDurationMs();
        }
    }

    @Override
    public void onInitializeClient() {
        SpotifyHandler.setup();

        SpotifyHandler.PollingThread thread = new SpotifyHandler.PollingThread();
        ExecutorService checkTasksExecutorService = new ThreadPoolExecutor(1, 10,
                100000, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>());
        checkTasksExecutorService.execute(thread);
        SpotifyHandler.songChangeEvent.add(SpoticraftClient::run);

        var key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spotify.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.spotify.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (key.wasPressed()) {
                client.setScreen(new SpotifyScreen(client.currentScreen));
            }
        });
    }
}
