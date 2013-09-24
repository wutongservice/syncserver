package com.borqs.sync.server.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.borqs.sync.server.common.providers.ContactItem;

public class Utility {

    private static final int PHONE_CHECK_SIZE = 8;
	private static final Pattern PAT_MAIL = Pattern.compile(".*<(.*)>.*");
	
	public static boolean isMail(int type) {
		return (type==ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS
				|| type==ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS);
	}
	
	public static boolean isPhone(int type) {
		return (type==ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER
				|| type==ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER);
	}
	
	public static String formatMail(String mail) {
		if(null == mail) return mail;
		
		Matcher matcher = PAT_MAIL.matcher(mail);
		if(matcher.matches()) {
			return matcher.group(1);
		}
		return mail;
	}
	
	public static String formatPhone(String phone) {
		if(null == phone) return phone;
		
		// format phone number
		String value = phone.replace("-", "")
				.replace(" ", "");
		if(value.startsWith("+86")) {
			value = value.substring(3);
		} else if(value.startsWith("0086")) {
			value = value.substring(4);
		} else if(value.startsWith("(086)")) {
			value = value.substring(5);
		}
		return value;
	}
	
    public static List<? extends Object> setToList(Set<? extends Object> set){
        Object[] array = new Object[set.size()];
        set.toArray(array);
        return Arrays.asList(array);
    }

    public static boolean stringEqual(String str1, String str2) {
        if (isEmpty(str1) && isEmpty(str2)) {
            return true;
        } else if (isEmpty(str1) && !isEmpty(str2)) {
            return false;
        } else if (!isEmpty(str1) && isEmpty(str2)) {
            return false;
        } else {
            return str1.equals(str2);
        }
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isEmptyList(List<?> list){
        return list == null || list.size() == 0;
    }
    
    public static String array2String(int[] objects){
        StringBuffer sbuffer = new StringBuffer();

        if(objects != null){
            for(Object o : objects){
                if(sbuffer.length() > 0 ){
                    sbuffer.append(",");
                }
                sbuffer.append(String.valueOf(o));
            }
        }
        return sbuffer.toString();
    }

    /**
     * clean the phone number,then return the substring from phone ,the size is 8 from the end.
     * @param value
     * @return   the substring from phone whose size is 8
     */
    public static String cleanPhoneNumber(String value) {
        if(Utility.isEmpty(value)){
            return value;
        }
        //http://www.vogella.de/articles/JavaRegularExpressions/article.html
        //\W -> all non digit chars (no letters and no numbers)
        value = value.replaceAll("[\\W]", "");
        value = value.replace("-", "").replace(" ", "");
        int length = value.length();
        if (length >= PHONE_CHECK_SIZE) {
            return value.substring(length - PHONE_CHECK_SIZE);
        } else {
            return value;
        }
    }

    /**
     * extract the valid email ,for example,xueting.yu<xueting.yu@borqs.com>,we can get the xueting.yu@borqs.com
     * @param email
     * @return    the valid email address
     */
    public static String getValidEmail(String email){
        if(isEmpty(email)){
            return email;
        }
        String regex = "\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE).matcher(email);
        if (m.find()) {
            return m.group().trim();
        }
        return email.trim();
    }

    /**
     * check if the email is valid
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email){
        if(isEmpty(email)){
            return false;
        }
        String regex = "\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE).matcher(email);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * check if the value exist chinese
     * @param value
     * @return
     */
    public static boolean isChinese(String value){
        boolean isChinese = false;
        if(value != null){
            for (int i = 0; i < value.length(); i++) {
                String regEx = "[\\u4e00-\\u9fa5]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(String.valueOf(value.charAt(i)));
                if (m.find()) {
                    isChinese = true;
                }else{
                    return false;
                }
            }
        }
        return isChinese;
    }
    
    public static String getValidTel(String phoneNumbr){
        phoneNumbr = phoneNumbr.replaceAll("[\\W]", "");
        return phoneNumbr;
    }

    /**
     * if the phone is valid,only support China.
     * TODO support more country
     * @param phoneNumbr
     * @return
     */
    public static  boolean isValidTel(String phoneNumbr){
        phoneNumbr = phoneNumbr.replaceAll("[\\W]", "");

        Pattern p1 = Pattern
                .compile("^(\\+{0,1}86|0086){0,1}((\\d{3}){0,1}(\\d{8})|(\\d{4}){0,1}(\\d{7}))");
        Matcher m1 = p1.matcher(phoneNumbr);
        if (m1.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
