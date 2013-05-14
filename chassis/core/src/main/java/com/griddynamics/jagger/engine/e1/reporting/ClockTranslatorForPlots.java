package com.griddynamics.jagger.engine.e1.reporting;

/**
 * Created by IntelliJ IDEA.
 * User: amikryukov
 * Date: 5/14/13
 */
public enum ClockTranslatorForPlots {

    INVOCATIONS("invocation", "Invocations"), THREAD_COUNT("user", "Thread Count"), TPS("tps", "TPS");

    private final String content;
    private final String result;
    ClockTranslatorForPlots(String content, String result){
        this.content = content;
        this.result = result;
    }

    public String getContent() {
        return content;
    }

    public String getResult() {
        return result;
    }
}
