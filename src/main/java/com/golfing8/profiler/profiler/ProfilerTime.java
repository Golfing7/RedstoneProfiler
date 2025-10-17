package com.golfing8.profiler.profiler;

import com.golfing8.profiler.RedstoneProfiler;
import com.golfing8.profiler.experiment.Experiment;
import com.golfing8.profiler.metrics.IMethodProfiler;
import com.golfing8.profiler.struct.ProfilerResults;
import com.golfing8.profiler.struct.ProfilingWorld;
import com.golfing8.profiler.metrics.ProfileStatistics;

import java.util.Optional;
import java.util.logging.Level;

public class ProfilerTime extends ProfilerAbstract<ProfilerTime.Results> {
    public ProfilerTime(ProfilingWorld world, Experiment experiment) {
        super(world, experiment);
    }

    @Override
    public Optional<Results> executeInternal(int repetitions) {
        // Perform the setup step
        IMethodProfiler profiler = IMethodProfiler.getDefaultProfiler();
        profiler.resetData();

        try {
            String keyI = "ignition_" + getExperiment().getName();
            String keyE = "extinguish_" + getExperiment().getName();

            for (int i = 0; i < repetitions; i++) {
                getExperiment().setupExperiment();

                profiler.start(keyI);
                getExperiment().ignite();
                profiler.stop(keyI);

                profiler.start(keyE);
                getExperiment().extinguish();
                profiler.stop(keyE);
            }

            return Optional.of(new Results(profiler.getStatistics(keyI), profiler.getStatistics(keyE)));
        } catch (Exception exc) {
            RedstoneProfiler.getInstance().getLogger().log(Level.SEVERE, "Failed to execute experiment", exc);
            return Optional.empty();
        }
    }

    public record Results(ProfileStatistics ignition, ProfileStatistics extinguish) implements ProfilerResults {}
}
