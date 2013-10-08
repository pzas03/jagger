package com.griddynamics.jagger.engine.e1.collector;

public class SuccessRateAggregatorProvider implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new MetricAggregator() {

            private long passNum = 0;
            private long failNum = 0;

            @Override
            public void append(Integer calculated)
            {
                if (calculated != 0)
                    failNum++;
                else
                    passNum++;
            }

            @Override
            public Double getAggregated() {
                if ((failNum + passNum) == 0)
                    return new Double(0.0);
                else
                    return new Double(100.0 * (double) (passNum) / (double) (failNum + passNum));
            }

            @Override
            public void reset() {
            }

            @Override
            public String getName() {
                return "Success rate, %";
            }

        };
    }
}