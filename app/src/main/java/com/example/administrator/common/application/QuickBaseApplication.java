package com.example.administrator.common.application;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;

import java.util.Stack;

/**
 * Created by Administrator on 2017/1/17.
 */

public class QuickBaseApplication extends Application{

    private Stack<Activity> activityStack = new Stack<Activity>();

    public static QuickBaseApplication fromContext(Context context){
        if ( context instanceof QuickBaseApplication )
            return (QuickBaseApplication)context;
        if ( context instanceof Activity)
            return (QuickBaseApplication)((Activity)context).getApplication();
        if ( context instanceof Service)
            return (QuickBaseApplication)((Service)context).getApplication();
        Context c = context.getApplicationContext();
        if ( c instanceof QuickBaseApplication )
            return (QuickBaseApplication)c;
        return null;
    }

    /**
     * 得到顶部的actibity
     * @return
     */
    public Activity getTopActivity(){
        return activityStack.lastElement();
    }

    /**
     * activity入栈
     * @param activity
     */
    public void pushActivity(Activity activity) {
        if(activity != null){
            activityStack.add(activity);
        }
    }

    /**
     * activity出栈
     * @param activity
     */
    public void popActivity(Activity activity){
        if(activity != null && activityStack.contains(activity)){
            activityStack.remove(activity);
        }
    }

    /**
     * 主动清空堆栈
     */
    public void clearActivities() {
        while(!activityStack.isEmpty()){
            Activity activity = activityStack.firstElement();
            if ( activity != null ){
                activity.finish();
                popActivity(activity);
            }
        }
    }

    /**
     * 主动清空堆栈，保留栈顶
     */
    public void clearActivitiesExceptTop() {
        while(activityStack.size() > 1){
            Activity activity = activityStack.firstElement();
            if ( activity != null ){
                activity.finish();
                popActivity(activity);
            }
        }
    }

    /**
     * 主动从栈底开始清栈，直到目标class（保留class）
     * @param cls
     */
    public void clearActivitiesFromBottomUntilClass(Class<?> cls){
        while(activityStack.size() > 0){
            Activity activity = activityStack.firstElement();
            if(activity != null){
                if(!activity.getClass().equals(cls)){
                    activity.finish();
                    popActivity(activity);
                }else{
                    break;
                }
            }
        }
    }

    /**
     * 主动清空堆栈，保留目标
     */
    public void clearActivitiesExceptClass(Class<?>... classes) {
        Stack<Activity> tempStack = new Stack<Activity>();
        while(!activityStack.isEmpty()){
            Activity activity = activityStack.pop();
            if(activity != null){
                if(inClassList(classes, activity.getClass())){ //是保留目标
                    tempStack.push(activity);
                }else{
                    activity.finish();
                }
            }
        }
        while(!tempStack.isEmpty()){
            activityStack.push(tempStack.pop());
        }
    }

    /**
     * 查找目标类实例，不清除类实例
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends Activity> T findTargetClass(Class<T> clazz) {
        T targetAct = null;
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(clazz)) {
                targetAct = (T) activity;
                break;
            }
        }
        return targetAct;
    }

    /**
     * 退出目标activity
     * @param cls
     */
    public void killActivity(Class<?> cls){
        for(Activity activity : activityStack){
            if(activity != null) {
                if (activity.getClass().equals(cls)) {
                    activity.finish();
                    popActivity(activity);
                    return;
                }
            }
        }
    }

    private boolean inClassList(Class<?>[] classes, Class<?> cls){
        for(Class<?> c : classes){
            if(c != null && c.equals(cls)){
                return true;
            }
        }
        return false;
    }

}
