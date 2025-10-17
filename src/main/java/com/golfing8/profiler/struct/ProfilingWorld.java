package com.golfing8.profiler.struct;

import io.papermc.paper.configuration.WorldConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.TriState;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

/**
 * A wrapper for the world that profiling will take place in.
 */
@Getter
public class ProfilingWorld {
    private final String worldName;
    /** The world in which we are profiling */
    private World world;

    public ProfilingWorld(String worldName) {
        this.worldName = worldName;
        resetWorld();
    }

    /**
     * Resets and re-creates the profiling world.
     */
    public void resetWorld() {
        // Forcefully unload the world.
        Bukkit.unloadWorld(worldName, false);
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        if (Files.exists(worldFolder.toPath())) {
            try {
                FileUtils.deleteDirectory(worldFolder);
            } catch (IOException ignored) {}
        }

        this.world = Bukkit.createWorld(buildCreator(worldName));
        if (this.world == null)
            throw new NullPointerException("Created world is null");

        this.world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
    }

    /**
     * Sets the redstone implementation of this world
     *
     * @param implementation the implementation
     */
    public void setImplementation(@NotNull WorldConfiguration.Misc.RedstoneImplementation implementation) {
        ((ServerLevel) world).getLevel().paperConfig().misc.redstoneImplementation = implementation;
    }

    private static WorldCreator buildCreator(String worldName) {
        ChunkGenerator generator = new ChunkGenerator() {};
        WorldCreator creator = new WorldCreator(worldName);
        creator.generator(generator);
        creator.keepSpawnLoaded(TriState.TRUE);
        creator.generateStructures(false);
        creator.bonusChest(false);
        return creator;
    }
}
