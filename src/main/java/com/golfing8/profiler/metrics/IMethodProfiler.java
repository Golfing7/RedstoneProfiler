package com.golfing8.profiler.metrics;


import org.jetbrains.annotations.Nullable;

public interface IMethodProfiler {
    /**
     * Dumps the info to console
     */
    void dump();

    /**
     * Resets all profiler data
     */
    void resetData();

    /**
     * Starts a profiler on a string key
     * @param key The key to start a profile on
     */
    void start(String key);

    /**
     * Stops a profiler on a string key
     * @param key The key to stop a profiler
     */
    void stop(String key);

    /**
     * Gets the statistics on the given key
     *
     * @param key the key
     * @return the statistics
     */
    @Nullable ProfileStatistics getStatistics(String key);

    public static IMethodProfiler getDefaultProfiler()
    {
        return HighLowAverageProfiler.INSTANCE;
    }
}
