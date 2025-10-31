package com.golfing8.profiler.metrics;

import com.golfing8.profiler.struct.ProfilerResults;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.LongList;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ProfileStatistics(long samples, double average, long max, long min, long sum, double average95, long max95,
                                long min95, long sum95, double stdDev, long[] data) implements ProfilerResults {

    public JsonObject getSummaryJson() {
        JsonObject object = new JsonObject();
        object.addProperty("samples", samples);
        object.addProperty("average", average);
        object.addProperty("max", max);
        object.addProperty("min", min);
        object.addProperty("sum", sum);
        object.addProperty("average95", average95);
        object.addProperty("max95", max95);
        object.addProperty("min95", min95);
        object.addProperty("sum95", sum95);
        object.addProperty("stddev", stdDev);
        return object;
    }

    public static ProfileStatistics construct(LongList samples) {
        long sum = 0L;
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;

        for (long l : samples) {
            sum += l;

            min = Math.min(min, l);
            max = Math.max(max, l);
        }

        double average = (double) sum / samples.size();
        BigDecimal stdDevSum = BigDecimal.ZERO;

        for (long l : samples) {
            stdDevSum = stdDevSum.add(BigDecimal.valueOf(Math.pow(l - average, 2)));
        }
        stdDevSum = stdDevSum.divide(BigDecimal.valueOf(samples.size()), 2, RoundingMode.HALF_UP);

        double stdDev = Math.sqrt(stdDevSum.doubleValue());
        double stdDev2 = stdDev * 2;

        long sum95 = 0;
        long min95 = Long.MAX_VALUE, max95 = Long.MIN_VALUE;
        for (long l : samples) {
            if (l < average - stdDev2 || l > average + stdDev2) {
                continue;
            }

            sum95 += l;
            min95 = Math.min(min95, l);
            max95 = Math.max(max95, l);
        }

        double average95 = (double) sum95 / samples.size();
        return new ProfileStatistics(samples.size(), average, max, min, sum, average95, max95, min95, sum95, stdDev, samples.toLongArray());
    }
}
