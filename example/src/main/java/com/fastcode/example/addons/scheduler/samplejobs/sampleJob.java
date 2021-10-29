package com.fastcode.example.addons.scheduler.samplejobs;

import org.quartz.*;

public class sampleJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return;
        }
    }
}
