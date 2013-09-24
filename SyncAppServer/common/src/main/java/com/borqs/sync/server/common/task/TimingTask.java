package com.borqs.sync.server.common.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/21/12
 * Time: 3:34 PM
 * a timing task
 */
public abstract class TimingTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        executeJob(jobExecutionContext);
    }

    public abstract void executeJob(JobExecutionContext jobExecutionContext);

}
