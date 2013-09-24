/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.util;

import com.borqs.sync.server.common.runtime.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Date: 9/22/11
 * Time: 5:35 PM
 */
public class ReflectUtil {

    public static Class forName(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

	public static Object newInstance(String className, Context context) {
		try {
			return Class.forName(className)
                    .getConstructor(new Class[]{Context.class})
                    .newInstance(context) ;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        }
		return null;
	}

    public static boolean interfaceOf(Object impl, Class iface) {
        //TODO: check interface of impl
        return true;
    }

    public static void invoke(Object context, String methodName, Object parameter){
        try {
            Class methodHodler = context.getClass();
            Method method = methodHodler.getMethod(methodName, parameter.getClass());
            method.invoke(context, parameter);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
