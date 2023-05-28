package mine.block.spoticraft.client.mixin;

import dev.lambdaurora.spruceui.Position;
import mine.block.spoticraft.client.ui.SpotifyScreen;
import mine.block.spoticraft.client.ui.widget.SpotifyButtonWidget;
import mine.block.spotify.SpotifyUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        int l = this.height / 4 + 48;
        SpotifyButtonWidget widget = new SpotifyButtonWidget(Position.of(this.width / 2 + 129, l + 96), 20, 20, Text.empty(), (btn) -> client.setScreen(new SpotifyScreen(this)));
        this.addDrawableChild(widget);

        SpotifyUtils.MC_LOADED = true;
    }
}
