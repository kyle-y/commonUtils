package com.example.administrator.common.commonUtils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;

import com.example.administrator.newappforutils.R;


/**
 * Created by Administrator on 2016/8/17.
 */
public abstract class DialogUtil {

	protected Activity m_Context;
	private Dialog dialog;

	public DialogUtil(Activity context) {
		this.m_Context = context;
		initDialog();
	}

	private void initDialog() {
		dialog = new Dialog(m_Context, R.style.my_dialog);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.dialog_animation); // 添加动画
	}

	protected final Dialog getDialog() {
		if (dialog == null)
			initDialog();
		return dialog;
	}

	protected final boolean dialogIsShowing() {
		return dialog != null && dialog.isShowing();
	}

	protected final void relaseDialog() {
		if (dialogIsShowing()) dialog.dismiss();
		dialog = null;
	}
}
