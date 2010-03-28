package org.allmon.client.agent.aop.advice;

public class SpringAdvice {

    public void logBeforeMethodCall() {
        // TODO: Code to collect Metrics
        System.out.println("Before Method Call");
    }

    public void logAfterMethodCall() {
        // TODO: Code to collect Metrics
        System.out.println("After Method Call");
    }
}
