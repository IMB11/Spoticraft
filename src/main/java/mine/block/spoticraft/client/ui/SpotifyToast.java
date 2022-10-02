package mine.block.spoticraft.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Track;

import static mine.block.spotify.SpotifyUtils.NOW_ART;
import static mine.block.spotify.SpotifyUtils.NOW_ID;

public class SpotifyToast implements Toast {
    private long startTime;
    private boolean justUpdated;
    public final CurrentlyPlaying currentlyPlaying;
    public SpotifyToast(CurrentlyPlaying currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        // 2,2 -> 35,35 image;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        DrawableHelper.fill(matrices, 0, 0, this.getWidth(), this.getHeight(), 0xFF191414);

        RenderSystem.setShaderTexture(0, NOW_ID);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();

        DrawableHelper.drawTexture(matrices, 2, 2, this.getHeight() - 4, this.getHeight() - 4, 0, 0, NOW_ART.getWidth(), NOW_ART.getHeight(), NOW_ART.getWidth(), NOW_ART.getHeight());

        RenderSystem.setShaderTexture(0, new Identifier("spoticraft", "textures/spotify.png"));

        DrawableHelper.drawTexture(matrices, this.getWidth() - (16+8), (this.getHeight() / 2)-8, 16, 16, 0, 0, 32, 32, 32, 32);

        RenderSystem.disableBlend();

        manager.getClient().textRenderer.draw(matrices, Text.literal(currentlyPlaying.getItem().getName()), 43F, 10F, -256);

        if(currentlyPlaying.getItem() instanceof Track track) {
            manager.getClient().textRenderer.draw(matrices, Text.literal(track.getArtists()[0].getName()), 43F, 21F, -1);
        } else {
            manager.getClient().textRenderer.draw(matrices, Text.literal(((Episode) currentlyPlaying.getItem()).getShow().getName()), 43F, 21F, -1);
        }

        return startTime - this.startTime < 5000L ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public Object getType() {
        return Toast.super.getType();
    }

    @Override
    public int getWidth() {
        int widthName = (int) (MinecraftClient.getInstance().textRenderer.getWidth(currentlyPlaying.getItem().getName()) * (0.75));
        int widthArtist = 0;

        if(currentlyPlaying.getItem() instanceof Track track) {
            widthArtist = (int) (MinecraftClient.getInstance().textRenderer.getWidth(Text.literal(track.getArtists()[0].getName())) * (0.75));
        } else {
            widthArtist = (int) (MinecraftClient.getInstance().textRenderer.getWidth(Text.literal(((Episode) currentlyPlaying.getItem()).getShow().getName())) * (0.75));
        }

        return 160 + Math.max(widthArtist, widthName);
    }

    @Override
    public int getHeight() {
        return 38;
    }
}
