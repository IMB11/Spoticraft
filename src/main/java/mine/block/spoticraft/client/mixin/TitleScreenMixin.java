package mine.block.spoticraft.client.mixin;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import mine.block.spoticraft.client.ui.SpotifyScreen;
import mine.block.spoticraft.client.ui.widget.SpotifyButtonWidget;
import mine.block.spotify.SpotifyHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    private static boolean ALREADY_DONE = false;

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        if(!ALREADY_DONE) {
            SpotifyHandler.PollingThread thread = new SpotifyHandler.PollingThread();
            ExecutorService checkTasksExecutorService = new ThreadPoolExecutor(1, 10,
                    100000, TimeUnit.MILLISECONDS,
                    new SynchronousQueue<>());
            checkTasksExecutorService.execute(thread);
            ALREADY_DONE = true;
        }
        SpotifyButtonWidget widget = new SpotifyButtonWidget(Position.of(this.width / 2 - 124 - 25, (this.height / 4 + 48) + 72 + 12), 20, 20, Text.empty(), (btn) -> client.setScreen(new SpotifyScreen(this)));
        this.addDrawableChild(widget);
    }
}
