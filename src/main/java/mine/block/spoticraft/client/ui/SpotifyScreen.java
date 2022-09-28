package mine.block.spoticraft.client.ui;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SpotifyScreen extends Screen {
    protected SpotifyScreen() {
        super(Text.empty());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.width, this.height, 0xFF121212);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
