package com.liar.testrecorder.utils.sp;

/**
 * SharedPreferences管理类

 */
public class SpManager {

    private static SpManager mInstance = new SpManager();

    public static SpManager get() {
        return mInstance;
    }

    private SpManager() {}

    /**
     * 设置用户账号
     * @param account 账号
     */
    public void setUserAccount(String account){
        SharedPreferencesUtils.putString(SpConfig.USER_ACCOUNT, account);
    }

    /** 获取用户账号 */
    public String getUserAccount(){
        return SharedPreferencesUtils.getString(SpConfig.USER_ACCOUNT, "");
    }
}
