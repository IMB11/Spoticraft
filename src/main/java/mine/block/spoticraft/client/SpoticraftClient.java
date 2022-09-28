package mine.block.spoticraft.client;

import mine.block.spoticraft.client.ui.SpotifyToast;
import mine.block.spotify.SpotifyHandler;
import mine.block.utils.LiveWriteProperties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;

@Environment(EnvType.CLIENT)
public class SpoticraftClient implements ClientModInitializer {
    
    public static final LiveWriteProperties CONFIG = new LiveWriteProperties();
    public static final Logger LOGGER = LoggerFactory.getLogger("Spoticraft");

    @Override
    public void onInitializeClient() {
        SpotifyHandler.setup();
        SpotifyHandler.songChangeEvent = (CurrentlyPlaying currentlyPlaying) -> {
            if(MinecraftClient.getInstance().inGameHud != null) {
                MinecraftClient.getInstance().getToastManager().add(new SpotifyToast(currentlyPlaying));
            }
        };
    }
}
