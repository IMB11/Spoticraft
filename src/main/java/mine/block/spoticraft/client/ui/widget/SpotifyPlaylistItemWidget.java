package mine.block.spoticraft.client.ui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSpruceWidget;
import mine.block.spotify.SpotifyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class SpotifyPlaylistItemWidget extends AbstractSpruceWidget {
    public SpotifyPlaylistItemWidget(Position position) {
        super(position);
    }

    @Override
    protected void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(SpotifyUtils.NOW_PLAYING != null && SpotifyUtils.NOW_ART != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, SpotifyUtils.NOW_ID);

            int size = SpotifyUtils.NOW_ART.getWidth();
            DrawableHelper.drawTexture(matrices, this.getX(), this.getY(), 64, 64, 0, 0, size, size, size, size);

            RenderSystem.disableBlend();

            var textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(matrices, Text.literal(SpotifyUtils.NOW_PLAYING.getItem().getName()), this.getX()+64+16F, this.getY()+32+4F, -256);

            if(SpotifyUtils.NOW_PLAYING.getItem() instanceof Track track) {
                textRenderer.draw(matrices, Text.literal(track.getArtists()[0].getName()), this.getX()+64+16F, this.getY()+21+4F, -1);
            } else {
                textRenderer.draw(matrices, Text.literal(((Episode) SpotifyUtils.NOW_PLAYING.getItem()).getShow().getName()), this.getX()+64+16F, this.getY()+21+4F, -1);
            }
        }
    }
}
