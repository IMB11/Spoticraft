package mine.block.spoticraft.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import mine.block.spoticraft.client.SpoticraftClient;
import mine.block.spoticraft.client.ui.widget.SpotifyButtonWidget;
import mine.block.spoticraft.client.ui.widget.SpotifyPlaylistItemWidget;
import mine.block.spoticraft.client.ui.widget.SpotifyTextButtonWidget;
import mine.block.spotify.SpotifyHandler;
import mine.block.spotify.SpotifyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.enums.ProductType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;

public class SpotifyScreen extends SpruceScreen {
    private final boolean connected;
    private final Screen parent;

    public SpotifyScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
        this.connected = SpotifyHandler.SPOTIFY_API != null;
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        fill(matrices, 0, 0, this.width, this.height, 0xFF121212);
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new SpotifyTextButtonWidget(Position.of(2, height - 22), 46, 20, Text.of("⟵ Back"), (btn) -> client.setScreen(parent)));
        this.addDrawableChild(new SpotifyPlaylistItemWidget(Position.center(width / 2, height - (height - 25))));

        boolean isPremium = false;

        try {
            var profile = SpotifyHandler.SPOTIFY_API.getCurrentUsersProfile().build().execute();
            if(profile.getProduct() == ProductType.PREMIUM) {
                isPremium = true;
            }
        } catch (IOException | SpotifyWebApiException | ParseException ignored) {}
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if(!connected) {
            Text text = Text.literal("No Spotify Connection").formatted(Formatting.RED, Formatting.BOLD);
            int widthe = MinecraftClient.getInstance().textRenderer.getWidth(text);
            MinecraftClient.getInstance().textRenderer.draw(matrices, text, (width / 2F) - (widthe / 2f), (height / 2F) - (widthe / 2f), 0xFFFF0000);
            return;
        }
    }
}
