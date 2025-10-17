package com.golfing8.profiler.metrics;

import com.golfing8.profiler.struct.ProfilerResults;
import it.unimi.dsi.fastutil.longs.LongList;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ProfileStatistics(long samples, long average, long max, long min, long sum, long average95, long max95,
                                long min95, long sum95, double stdDev) implements ProfilerResults {

    public static ProfileStatistics construct(LongList statistics) {
        long sum = 0L;
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;

        for (long l : statistics) {
            sum += l;

            min = Math.min(min, l);
            max = Math.max(max, l);
        }

        long average = sum / statistics.size();
        BigDecimal stdDevSum = BigDecimal.ZERO;

        for (long l : statistics) {
            stdDevSum = stdDevSum.add(BigDecimal.valueOf(Math.pow(l - average, 2)));
        }
        stdDevSum = stdDevSum.divide(BigDecimal.valueOf(statistics.size()), 2, RoundingMode.HALF_UP);

        double stdDev = Math.sqrt(stdDevSum.doubleValue());
        double stdDev2 = stdDev * 2;

        long sum95 = 0;
        long min95 = Long.MAX_VALUE, max95 = Long.MIN_VALUE;
        for (long l : statistics) {
            if (l < average - stdDev2 || l > average + stdDev2) {
                continue;
            }

            sum95 += l;
            min95 = Math.min(min95, l);
            max95 = Math.max(max95, l);
        }

        long average95 = sum95 / statistics.size();
        return new ProfileStatistics(statistics.size(), average, max, min, sum, average95, max95, min95, sum95, stdDev);
    }
}
