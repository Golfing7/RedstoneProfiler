package com.golfing8.profiler.experiment;

import com.golfing8.profiler.struct.ProfilingWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@AllArgsConstructor @Getter
public enum ExperimentType {
    NULL((world) -> new Experiment("null", world, null)),
    FLOOD_1((world) -> new Experiment("flood_1", world, "flood_1.schem")),
    FLOOD_2((world) -> new Experiment("flood_2", world, "flood_2.schem")),
    FLOOD_3((world) -> new Experiment("flood_3", world, "flood_3.schem")),
    FLOOD_4((world) -> new Experiment("flood_4", world, "flood_4.schem")),
    FLOOD_5((world) -> new Experiment("flood_5", world, "flood_5.schem")),
    FLOOD_6((world) -> new Experiment("flood_6", world, "flood_6.schem")),
    FLOOD_7((world) -> new Experiment("flood_7", world, "flood_7.schem")),
    FLOOD_8((world) -> new Experiment("flood_8", world, "flood_8.schem")),
    FLOOD_9((world) -> new Experiment("flood_9", world, "flood_9.schem")),
    FLOOD_10((world) -> new Experiment("flood_10", world, "flood_10.schem")),
    FLOOD_11((world) -> new Experiment("flood_11", world, "flood_11.schem")),
    FLOOD_12((world) -> new Experiment("flood_12", world, "flood_12.schem")),
    FLOOD_13((world) -> new Experiment("flood_13", world, "flood_13.schem")),
    FLOOD_14((world) -> new Experiment("flood_14", world, "flood_14.schem")),
    FLOOD_15((world) -> new Experiment("flood_15", world, "flood_15.schem")),
    FLOOD_3D((world) -> new Experiment("flood_3d", world, "flood_3d.schem")),
    LINE((world) -> new Experiment("line", world, "line.schem")),
    ;

    final Function<ProfilingWorld, Experiment> experiment;
}
