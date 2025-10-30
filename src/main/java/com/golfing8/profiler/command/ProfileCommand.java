package com.golfing8.profiler.command;

import com.golfing8.profiler.RedstoneProfiler;
import com.golfing8.profiler.experiment.Experiment;
import com.golfing8.profiler.experiment.ExperimentType;
import com.golfing8.profiler.profiler.ProfilerMetrics;
import com.golfing8.profiler.profiler.ProfilerTime;
import io.papermc.paper.configuration.WorldConfiguration;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

/**
 * A command to run the profiling system
 */
@AllArgsConstructor
public class ProfileCommand implements CommandExecutor {
    private static final int EXPERIMENT_WARMUP = 5;
    private static final int EXPERIMENT_REPETITIONS = 100;
    private static final List<WorldConfiguration.Misc.RedstoneImplementation> REDSTONE_IMPLEMENTATIONS = List.of(
            WorldConfiguration.Misc.RedstoneImplementation.VANILLA,
            WorldConfiguration.Misc.RedstoneImplementation.EIGENCRAFT,
            WorldConfiguration.Misc.RedstoneImplementation.ALTERNATE_CURRENT
    );
    private final RedstoneProfiler plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Experiment experiment = ExperimentType.FLOOD.getExperiment().apply(plugin.getWorld());
        ProfilerTime profilerTime = new ProfilerTime(plugin.getWorld(), experiment);
        ProfilerMetrics profilerMetrics = new ProfilerMetrics(plugin.getWorld(), experiment);

        // Run the profiler to warm up first
        for (var implementation : REDSTONE_IMPLEMENTATIONS) {
            plugin.getWorld().setImplementation(implementation);
            sender.sendMessage("Running profiler warmup for " + EXPERIMENT_WARMUP + " iterations on " + implementation);
            profilerTime.execute(EXPERIMENT_WARMUP);
            profilerMetrics.execute(EXPERIMENT_REPETITIONS);

            sender.sendMessage("Running profiler for " + EXPERIMENT_REPETITIONS + " iterations on " + implementation);
            var timeData = profilerTime.execute(EXPERIMENT_REPETITIONS);
            if (timeData.isEmpty()) {
                sender.sendMessage("Experiment failed. No data produced.");
                return true;
            }
            var metricsData = profilerMetrics.execute(EXPERIMENT_REPETITIONS);
            if (metricsData.isEmpty()) {
                sender.sendMessage("Experiment failed. No data produced.");
                return true;
            }

            try {
                sender.sendMessage("Writing results for " + implementation);
                writeResults(timeData.get(), metricsData.get(), implementation);
            } catch (IOException exc) {
                sender.sendMessage("Failed to write results. See console.");
                plugin.getLogger().log(Level.SEVERE, "Failed to write results for implementation " + implementation, exc);
            }
        }
        sender.sendMessage("Completed all profiling");
        return true;
    }

    private void writeResults(ProfilerTime.Results time, ProfilerMetrics.Results metrics, WorldConfiguration.Misc.RedstoneImplementation implementation) throws IOException {
        String fileName = implementation.name() + ".csv";
        Path path = plugin.getDataPath().resolve("data").resolve(fileName);
        Files.createDirectories(path.getParent());
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (FileWriter fileWriter = new FileWriter(path.toFile())) {
            int samples = time.powerOn().data().length;
            fileWriter.write("PowerOnNanos,PowerOnPhysics,PowerOnRedstone,PowerOffNanos,PowerOffPhysics,PowerOffRedstone\n");
            for (int i = 0; i < samples; i++) {
                fileWriter.write(time.powerOn().data()[i] + "," +
                        metrics.powerOn().physics().data()[i] + "," +
                        metrics.powerOn().redstone().data()[i] + "," +
                        time.powerOff().data()[i] + "," +
                        metrics.powerOff().physics().data()[i] + "," +
                        metrics.powerOff().redstone().data()[i] + "\n");
            }
            fileWriter.flush();
        }
    }
}
