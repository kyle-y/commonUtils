package com.example.administrator.common.commonUtils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/1/11.
 */

public class LoginUtils {

    /**
     * 判断密码格式是否符合要求
     * @param pwd
     * @return
     */
    public static boolean checkPwdStyle(String pwd) {
        boolean isRight = false;
        String format = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{6,12}$";//密码格式
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(pwd);
        if (matcher.matches()) {
            isRight = true;
        }
        return isRight;
    }

    /**
     * 判断是否是手机号码
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188、147
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、177（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][34758]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }


}
