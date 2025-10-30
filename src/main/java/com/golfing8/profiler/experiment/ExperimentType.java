package com.golfing8.profiler.experiment;

import com.golfing8.profiler.struct.ProfilingWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor @Getter
public enum ExperimentType {
    FLOOD((world) -> new Experiment("flood", world, "flood.schem")),
    ;

    final Function<ProfilingWorld, Experiment> experiment;
}
