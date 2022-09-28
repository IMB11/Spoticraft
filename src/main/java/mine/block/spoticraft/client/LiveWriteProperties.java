package mine.block.spoticraft.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Properties;

public class LiveWriteProperties extends Properties {

    private final Path pathToConfig = FabricLoader.getInstance().getConfigDir().resolve("spotify.cred");
    public boolean empty = true;

    public LiveWriteProperties() {
        if(Files.exists(pathToConfig)) {
            try {
                this.load(Files.newInputStream(pathToConfig));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(this.getProperty("client-secret") == null) {
                System.out.println("Old configuration file! Removing.");
                try {
                    Files.delete(pathToConfig);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.clear();
            } else {
                empty = false;
            }
        }
    }

    public void markDirty() throws IOException {
        var os = Files.newOutputStream(pathToConfig);
        this.store(os, null);
        os.close();
    }

    @Override
    public synchronized Object setProperty(String key, String value) {
        super.setProperty(key, value);
        try {
            markDirty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return value;
    }
}
