/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.runtime;

import java.lang.reflect.InvocationTargetException;

/**
 * Date: 9/20/11
 * Time: 11:47 AM
 */
public abstract class ReflectibleService extends ContextHolder{
    public ReflectibleService(Context context){
        super(context);
    }

    public static <T extends ReflectibleService> T fromName(String className, Context context){
        try {
            Class anonymousClass = Class.forName(className);
            //TODO: check interface of ReflectibleService
            Object instance = anonymousClass.getConstructor(new Class[]{Context.class}).newInstance(context);
            if( instance instanceof ReflectibleService){
                return (T)instance;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
