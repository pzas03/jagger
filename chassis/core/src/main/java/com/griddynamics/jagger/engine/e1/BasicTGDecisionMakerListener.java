package com.griddynamics.jagger.engine.e1;

import com.griddynamics.jagger.engine.e1.collector.limits.DecisionPerTest;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerInfo;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.WorstCaseDecisionMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

//todo ??? JFG-744 docu for decision making with use of limits
public class BasicTGDecisionMakerListener extends ServicesAware implements Provider<TestGroupDecisionMakerListener> {
    private static final Logger log = LoggerFactory.getLogger(BasicTGDecisionMakerListener.class);

    @Override
    protected void init() {

    }

    @Override
    public TestGroupDecisionMakerListener provide() {
        return new TestGroupDecisionMakerListener() {
            @Override
            public Decision onDecisionMaking(TestGroupDecisionMakerInfo decisionMakerInfo) {
                Decision decisionPerTestGroup;
                WorstCaseDecisionMaker worstCaseDecisionMaker = new WorstCaseDecisionMaker();
                List<Decision> decisions = new ArrayList<Decision>();

                for (DecisionPerTest decisionPerTest : decisionMakerInfo.getDecisionsPerTest()) {
                    decisions.add(decisionPerTest.getDecisionPerTest());
                }

                decisionPerTestGroup = worstCaseDecisionMaker.getDecision(decisions);

                log.debug("\nDecision for test group {} - {}",decisionMakerInfo.getTestGroup().getTaskName(),decisionPerTestGroup);

                return decisionPerTestGroup;
            }
        };
    }

}

/* **************** Decision maker page ************************* */
/// @defgroup Main_Decision_Maker_General_group Decision Maker main page
///
///


//???
/// @li General information about interfaces: @ref Main_Aggregators_Base_group
/// @li Available implementations: @ref Main_Aggregators_group
/// @li How to customize: @ref Main_HowToCustomizeAggregators_group
/// @li @ref Section_aggregators_time_intervals
/// @n
/// @n
/// @details
/// @par General info
/// Aggregators are processing raw data to get final measurement results that will be saved to database. @n
/// Aggregators are executed after all measurements are finished. Main goal for them is to reduce number of data values available after measurement @n
/// Simplest example: after measurement there are 1000 points, but you want to save only 200 points to database @n
/// So you are applying averaging aggregator that takes average value for every 5 points from raw data and saves it to DB as single value. @n
///
/// @par Example of aggregators setup in XML:
/// @dontinclude  tasks-new.conf.xml
/// @skip  begin: following section is used for docu generation - standard aggregator usage
/// @until end: following section is used for docu generation - standard aggregator usage
///
/// @par Aggregators XML elements
/// @xlink_complex{metricAggregatorAbstract} - what aggregators can be used in XML elements. See <b> 'Sub Types' </b> section of man page @n
/// How aggregators mentioned above are implemented you can see in section: @ref Main_Aggregators_group @n
/// @n
///
/// @section Section_aggregators_time_intervals Aggregation interval
/// Aggregation interval defines how many values will be saved to database and displayed in plots. @n
/// In property file you can decide what parameter you will set.@n
/// You can set either number of points on the plot or directly aggregation interval. @n
/// @dontinclude  environment.properties
/// @skip  begin: following section is used for docu generation - Aggregation interval
/// @until end: following section is used for docu generation - Aggregation interval
/// @n
/// In the picture below you can see comparison of both settings:@n
/// for session 26 - point count was set @n
/// for session 29 - interval @n
/// @image html jagger_point_count_vs_time_interval.png "Aggregation interval setup"


/* **************** How to customize aggregators ************************* */
/// @defgroup Main_HowToCustomizeAggregators_group Custom aggregators
///
/// @details
/// @ref Main_Aggregators_General_group
/// @n
/// @n
/// 1. Create class which implements @ref MetricAggregatorProvider @n
/// This class should provide instance of your aggregator - class that implements interface @ref MetricAggregator<C extends Number> @n
/// @dontinclude  MaxMetricAggregatorProvider.java
/// @skip  begin: following section is used for docu generation - custom aggregator source
/// @until end: following section is used for docu generation - custom aggregator source
/// @n
///
/// 2. Create bean of this class in some configuration file. Put some id for it.
/// @dontinclude  calculatorsAndAggregators.conf.xml
/// @skip  begin: following section is used for docu generation - custom aggregator
/// @until end: following section is used for docu generation - custom aggregator
/// @n
///
/// 3. Add metric aggregator of type @xlink_complex{metric-aggregator-ref} to you @xlink{metric-custom} block.@n
/// @dontinclude  tasks-new.conf.xml
/// @skip  begin: following section is used for docu generation - custom aggregator usage
/// @until end: following section is used for docu generation - custom aggregator usage

