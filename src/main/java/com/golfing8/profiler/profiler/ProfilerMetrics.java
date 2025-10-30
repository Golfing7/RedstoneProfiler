package com.golfing8.profiler.profiler;

import com.golfing8.profiler.RedstoneProfiler;
import com.golfing8.profiler.experiment.Experiment;
import com.golfing8.profiler.metrics.ProfileStatistics;
import com.golfing8.profiler.struct.ProfilerResults;
import com.golfing8.profiler.struct.ProfilingWorld;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Optional;
import java.util.logging.Level;

public class ProfilerMetrics extends ProfilerAbstract<ProfilerMetrics.Results> {
    private int physics;
    private int redstone;

    public ProfilerMetrics(ProfilingWorld world, Experiment experiment) {
        super(world, experiment);
    }

    @Override
    protected Optional<Results> executeInternal(int repetitions) {
        LongList ignitionPhysics = new LongArrayList();
        LongList ignitionRedstone = new LongArrayList();
        LongList extinguishPhysics = new LongArrayList();
        LongList extinguishRedstone = new LongArrayList();
        for (int i = 0; i < repetitions; i++) {
            try {
                getExperiment().setupExperiment();

                physics = redstone = 0;
                getExperiment().ignite();
                ignitionPhysics.add(physics);
                ignitionRedstone.add(redstone);

                physics = redstone = 0;
                getExperiment().extinguish();
                extinguishPhysics.add(physics);
                extinguishRedstone.add(redstone);

                physics = redstone = 0;
            } catch (Exception exc) {
                RedstoneProfiler.getInstance().getLogger().log(Level.SEVERE, "Failed to execute metrics profiler", exc);
                return Optional.empty();
            }
        }

        return Optional.of(new Results(
                new Results.Tracker(
                        ProfileStatistics.construct(ignitionPhysics),
                        ProfileStatistics.construct(ignitionRedstone)
                ),
                new Results.Tracker(
                        ProfileStatistics.construct(extinguishPhysics),
                        ProfileStatistics.construct(extinguishRedstone)
                )
        ));
    }

    @EventHandler
    public void onBP(BlockPhysicsEvent event) {
        physics++;
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event) {
        redstone++;
    }

    public record Results(Tracker powerOn, Tracker powerOff) implements ProfilerResults {
        public record Tracker(ProfileStatistics physics, ProfileStatistics redstone) {}
    }
}
