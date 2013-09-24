package com.borqs.sync.server.task.profilesuggestion;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.task.ITaskListener;
import com.borqs.sync.server.common.task.TimingTask;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/21/12
 * Time: 6:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileSuggestionCollectionTask extends TimingTask {

    @Override
    public void executeJob(JobExecutionContext jobExecutionContext) {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        if(!dataMap.containsKey(ProfileSuggestionTaskManager.JOB_DATA_KEY_PARAMETER_MAP)){
            throw new IllegalArgumentException("error!job data map parameter is null");
        }
        Map<Object,Object> parameterMap = (Map<Object,Object>)dataMap.get(ProfileSuggestionTaskManager.JOB_DATA_KEY_PARAMETER_MAP);
        if(!parameterMap.containsKey(ProfileSuggestionTaskManager.JOB_DATA_KEY_CONTEXT)){
            throw new IllegalArgumentException("error!job's context is null");
        }
        Context context = (Context) parameterMap.get(ProfileSuggestionTaskManager.JOB_DATA_KEY_CONTEXT);
        ITaskListener taskListener = (ITaskListener)parameterMap.get(ProfileSuggestionTaskManager.JOB_DATA_KEY_LISTENER);
        //send the information
        if(taskListener != null){
            taskListener.onTaskStart();
        }
        ProfileSuggestionTaskOperator operator = new ProfileSuggestionTaskOperator(context);
        operator.sendInformation();

        if(taskListener != null){
            taskListener.onTaskEnd();
        }
    }

}
