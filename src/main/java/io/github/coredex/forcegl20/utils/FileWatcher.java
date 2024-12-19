package io.github.coredex.forcegl20.utils;

import java.io.IOException;
import java.nio.file.*;

public class FileWatcher implements Runnable {
    private final Path configFilePath;
    private final Runnable onChange;

    public FileWatcher(Path configFilePath, Runnable onChange) {
        this.configFilePath = configFilePath;
        this.onChange = onChange;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path parentDir = configFilePath.getParent();
            if (parentDir == null) return;

            parentDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = (Path) event.context();
                    if (changed != null && changed.endsWith(configFilePath.getFileName())) {
                        onChange.run();
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
