/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.webagent.account;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.WebConfiguration;
import com.borqs.sync.server.webagent.base.AccountErrorCode;
import com.borqs.sync.server.webagent.util.TextUtil;
import com.borqs.sync.server.webagent.util.WebLog;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class AccountRequestOperation {

    private File mFile;
    private Logger mLogger;
    
    public AccountRequestOperation(Context context,String fileName) throws AccountException {
        mLogger = WebLog.getLogger(context);
        String path = WebConfiguration.getWebAgentConfPath(context);
        if(!TextUtil.isEmpty(path)){
            mFile = new File(path);
            if(!mFile.exists()){
               mFile.mkdirs();
            }
            mFile = new File(path + fileName);
            if (!mFile.exists()) {
                try {
                    mLogger.info(fileName + "not exist,create new");
                    mFile.createNewFile();
                } catch (IOException e) {
                    mLogger.info(e.getMessage());
                    e.printStackTrace();
                }
            }
        }else{
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR,"webagent conf path is null");
        }
    }

    public String readMobileByUid(String guid) {
        Properties pro = readAccount();
        mLogger.info("readed account : " + pro.toString());
        Set<Object> keySet = pro.keySet();
        mLogger.info("pro keyset is :" + keySet.toString());
        if (keySet != null) {
            for (Object mobile : keySet) {
                String readedGuid = pro.getProperty(mobile.toString());
                mLogger.info("readed guid by mobile is :" + readedGuid);
                mLogger.info("the specified guid is :" + guid);
                if (guid.equals(readedGuid)) {
                    return mobile.toString();
                }
            }
        }
        
//        if(pro.containsKey(guid)){
//            return pro.getProperty(guid);
//        }
        return null;
    }

    /**
     * 
     * @param guid
     * @param mMobile
     */
    public void writeMobile(String guid, String mMobile) {
        //TODO sometimes,readAccount return null value when the file is being writing,.
        //Should research how to read the file normally when writing,instead of reading null.
        Properties pro = readAccount();
        FileOutputStream out = null;
        FileChannel fcout = null;
        FileLock flout = null;
        try {
            if (!mFile.exists())
                mFile.createNewFile();
            // lock the file
          //TODO sometimes,readAccount return null value when the file is being writing,.
            //Should research how to read the file normally when writing,instead of reading null.
            //now append the bind(mobile with guid) info
            out = new FileOutputStream(mFile, false);
            fcout = out.getChannel();
            while (true) {
                try {
                    flout = fcout.tryLock();
                    if (flout != null) {
                        break;
                    } else {
                        mLogger.info("there is someone access the file,sleep 20ms");
                        Thread.sleep(20);
                    }
                } catch (OverlappingFileLockException e) {
                    mLogger.info("OverlappingFileLockException,sleep 20ms");
                    Thread.sleep(20);
                }
            }
            // write
            writeToProperties(guid, mMobile, pro, out);
        } catch (IOException e) {
            mLogger.info(e.getMessage());
        } catch (InterruptedException e) {
            mLogger.info(e.getMessage());
        } finally {
            try {
                if (flout != null) {
                    flout.release();
                }
                if (fcout != null) {
                    fcout.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
                out = null;
            } catch (Exception e) {
                mLogger.info(e.getMessage());
            }
        }

    }

    private void writeToProperties(String guid, String mobile, Properties propeties,
            FileOutputStream out) {
        // write
        if (propeties == null) {
            propeties = new Properties();
        }
        Properties pro = propeties;
        mLogger.info("bind mobile: " + mobile + " with guid: " + guid);
        pro.setProperty(mobile, guid);
        try {
            pro.store(out, "mobile=guid");
            out.getFD().sync();
        } catch (IOException e) {
            mLogger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    private Properties readAccount() {
        FileInputStream fis = null;
        FileChannel fcin = null;
        FileLock flin = null;
        try {
            fis = new FileInputStream(mFile);
            fcin = fis.getChannel();
            flin = null;
            while (true) {
                try {
                    flin = fcin.tryLock(0, Long.MAX_VALUE, true);
                    if (flin != null) {
                        break;
                    } else {
                        mLogger.info("there is someone access the file,sleep 20ms");
                        Thread.sleep(20);
                    }
                } catch (OverlappingFileLockException e) {
                    mLogger.info("OverlappingFileLockException,sleep 20ms");
                    Thread.sleep(20);
                }
            }
            return readAcount(fis);
        } catch (InterruptedException e) {
            mLogger.info(e.getMessage());
        } catch (FileNotFoundException e) {
            mLogger.info(e.getMessage());
        } catch (IOException e) {
            mLogger.info(e.getMessage());
        } finally {
            try {
                if (flin != null) {
                    flin.release();
                }
                if (fcin != null) {
                    fcin.close();
                }
                if (fis != null) {
                    fis.close();
                }
                fis = null;
            } catch (Exception e) {
                mLogger.info(e.getMessage());
            }
        }
        return null;

    }

    private Properties readAcount(FileInputStream fis) {
        Properties pro = new Properties();
        try {
            pro.load(fis);
            return pro;
        } catch (IOException e) {
            mLogger.info(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mLogger.info(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
