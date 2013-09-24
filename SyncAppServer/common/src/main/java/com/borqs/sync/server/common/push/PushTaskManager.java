package com.borqs.sync.server.common.push;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.LogHelper;
import org.quartz.*;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author b211 push sync task manager.schedule the sync job
 */
public class PushTaskManager {
    private static String JOB_GROUP_NAME = "default_job_group";
    private static String TRIGGER_GROUP_NAME = "default_trigger_group";

    public static final String JOB_DATA_KEY_PARAMETER_MAP = "PARAMETER_MAP";
    
    private static PushTaskManager mInstance;
    private Scheduler mScheduler;
    private Logger mLogger;

    private PushTaskManager(Scheduler s, Logger logger) {
        mScheduler = s;
        mLogger = logger;
    }

    public static synchronized PushTaskManager getInstance(Scheduler s, Logger logger) {
        if (mInstance == null) {
            mInstance = new PushTaskManager(s, logger);
        }
        return mInstance;
    }

    private boolean hasJob(String jobName) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
        Trigger trigger = mScheduler.getTrigger(triggerKey);

        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
        JobDetail jobDetail = mScheduler.getJobDetail(jobKey);

        return trigger != null && jobDetail != null;
    }

    /**
     * @param jobName the name of job
     * @param delay the time to start the job
     * @throws SchedulerException
     */
    private void rescheduleJob(String jobName, long delay) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
        Trigger trigger = mScheduler.getTrigger(triggerKey);

        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
        JobDetail jobDetail = mScheduler.getJobDetail(jobKey);

        LogHelper.logD(mLogger,"==========exist job,reschedule it :" + jobName);
        // pause job
        mScheduler.pauseJob(jobKey);
        // pause trigger
        mScheduler.pauseTrigger(triggerKey);
        // remove the trigger
        mScheduler.unscheduleJob(triggerKey);
        // update the trigger time
        trigger = trigger.getTriggerBuilder().startAt(new Date(System.currentTimeMillis() + delay))
                .build();

        mScheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     *
     * @param job
     * @param jobName
     * @param delay
     * @param jobMap
     * @throws SchedulerException
     * @throws java.text.ParseException
     * @throws InterruptedException
     */
    private void scheduleNewJob(Job job,String jobName,long delay,Map<Object,Object> jobMap)
            throws SchedulerException, ParseException, InterruptedException {
        // new job and trigger
        LogHelper.logD(mLogger,"==========new job :" + jobName);
        JobDetail jobDetail = newJob(job.getClass()).withIdentity(jobName, JOB_GROUP_NAME).build();
        jobDetail.getJobDataMap().put(JOB_DATA_KEY_PARAMETER_MAP, jobMap);

        // new trigger
        Trigger trigger = newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME)
                .startAt(new Date(System.currentTimeMillis() + delay)).build();
        mScheduler.scheduleJob(jobDetail, trigger);
        // start
        mScheduler.start();
    }

    /**
     * schedule a job for jobName by job class
     * @param job
     * @param jobName
     * @param delay
     * @param jobMap
     * @throws SchedulerException
     * @throws java.text.ParseException
     * @throws InterruptedException
     */
    public void scheduleJob(Job job,String jobName,long delay,Map<Object,Object> jobMap) throws SchedulerException, ParseException, InterruptedException {
        if(hasJob(jobName)){
            rescheduleJob(jobName,delay);
        }else{
            scheduleNewJob(job,jobName,delay,jobMap);
        }

    }

}
