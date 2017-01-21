package com.example.administrator.common.commonUtils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/1/17.
 */

public class InputFilterUtils {

    /**
     * 用来过滤除QQ emoji表情、一般字符、基本汉字之外的特殊字符，用□替换
     * (部分emoji表情在某些机型不显示，需要作相应的处理)
     * @return
     */
    public static InputFilter emojiInfilter(){
        InputFilter emojiFilter = new InputFilter ( ) {
            Pattern emoji = Pattern . compile (
                    "[\\ue001-\\uefff]*" ,
                    Pattern . UNICODE_CASE | Pattern . CASE_INSENSITIVE ) ;
            @Override
            public CharSequence filter (CharSequence source , int start , int end , Spanned dest , int dstart ,
                                        int dend ) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < source.length(); i++){
                    if (emoji.matcher(String.valueOf(source.charAt(i))).matches()) {
                        builder.append("\u25a1");
                        continue;
                    }
                    builder.append(source.charAt(i));
                }
                return builder.toString() ;
            }
        } ;
        return emojiFilter;
    }
}
