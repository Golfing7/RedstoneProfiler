package com.golfing8.profiler.experiment;

import com.golfing8.profiler.struct.ProfilingWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@AllArgsConstructor @Getter
public enum ExperimentType {
    NULL((world) -> new Experiment("null", world, null)),
    FLOOD((world) -> new Experiment("flood", world, "flood.schem")),
    FLOOD_3D((world) -> new Experiment("flood_3d", world, "flood_3d.schem")),
    LINE((world) -> new Experiment("line", world, "line.schem")),
    ;

    final Function<ProfilingWorld, Experiment> experiment;
}
