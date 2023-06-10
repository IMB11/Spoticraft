package mine.block.spoticraft.client.ui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSpruceWidget;
import mine.block.spotify.SpotifyUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class SpotifyPlaylistItemWidget extends AbstractSpruceWidget {
    public SpotifyPlaylistItemWidget(Position position) {
        super(position);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

        if(SpotifyUtils.NOW_PLAYING != null && SpotifyUtils.NOW_ART != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();

            int size = SpotifyUtils.NOW_ART.getWidth();
            context.drawTexture(SpotifyUtils.NOW_ID, this.getX(), this.getY(), 64, 64, 0, 0, size, size, size, size);

            RenderSystem.disableBlend();

            var textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawText(textRenderer, Text.literal(SpotifyUtils.NOW_PLAYING.getItem().getName()), this.getX()+64+16, this.getY()+32+4, -256, false);

            if(SpotifyUtils.NOW_PLAYING.getItem() instanceof Track track) {
                context.drawText(textRenderer, Text.literal(track.getArtists()[0].getName()), this.getX()+64+16, (int) (this.getY()+21+4), -1, false);
            } else {
                context.drawText(textRenderer, Text.literal(((Episode) SpotifyUtils.NOW_PLAYING.getItem()).getShow().getName()), this.getX()+64+16, this.getY()+21+4, -1, false);
            }
        }
    }
}
