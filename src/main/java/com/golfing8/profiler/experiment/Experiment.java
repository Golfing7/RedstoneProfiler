package com.golfing8.profiler.experiment;

import com.golfing8.profiler.struct.ProfilingWorld;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a single type of experiment.
 */
@RequiredArgsConstructor @SuppressWarnings("UnstableApiUsage")
public class Experiment {
    private static final BlockPosition PASTE_LOCATION = Position.block(0, 0, 0);

    /** The name of this experiment */
    @Getter
    private final String name;
    /** The world in which this experiment takes place */
    private final ProfilingWorld world;
    /** The path to the schematic */
    private final Path schematicPath;
    /** The position to 'ignite' the experiment */
    private final BlockPosition ignitionPosition;
    /** A cached clipboard for the schematic */
    private @Nullable Clipboard clipboard;
    /** If the experiment has been pasted */
    private boolean pasted;

    /**
     * Ignites the redstone of this experiment
     */
    public void ignite() {
        if (!pasted)
            throw new IllegalStateException("Cannot execute experiment that has not been pasted");

        // Turns on whatever redstone contraption has been prepared with a redstone block
        world.getWorld().getBlockAt(ignitionPosition.toLocation(world.getWorld())).setType(Material.REDSTONE_BLOCK);
    }

    /**
     * Extinguishes the redstone of this experiment
     */
    public void extinguish() {
        if (!pasted)
            throw new IllegalStateException("Cannot execute experiment that has not been pasted");

        // Removes the power source from the prepared contraption
        world.getWorld().getBlockAt(ignitionPosition.toLocation(world.getWorld())).setType(Material.AIR);
    }

    /**
     * Sets up the experiment by pasting the schematic for it
     */
    public void setupExperiment() {
        tryPasteSchematic();
        this.pasted = true;
    }

    /**
     * Attempts to paste the schematic
     */
    private void tryPasteSchematic() {
        if (clipboard == null) {
            ClipboardFormat format = ClipboardFormats.findByFile(schematicPath.toFile());
            if (format == null) {
                throw new RuntimeException("Failed to find clipboard format for path " + schematicPath);
            }

            try (ClipboardReader reader = format.getReader(Files.newInputStream(schematicPath))) {
                this.clipboard = reader.read();
            } catch (IOException exc) {
                throw new RuntimeException("Failed to read clipboard file " + schematicPath, exc);
            }
        }

        try (EditSession session = WorldEdit.getInstance().newEditSession(new BukkitWorld(world.getWorld()))) {
            Operation operation =  new ClipboardHolder(Objects.requireNonNull(this.clipboard))
                    .createPaste(session)
                    .to(BlockVector3.at(PASTE_LOCATION.x(), PASTE_LOCATION.y(), PASTE_LOCATION.z()))
                    .ignoreAirBlocks(false)
                    .build();

            Operations.complete(operation);
        } catch (WorldEditException exc) {
            throw new RuntimeException("Failed to paste clipboard file " + schematicPath, exc);
        }
    }
}
