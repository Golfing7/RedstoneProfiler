package com.golfing8.profiler.command;

import com.golfing8.profiler.RedstoneProfiler;
import com.golfing8.profiler.experiment.Experiment;
import com.golfing8.profiler.experiment.ExperimentType;
import com.golfing8.profiler.profiler.ProfilerMetrics;
import com.golfing8.profiler.profiler.ProfilerTime;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import io.papermc.paper.configuration.WorldConfiguration;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
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
        ExperimentType experimentType;
        if (args.length > 0) {
            try {
                experimentType = ExperimentType.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException exc) {
                sender.sendMessage("Unrecognized experiment type " + args[0]);
                return true;
            }
        } else {
            experimentType = ExperimentType.FLOOD;
        }

        // Load the experiments
        Experiment experiment = experimentType.getExperiment().apply(plugin.getWorld());
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
                writeResults(experiment, timeData.get(), metricsData.get(), implementation);
            } catch (IOException exc) {
                sender.sendMessage("Failed to write results. See console.");
                plugin.getLogger().log(Level.SEVERE, "Failed to write results for implementation " + implementation, exc);
            }
        }
        sender.sendMessage("Completed all profiling");
        return true;
    }

    private void writeResults(Experiment experiment, ProfilerTime.Results time, ProfilerMetrics.Results metrics, WorldConfiguration.Misc.RedstoneImplementation implementation) throws IOException {
        Path dataDirectory = plugin.getDataPath().resolve("data").resolve(experiment.getName()).resolve(implementation.name());
        Path dataPath = dataDirectory.resolve("data.csv");
        Files.createDirectories(dataPath.getParent());
        Files.deleteIfExists(dataPath);
        Files.createFile(dataPath);
        try (BufferedWriter writer = Files.newBufferedWriter(dataPath)) {
            int samples = time.powerOn().data().length;
            writer.write("PowerOnNanos,PowerOnPhysics,PowerOnRedstone,PowerOffNanos,PowerOffPhysics,PowerOffRedstone\n");
            for (int i = 0; i < samples; i++) {
                writer.write(time.powerOn().data()[i] + "," +
                        metrics.powerOn().physics().data()[i] + "," +
                        metrics.powerOn().redstone().data()[i] + "," +
                        time.powerOff().data()[i] + "," +
                        metrics.powerOff().physics().data()[i] + "," +
                        metrics.powerOff().redstone().data()[i] + "\n");
            }
        }

        Path summaryPath = dataDirectory.resolve("summary.json");
        JsonObject wrapper = new JsonObject();
        wrapper.add("power_on", time.powerOn().getSummaryJson());
        wrapper.add("power_off", time.powerOff().getSummaryJson());
        try (BufferedWriter writer = Files.newBufferedWriter(summaryPath)) {
            writer.write(RedstoneProfiler.getGson().toJson(wrapper));
        }
    }
}
