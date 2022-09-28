package mine.block.spoticraft.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Episode;
import se.michaelthelin.spotify.model_objects.specification.Track;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.Currency;
import java.util.Objects;

public class SpotifyToast implements Toast {
    private long startTime;
    private boolean justUpdated;
    public final CurrentlyPlaying currentlyPlaying;
    private final NativeImage image;

    public SpotifyToast(CurrentlyPlaying currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;

        if(currentlyPlaying.getItem() instanceof Track track) {
            Identifier texID = new Identifier("spotify", track.getId().toLowerCase());

            try {
                image = NativeImage.read(new URL(track.getAlbum().getImages()[0].getUrl()).openStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MinecraftClient.getInstance().getTextureManager().registerTexture(texID, new NativeImageBackedTexture(image));
        } else if(currentlyPlaying.getItem() instanceof Episode episode) {
            Identifier texID = new Identifier("spotify", episode.getId().toLowerCase());

            try {
                image = NativeImage.read(new URL(episode.getImages()[0].getUrl()).openStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MinecraftClient.getInstance().getTextureManager().registerTexture(texID, new NativeImageBackedTexture(image));
        } else {
            throw new RuntimeException("Invalid");
        }
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        // 2,2 -> 35,35 image;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        Identifier texID = new Identifier("spotify", currentlyPlaying.getItem().getId().toLowerCase());

        DrawableHelper.fill(matrices, 0, 0, this.getWidth(), this.getHeight(), 0xFF191414);

        RenderSystem.setShaderTexture(0, texID);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();

        DrawableHelper.drawTexture(matrices, 2, 2, this.getHeight() - 4, this.getHeight() - 4, 0, 0, image.getWidth(), image.getHeight(), image.getWidth(), image.getHeight());

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
