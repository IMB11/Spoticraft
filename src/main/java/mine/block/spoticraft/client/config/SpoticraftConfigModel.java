package mine.block.spoticraft.client.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import mine.block.spoticraft.client.SpoticraftClient;
import mine.block.spotify.SpotifyHandler;

@Modmenu(modId = SpoticraftClient.MODID)
@Config(name = SpoticraftClient.MODID + "/spoticraft", wrapperName = "SpoticraftConfig", defaultHook = true)
public class SpoticraftConfigModel {

    public boolean autoMuteIngameMusic = false;

    public boolean resetSpotifyCredentials = false;

    public static class Callbacks {

        public static void onResetCredentials(boolean newValue) {
            if(newValue) {
                SpoticraftClient.MOD_CONFIG.resetSpotifyCredentials(false);
                SpoticraftClient.MOD_CONFIG.save();
                SpotifyHandler.setup(true);
            }
        }

        public static void onToggleMuteMusic(boolean newValue) {
            // TODO toggle ingame music
        }

    }
}
