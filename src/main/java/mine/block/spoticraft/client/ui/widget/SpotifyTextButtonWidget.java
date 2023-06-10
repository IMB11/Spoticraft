package mine.block.spoticraft.client.ui.widget;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SpotifyTextButtonWidget extends SpruceButtonWidget {
    public SpotifyTextButtonWidget(Position position, int width, int height, Text message, PressAction action) {
        super(position, width, height, message, action);
    }

    @Override
    protected void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.fill(this.getX()+1, this.getY()+1, this.getX()+this.width - 1, this.getY()+this.height - 1, 0xFF000000);
    }
}
