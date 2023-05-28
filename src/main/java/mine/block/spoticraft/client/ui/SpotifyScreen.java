package mine.block.spoticraft.client.ui;

import com.github.winterreisender.webviewko.WebviewKo;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import mine.block.spoticraft.client.ui.widget.SpotifyPlaylistItemWidget;
import mine.block.spoticraft.client.ui.widget.SpotifyTextButtonWidget;
import mine.block.spotify.SpotifyHandler;
import mine.block.spotify.SpotifyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.enums.ProductType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

public class SpotifyScreen extends SpruceScreen {
    private final boolean connected;
    private final Screen parent;
    public float percentageDone;
    public float progress;

    public SpotifyScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
        this.connected = SpotifyHandler.SPOTIFY_API != null;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        fill(matrices, 0, 0, this.width, this.height, 0xFF121212);
    }

    public void triggerManualPoll() {
        new SpotifyHandler.PollingThread().run();
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new SpotifyTextButtonWidget(Position.of(2, height - 22), 46, 20, Text.of("⟵ Back"), (btn) -> client.setScreen(parent)));
        this.addDrawableChild(new SpotifyPlaylistItemWidget(Position.center(width / 2, (height / 3))));

        var currentlyPlaying = SpotifyUtils.NOW_PLAYING;

        if(currentlyPlaying != null) {
            progress = ((float) 0.5 * currentlyPlaying.getProgress_ms()) / ((float) 0.5 * currentlyPlaying.getItem().getDurationMs());
        }

        boolean isPremium = false;

        try {
            var profile = SpotifyHandler.SPOTIFY_API.getCurrentUsersProfile().build().execute();
            if(profile.getProduct() == ProductType.PREMIUM) {
                isPremium = true;
            }
        } catch (IOException | SpotifyWebApiException | ParseException ignored) {}

        SpotifyTextButtonWidget infoWidget = new SpotifyTextButtonWidget(Position.of(2+46+2, height - 22), 20, 20, Text.of("ℹ"), (btn) -> {
            Util.getOperatingSystem().open("https://github.com/mineblock11/Spoticraft");
        });
        this.addDrawableChild(infoWidget);

        SpotifyTextButtonWidget previousWidget = new SpotifyTextButtonWidget(Position.of(2+46+2+20+2, height - 22), 20, 20, Text.of("←"), (btn) -> {
            btn.setActive(false);
            new Thread(() -> {try {
                SpotifyHandler.SPOTIFY_API.skipUsersPlaybackToPreviousTrack().build().execute();
                btn.setActive(true);
                triggerManualPoll();
           } catch (IOException | ParseException | SpotifyWebApiException ignored) {}}).start();
        });

        previousWidget.setActive(isPremium);

        SpotifyTextButtonWidget pausePlayWidget = new SpotifyTextButtonWidget(Position.of(2+46+2+20+2+20+2, height - 22), 20, 20, Text.of("⏯"), (btn) -> {
            btn.setActive(false);
            new Thread(() -> {try {
                var status = SpotifyHandler.SPOTIFY_API.getInformationAboutUsersCurrentPlayback().build().execute();
                if(status.getIs_playing()) SpotifyHandler.SPOTIFY_API.pauseUsersPlayback().build().execute();
                else SpotifyHandler.SPOTIFY_API.startResumeUsersPlayback().build().execute();
                btn.setActive(true);
                triggerManualPoll();
            } catch (IOException | ParseException | SpotifyWebApiException ignored) {}}).start();
        });

        pausePlayWidget.setActive(isPremium);

        SpotifyTextButtonWidget nextWidget = new SpotifyTextButtonWidget(Position.of(2+46+2+20+2+20+2+20+2, height - 22), 20, 20, Text.of("→"), (btn) -> {
            btn.setActive(false);
            new Thread(() -> {try {
                SpotifyHandler.SPOTIFY_API.skipUsersPlaybackToNextTrack().build().execute();
                btn.setActive(true);
                triggerManualPoll();
            } catch (IOException | ParseException | SpotifyWebApiException ignored) {}}).start();
        });

        nextWidget.setActive(isPremium);

        SpotifyTextButtonWidget shuffleWidget = new SpotifyTextButtonWidget(Position.of(width - 22, height - 22), 20, 20, Text.of("S"), (btn) -> {
            btn.setActive(false);
            new Thread(() -> {
                try {
                    var playbackStatus = SpotifyHandler.SPOTIFY_API.getInformationAboutUsersCurrentPlayback().build().execute();
                    boolean shuffleState = playbackStatus.getShuffle_state();
                    SpotifyHandler.SPOTIFY_API.toggleShuffleForUsersPlayback(!shuffleState).build().execute();
                    btn.setMessage(shuffleState ? Text.of("S") : Text.literal("S").formatted(Formatting.BLUE, Formatting.BOLD));
                    btn.setActive(true);
                    triggerManualPoll();
                } catch (IOException | ParseException | SpotifyWebApiException ignored) {}
            }).start();
        });

        shuffleWidget.setActive(isPremium);

        SpotifyTextButtonWidget loopWidget = new SpotifyTextButtonWidget(Position.of(width - 44, height - 22), 20, 20, Text.of("R"), (btn) -> {
            btn.setActive(false);
            new Thread(() -> {try {
                var playbackStatus = SpotifyHandler.SPOTIFY_API.getInformationAboutUsersCurrentPlayback().build().execute();
                var repeat_state = playbackStatus.getRepeat_state();
                switch (repeat_state) {
                    case "context" -> {
                        SpotifyHandler.SPOTIFY_API.setRepeatModeOnUsersPlayback("off").build().execute();
                        btn.setMessage(Text.literal("R"));
                    }
                    case "track" -> {
                        SpotifyHandler.SPOTIFY_API.setRepeatModeOnUsersPlayback("context").build().execute();
                        btn.setMessage(Text.literal("R").formatted(Formatting.BLUE, Formatting.BOLD));
                    }
                    case "off" -> {
                        SpotifyHandler.SPOTIFY_API.setRepeatModeOnUsersPlayback("track").build().execute();
                        btn.setMessage(Text.literal("R").formatted(Formatting.GREEN, Formatting.BOLD));
                    }
                }
                btn.setActive(true);
                triggerManualPoll();
            } catch (IOException | ParseException | SpotifyWebApiException ignored) {}}).start();
        });

        loopWidget.setActive(isPremium);

        this.addDrawableChild(shuffleWidget);
        this.addDrawableChild(loopWidget);
        this.addDrawableChild(previousWidget);
        this.addDrawableChild(nextWidget);
        this.addDrawableChild(pausePlayWidget);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ScissorManager.pushScaleFactor(this.scaleFactor);
        this.renderBackground(matrices);
        this.renderWidgets(matrices, mouseX, mouseY, delta);
        this.renderTitle(matrices, mouseX, mouseY, delta);
        Tooltip.renderAll(this, matrices);
        ScissorManager.popScaleFactor();

        if(!connected) {
            Text text = Text.literal("No Spotify Connection").formatted(Formatting.RED, Formatting.BOLD);
            int widthe = MinecraftClient.getInstance().textRenderer.getWidth(text);
            MinecraftClient.getInstance().textRenderer.draw(matrices, text, (width / 2F) - (widthe / 2f), (height / 2F) - (widthe / 2f), 0xFFFF0000);
            return;
        }

        renderProgressBar(matrices);

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, new Identifier("spoticraft", "textures/spotify-long.png"));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        DrawableHelper.drawTexture(matrices, this.width - 2 - 128/2, 4, 64, 18, 0, 0, 312, 92, 312, 92);
        RenderSystem.setShaderTexture(0, new Identifier("spoticraft", "textures/spotify.png"));
        DrawableHelper.drawTexture(matrices, this.width - 2 - 128/2 - 18, 4, 16, 16, 0, 0, 32, 32, 32, 32);

        RenderSystem.disableBlend();
    }

    private void renderProgressBar(MatrixStack matrices) {
        float playbackBarWidth = this.width - 46;
        fill(matrices, 2+46+2+20+2+20+2+20+2+20+2, height - 19, (int)playbackBarWidth, height - 4, 0xFF5E5E5E);
        this.percentageDone = MathHelper.clamp(this.percentageDone * 0.95F + this.progress * 0.050000012F, 0.0F, 1.0F);

        if(percentageDone <= 0) {
            return;
        }

        float filledWidth = 2+46+2+20+2+20+2+20+2+20+2 + MathHelper.ceil((float)((this.width - 46) - (2+46+2+20+2+20+2+20+2+20+2) - 2) * this.percentageDone);
        fill(matrices, (int) 2+46+2+20+2+20+2+20+2+20+2, height - 19, (int) filledWidth, height - 4, 0xFFFFFFFF);
    }
}
