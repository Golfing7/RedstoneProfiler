package com.golfing8.profiler.profiler;

import com.golfing8.profiler.RedstoneProfiler;
import com.golfing8.profiler.experiment.Experiment;
import com.golfing8.profiler.struct.ProfilerResults;
import com.golfing8.profiler.struct.ProfilingWorld;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Optional;

/**
 * An abstract type of profiler
 */
@Getter
public abstract class ProfilerAbstract<T extends ProfilerResults> implements Listener {
    private final ProfilingWorld world;
    private final Experiment experiment;

    public ProfilerAbstract(ProfilingWorld world, Experiment experiment) {
        this.world = world;
        this.experiment = experiment;
    }

    /**
     * Executes the experiment
     *
     * @return the execution result
     */
    public Optional<T> execute(int repetitions) {
        Bukkit.getPluginManager().registerEvents(this, RedstoneProfiler.getInstance());
        Optional<T> result = executeInternal(repetitions);
        HandlerList.unregisterAll(this);
        return result;
    }

    /**
     * Executes this profiler and returns the result.
     *
     * @param repetitions the amount of times to repeat the experiment
     * @return an optional result if successful
     */
    protected abstract Optional<T> executeInternal(int repetitions);
}
