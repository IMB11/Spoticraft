package mine.block.spoticraft.client.ui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SpotifyButtonWidget extends SpruceIconButtonWidget {
    public SpotifyButtonWidget(Position position, int width, int height, Text message, PressAction action) {
        super(position, width, height, message, action);
    }

    @Override
    protected int renderIcon(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(this.getX()+1, this.getY()+1, this.getX()+this.width - 1, this.getY()+this.height - 1, 0xFF000000);
        RenderSystem.enableBlend();
        context.drawTexture(new Identifier("spoticraft", "textures/spotify.png"), this.getX()+2, this.getY()+2, 16, 16, 0F, 0F, 32, 32, 32, 32);
        RenderSystem.disableBlend();
        return super.renderIcon(context, mouseX, mouseY, delta);
    }
}
