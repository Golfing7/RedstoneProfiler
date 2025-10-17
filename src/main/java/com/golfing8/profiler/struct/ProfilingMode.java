package com.golfing8.profiler.struct;

/**
 * A mode that a profiler will run in.
 */
public enum ProfilingMode {
    /**
     * Measures total execution time of the experiment
     */
    RUNTIME,
    /**
     * Measures quantifiable metrics about an experiment such as:
     * <ol>
     *     <li>Block Updates</li>
     *     <li>Physics Updates</li>
     * </ol>
     */
    METRICS,
}
