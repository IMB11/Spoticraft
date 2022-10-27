package mine.block.utils;

import mine.block.spoticraft.client.SpoticraftClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class LiveWriteProperties extends Properties {

    private final Path pathToConfig = FabricLoader.getInstance().getConfigDir().resolve(SpoticraftClient.MODID).resolve("spotify.cred");
    public boolean empty = true;

    public LiveWriteProperties() {
        if(Files.exists(pathToConfig)) {
            try (var stream = Files.newInputStream(pathToConfig)) {
                this.load(stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(this.getProperty("client-secret") == null || !Objects.equals(this.getProperty("version"), SpoticraftClient.VERSION)) {
                System.out.println("Old configuration file! Removing.");
                try {
                    Files.delete(pathToConfig);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.clear();
                this.setProperty("version", SpoticraftClient.VERSION);
            } else {
                empty = false;
            }
        }
    }

    public void markDirty() throws IOException {
        try(var os = Files.newOutputStream(pathToConfig)) {
            this.store(os, null);
        }
    }
}
