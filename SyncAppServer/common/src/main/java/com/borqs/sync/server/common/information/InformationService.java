
package com.borqs.sync.server.common.information;

import com.borqs.information.rpc.service.IInformationsService;
import com.borqs.information.rpc.service.Info;
import com.borqs.information.rpc.service.SendInfo;
import com.borqs.information.rpc.service.StateResult;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.LogHelper;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.Ipc;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class InformationService {

    private static final String CONFIG_INFORMATION = "config/information.properties";
    public static final String TAG = "Information";

    private static final String PROPS_URL = "url";

    private static final String SYNC_APP_HOME = "sync.app.home";

    private IInformationsService mInfoService;
    private Transceiver mTrans;

    private Logger mLogger;

    private List<SendInfo> mSendInfos = new ArrayList<SendInfo>();

    //TODO Information life circle
    public InformationService(Context context) {
        try {
            Properties props = new Properties();
            String sync_app_home = System.getProperty(SYNC_APP_HOME);
            props.load(new FileInputStream(sync_app_home + "/" + CONFIG_INFORMATION));
            String url = props.getProperty(PROPS_URL);
            URI uri = new URI(url);
            mTrans = Ipc.createTransceiver(uri);
            mInfoService = SpecificRequestor.getClient(IInformationsService.class, mTrans);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLogger(Logger logger){
        mLogger = logger;
    }

    public void addSendInfo(SendInfo info) {
        mSendInfos.add(info);
    }

    public void clearSendInfo(){
        if(mSendInfos != null){
            mSendInfos.clear();
        }
    }

    public void sendInformation() {
        mLogger.info("========sendInformation,mSendInfos.size:" + mSendInfos.size());
        try {
            if (mSendInfos != null) {
                for (SendInfo info : mSendInfos) {
                    if(info == null){
                        continue;
                    }
                    StateResult result = mInfoService.replaceInfo(info);
                    mLogger.info("==============" + info.senderId +
                            " send to " + info.receiverId + ",result:" + result.toString() + "\n");
                }
            }
        } catch (AvroRemoteException e) {
            e.printStackTrace();
        }
    }

    public void process(String appid,String type,String receiverid,String objectid){
        try {
            List<Info> result = mInfoService.queryInfo(appid,type,receiverid,objectid);
            if(result != null && result.size() > 0){
                Info info = result.get(0);
                long id = info.id;
                String stateResult = mInfoService.process(String.valueOf(id)).toString();
                mLogger.info("mark:" + id + ",result:" + stateResult + "\n");
            }else{
                mLogger.info("can not query the corresponding information by appid:" + appid + ",type:" + type
                + ",receiverid:" + receiverid + ",objectid:" + objectid);
            }
        } catch (AvroRemoteException e) {
            e.printStackTrace();
        }
    }

    public void release(){
        if(mTrans != null){
            mLogger.info("================close mTrans ");
            try {
                mTrans.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
