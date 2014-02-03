package com.griddynamics.jagger.webclient.client.mvp;

/**
 * Class containing all the tokens of the application. Useful to reference them in one place and use them in uibinder
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class NameTokens {

    protected NameTokens() {
    }

    public static final String SUMMARY = "summary";

    public static final String TRENDS = "trends";

    public static final String METRICS = "metrics";

    public static final String CONTROL_SUMMARY_TRENDS = "Summary & Trends";
    public static final String CONTROL_METRICS = "Metrics";
    public static final String SESSION_INFO = "Session Info";
    public static final String TEST_INFO = "Test Info";
    public static final String SESSION_SCOPE_PLOTS = "Session Scope Plots";

    // used to understand if test from summary subtree or from metrics subtree
    public static final String SUMMARY_PREFIX = "sum-";
    public static final String METRICS_PREFIX = "met-";

    public static final String MONITORING_PREFIX = "mon-";

    /**
     * used to separate monitoring plot name and agent id in PolNameDto.plotName/SessionNameDto.plotName
     * note: '|' == '%7C' in while link processing
     */
    public static final String AGENT_NAME_SEPARATOR = "|";

    public static String EMPTY = "";

    public static String summary() {
        return SUMMARY;
    }

    public static String trends() {
        return TRENDS;
    }

    public static final String EXCEPTION_MESSAGE_NO_METRICDESCRIPTION_TABLE =
            "Model of data base do not match with data model of current version of jagger.\n" +
            "Please do next changes in you database: \n" +
            "1. Create MetricDescriptionEntity table: \n" +
            "    CREATE TABLE `MetricDescriptionEntity` (\n" +
            "       `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
            "       `displayName` varchar(255) DEFAULT NULL,\n" +
            "       `metricId` varchar(255) DEFAULT NULL,\n" +
            "       `taskData_id` bigint(20) DEFAULT NULL,\n" +
            "       PRIMARY KEY (`id`),\n" +
            "       KEY `FK58B9F0FF5EA8703` (`taskData_id`),\n" +
            "       CONSTRAINT `FK58B9F0FF5EA8703` FOREIGN KEY (`taskData_id`) REFERENCES `TaskData` (`id`)\n" +
            "    )\n" +
            "2. Create MetricPointEntity table: \n" +
            "    CREATE TABLE `MetricPointEntity` (\n" +
            "       `id` bigint(20) NOT NULL,\n" +
            "       `time` bigint(20) DEFAULT NULL,\n" +
            "       `value` double DEFAULT NULL,\n" +
        "           `metricDescription_id` bigint(20) DEFAULT NULL,\n" +
            "       PRIMARY KEY (`id`),\n" +
            "       KEY `FKB5BD3DA36A568CED` (`metricDescription_id`),\n" +
            "       CONSTRAINT `FKB5BD3DA36A568CED` FOREIGN KEY (`metricDescription_id`) REFERENCES `MetricDescriptionEntity` (`id`)\n" +
            "    )\n" +
            "3. Create MetricSummaryEntity table: \n" +
            "    CREATE TABLE `MetricSummaryEntity` (\n" +
            "       `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
            "       `total` double DEFAULT NULL,\n" +
            "       `metricDescription_id` bigint(20) DEFAULT NULL,\n" +
            "       PRIMARY KEY (`id`),\n" +
            "       KEY `FKCBD863396A568CED` (`metricDescription_id`),\n" +
            "       CONSTRAINT `FKCBD863396A568CED` FOREIGN KEY (`metricDescription_id`) REFERENCES `MetricDescriptionEntity` (`id`)\n" +
            "    )\n" +
            "4. Alter ValidationResultEntity table: \n" +
            "    ALTER TABLE ValidationResultEntity ADD displayName varchar(255) DEFAULT NULL AFTER validator";
}
