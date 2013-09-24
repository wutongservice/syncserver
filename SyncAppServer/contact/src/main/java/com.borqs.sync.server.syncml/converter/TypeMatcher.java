/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.syncml.converter;

import java.util.ArrayList;
import java.util.List;

public class TypeMatcher {
	private int mSourceType;
	private String mMatchedType;
	private int mMatcherType;
	private List<TypeMatcher> mChildrenMatcher = new ArrayList<TypeMatcher>();
	private TypeMatcher mChild;
	
	public static final int TYPE_PHONE_MATCHER = 1;
	public static final int TYPE_ORG_MATCHER = 2;
	public static final int TYPE_EMAIL_MATCHER = 3;
	public static final int TYPE_ADDRESS_MATCHER = 4;
	public static final int TYPE_IM_MATCHER = 5;
	public static final int TYPE_WEBSITE_MATCHER = 6;
    public static final int TYPE_XTAG_MATCHER = 7;
	
	public TypeMatcher(){
	}
	
	/**
	 * get the JContact type by contact type
	 * @param contactType the contact type define in the @android.provider.ContactsContract
	 * @param matcherType phoneTypeMatcher?EmailTypeMatcher?
	 * @return null if thers is not matched type
	 */
	public String matchJContactType(int contactType,String defaultType,int matcherType){
		for (TypeMatcher child : mChildrenMatcher) {
			if(matcherType == child.mMatcherType
					&& contactType == child.mSourceType){
				return child.mMatchedType;
			}
		}
		return defaultType;
	}
	
	/**
	 * get the contact type by JContact type
	 * @param jType
	 * @param matcherType
	 * @return the contact type define in the @android.provider.ContactsContract
	 */
	public int matchContactType(String jType,int defaultType,int matcherType){
		for (TypeMatcher child : mChildrenMatcher) {
			if(matcherType == child.mMatcherType
					&& child.mMatchedType.equals(jType)){
				return child.mSourceType;
			}
		}
		return defaultType;
	}
	
	
	
	/**
	 * collect the type information
	 * @param sourceType
	 * @param matchedType
	 * @param matcherType phoneTypeMatcher?EmailTypeMatcher?
	 */
	public void addType(int sourceType,String matchedType,int matcherType){
		mChild = new TypeMatcher();
		mChild.mMatcherType = matcherType;
		mChild.mSourceType = sourceType;
		mChild.mMatchedType = matchedType;
		mChildrenMatcher.add(mChild);
	}

}


