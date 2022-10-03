package mine.block.spoticraft.client.mixin;

import mine.block.spoticraft.client.SpoticraftClient;
import mine.block.spotify.SpotifyUtils;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundInstance;getCategory()Lnet/minecraft/sound/SoundCategory;"), cancellable = true)
    public void spoticraft$cancelMusicIfSpotifyPlaying(SoundInstance sound, CallbackInfo ci) {
        if (sound.getCategory() == SoundCategory.MUSIC && SpotifyUtils.NOW_PLAYING != null && SpotifyUtils.NOW_PLAYING.getIs_playing()) {
            ci.cancel();
        }
    }
}
