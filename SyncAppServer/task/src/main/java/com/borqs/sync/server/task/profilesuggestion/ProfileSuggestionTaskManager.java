package com.borqs.sync.server.task.profilesuggestion;

import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.runtime.ContextHolder;
import com.borqs.sync.server.common.task.ITaskListener;
import com.borqs.sync.server.common.task.ITaskManager;
import com.borqs.sync.server.common.util.LogHelper;
import com.borqs.sync.server.task.util.TaskLogger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/21/12
 * Time: 10:57 AM
 * created for scheduled timing TaskManager
 */
public class ProfileSuggestionTaskManager extends ContextHolder implements ITaskManager{
    
    private static final String TASK_RULE_KEY = "task_rule";
    private static final String TASK_RULE_DETAIL_KEY = "task_rule_detail";
    private static final String TASK_IMPL_KEY = "task_impl";

    private static final String JOB_GROUP_NAME = "timing_task_job_group";
    private static final String TRIGGER_GROUP_NAME = "timing_task_trigger_group";
    public static final String JOB_DATA_KEY_PARAMETER_MAP = "PARAMETER_MAP";
    
    public static final String JOB_DATA_KEY_CONTEXT = "key_context";
    public static final String JOB_DATA_KEY_LISTENER = "key_listener";

    private static final int TASK_RULE_DAILY_AT_HOUR_AND_MINUTES = 1;
    private static final int TASK_RULE_MONTHLY_ON_DAY_AND_HOUR_AND_MINUTES = 2;
    private static final int TASK_RULE_WEEKLY_ON_DAY_AND_HOUR_AND_MINUTE = 3;

    private Context mContext;
    private Scheduler mScheduler;
    private Logger mLogger;
    private ITaskListener mTaskListener;

    public ProfileSuggestionTaskManager(Context context) {
        super(context);
        mContext = context;
        mLogger = TaskLogger.getInstnace(context).getLogger();
        try {
            mScheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startTask(Job task,String taskImplName){
        ConfigurationBase configuration = mContext.getConfig();
        Properties taskSettings = configuration.getTaskSettings();
        try {
            ScheduleBuilder builder = createScheduleBuilder(taskImplName,taskSettings);
            Map<Object,Object> jobMap = new HashMap<Object, Object>();
            jobMap.put(JOB_DATA_KEY_CONTEXT, mContext);
            if(mTaskListener != null){
                jobMap.put(JOB_DATA_KEY_LISTENER, mTaskListener);
            }
            scheduleJob(task,taskImplName,jobMap,builder);
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning(){
        try {
            return !mScheduler.isShutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void stopTask(){
        try {
            mScheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerListener(ITaskListener taskListener) {
        mTaskListener = taskListener;
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
     * @param scheduleBuilder task rule
     * @throws org.quartz.SchedulerException
     */
    private void rescheduleJob(String jobName,String taskImplName, ScheduleBuilder scheduleBuilder) throws SchedulerException, JSONException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
        Trigger trigger = mScheduler.getTrigger(triggerKey);

        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
        JobDetail jobDetail = mScheduler.getJobDetail(jobKey);

        LogHelper.logD(mLogger, "==========exist job,reschedule it :" + jobName);
        // pause job
        mScheduler.pauseJob(jobKey);
        // pause trigger
        mScheduler.pauseTrigger(triggerKey);
        // remove the trigger
        mScheduler.unscheduleJob(triggerKey);
        // update the trigger time
        TriggerBuilder builder = trigger.getTriggerBuilder();
        trigger = builder.withSchedule(scheduleBuilder).startNow()
                .build();

        mScheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     *
     * @param job
     * @param jobName
     * @param jobMap
     * @param scheduleBuilder task rule
     * @throws org.quartz.SchedulerException
     * @throws java.text.ParseException
     * @throws InterruptedException
     */
    private void scheduleNewJob(Job job,String jobName,Map<Object,Object> jobMap,String taskImplName,ScheduleBuilder scheduleBuilder)
            throws SchedulerException, ParseException, InterruptedException, JSONException {
        // new job and trigger
        LogHelper.logD(mLogger,"==========new job :" + jobName);
        JobDetail jobDetail = newJob(job.getClass()).withIdentity(jobName, JOB_GROUP_NAME).build();
        jobDetail.getJobDataMap().put(JOB_DATA_KEY_PARAMETER_MAP, jobMap);
        // new trigger
        Trigger trigger = newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME).withSchedule(scheduleBuilder)
                .startNow().build();
        mScheduler.scheduleJob(jobDetail, trigger);
        // start
        mScheduler.start();
    }

    /**
     * schedule a job for jobName by job class
     * @param job
     * @param jobMap
     * @param scheduleBuilder task rule
     * @throws org.quartz.SchedulerException
     * @throws java.text.ParseException
     * @throws InterruptedException
     */
    private void scheduleJob(Job job,String taskImplName,Map<Object,Object> jobMap,ScheduleBuilder scheduleBuilder) throws SchedulerException, ParseException, InterruptedException, JSONException {
        String jobName = taskImplName;
        if(hasJob(jobName)){
            rescheduleJob(jobName,taskImplName,scheduleBuilder);
        }else{
            scheduleNewJob(job,jobName,jobMap,taskImplName,scheduleBuilder);
        }
    }

    private ScheduleBuilder createScheduleBuilder(String taskImplName, Properties taskSettings) throws JSONException {
        JSONObject ruleJson = getRuleJson(taskImplName, taskSettings);
        if (ruleJson != null) {
            switch (ruleJson.getInt("rule")) {
                case TASK_RULE_DAILY_AT_HOUR_AND_MINUTES:
                    return CronScheduleBuilder.dailyAtHourAndMinute(ruleJson.getInt("hour"), ruleJson.getInt("minute"));
                case TASK_RULE_WEEKLY_ON_DAY_AND_HOUR_AND_MINUTE:
                    return CronScheduleBuilder.weeklyOnDayAndHourAndMinute(ruleJson.getInt("dayOfWeek"), ruleJson.getInt("hour"), ruleJson.getInt("minute"));
                case TASK_RULE_MONTHLY_ON_DAY_AND_HOUR_AND_MINUTES:
                    return CronScheduleBuilder.weeklyOnDayAndHourAndMinute(ruleJson.getInt("dayOfMonth"), ruleJson.getInt("hour"), ruleJson.getInt("minute"));
            }
        }
        mLogger.info("ERROR!!,load task rule error,so use the 0hour,0minute as default");
        //default
        return CronScheduleBuilder.dailyAtHourAndMinute(0, 0);
    }

    /**
     *
     * @param taskImplName
     * @param taskSettings
     * @return
     * @throws com.borqs.sync.server.common.json.JSONException
     */
    private JSONObject getRuleJson(String taskImplName, Properties taskSettings) throws JSONException {
        /**
         * task_rule:[\
         { task_impl:com.borqs.sync.server.framework.services.task.ProfileSuggestionCollectionTask,\
         task_rule_detail:[\
         {rule:1,\
         enable:true,\
         hour:0,\
         minutes:0\
         },\
         {rule:2,\
         enable:false,\
         dayOfMonth:1,\
         hour:0,\
         minute:0\
         },\
         {rule:3,\
         enable:false,\
         dayOfWeek:1,\
         hour:0,\
         minute:0\
         }\
         ]\
         }\
         ]
         */
        String taskRuleStr = taskSettings.getProperty(TASK_RULE_KEY);
        JSONArray ruleList = new JSONArray(taskRuleStr);
        int length = ruleList.length();
        for (int i = 0; i < length; i++) {
            JSONObject s = ruleList.getJSONObject(i);
            String taskImpl = s.getString(TASK_IMPL_KEY);
            if (taskImplName != null && taskImplName.equals(taskImpl)) {
                try {
                    JSONArray ruleDetail = s.getJSONArray(TASK_RULE_DETAIL_KEY);
                    for (int j = 0; j < ruleDetail.length(); j++) {
                        JSONObject detailJson = ruleDetail.getJSONObject(j);
                        boolean enable = detailJson.getBoolean("enable");
                        if (enable) {
                            return detailJson;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
