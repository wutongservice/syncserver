package com.borqs.sync.server.task.profilesuggestion;

import com.borqs.information.rpc.service.SendInfo;
import com.borqs.sync.server.common.information.InformationService;
import com.borqs.sync.server.common.information.InformationUidDefine;
import com.borqs.sync.server.common.profilesuggestion.ProfileSuggestionGenerator;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.LogHelper;
import com.borqs.sync.server.task.util.TaskLogger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/29/12
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileSuggestionTaskOperator {

    private static final int BORQS_ID_QUERY_OFFSET = 500;
    private static final int POOL_THREAD_COUNT = 10;

    private static final String TITLE = "您的好友给您增加了新的联系信息";
    private static final String CHANGE_REQUEST_URI = "sync://com.borqs.service.accountsync/profilesuggestion_detail";
    private static final String CHANGE_REQUEST_APP_ID = "10";

    private Context mContext;
    private InformationService mInformationService;
    private ProfileSuggestionGenerator mProfileSuggestionGenerator;
    private Logger mLogger;

    public ProfileSuggestionTaskOperator(Context context){
        mContext = context;
        mLogger = TaskLogger.getInstnace(mContext).getLogger();
        mProfileSuggestionGenerator = new ProfileSuggestionGenerator(mContext);
        mInformationService =  new InformationService(mContext);
        mInformationService.setLogger(mLogger);
        mProfileSuggestionGenerator.setLogger(mLogger);
    }



    public void sendInformation() {
        try {
            LogHelper.logD(mLogger, "start send profile suggestion");

            ProfileSuggestionDAO dao = new ProfileSuggestionDAO(mContext);
            long borqsIdsCount = dao.getBorqsIdsCount();
            mLogger.info("the count of borqsids to be collected profile suggestion is :" + borqsIdsCount);

            long page;

            if (borqsIdsCount <= BORQS_ID_QUERY_OFFSET) {
                page = 1;
            } else {
                page = borqsIdsCount / BORQS_ID_QUERY_OFFSET + 1;
            }

            //query borqsid by page,and then send suggestion
            for (int i = 1; i <=page; i++) {
                List<String> borqsIds = dao.getScheduledBorqsIds(i, BORQS_ID_QUERY_OFFSET);
                if (borqsIds != null) {
                    //judge if the borqsid's profile change
                    ExecutorService pool = Executors.newFixedThreadPool(POOL_THREAD_COUNT);
                    SuggestionGeneratorThread thread;
                    for (String borqsId : borqsIds) {
                        thread = new SuggestionGeneratorThread(borqsId);
                        pool.execute(thread);
                    }
                    pool.shutdown();
                    while (!pool.isTerminated()) {
                    }
                }
                mInformationService.sendInformation();
                mInformationService.clearSendInfo();
            }
        } finally {
            mInformationService.release();
        }

    }

    class SuggestionGeneratorThread extends Thread{

        private String mBorqsId;
        public SuggestionGeneratorThread(String borqsId){
            mBorqsId = borqsId;
        }

        @Override
        public void run() {
            if(mProfileSuggestionGenerator.hasChange(mBorqsId)){
                mLogger.info("the changed borqsId is :" + mBorqsId);
                mInformationService.addSendInfo(createSendInfo(mBorqsId));
            }
        }
    }

    private SendInfo createSendInfo(String borqsId){
        SendInfo info = new SendInfo();
        //info.action = CHANGE_REQUEST_ACTION;
        info.uri = CHANGE_REQUEST_URI;
        info.appId = CHANGE_REQUEST_APP_ID;
        info.data = null;
        info.type = InformationUidDefine.GUID_SYNC_CHANGE_REQUEST;
        info.senderId = "0";
        info.receiverId = borqsId;
        info.title = TITLE;
        info.processMethod = 1;
        info.objectId = borqsId;
        return info;
    }
}
