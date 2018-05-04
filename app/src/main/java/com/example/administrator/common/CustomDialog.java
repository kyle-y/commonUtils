package com.example.administrator.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.newappforutils.R;


/**
 * Created by yxb on 2017/10/27.
 * 自定义弹窗，用于app统一的弹窗提示，样式待修改
 */

public class CustomDialog extends Dialog {

    public CustomDialog(@NonNull Context context) {
        this(context, R.style.dialog);
    }

    public CustomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private int msgGravity = Gravity.CENTER;

        private boolean cancelable = true;
        private Context context;
        private String title;
        private boolean showClose = false;
        private String message;
        private View customContent;
        private String positiveButtonText;
        private String negativeButtonText;
        private String oneBottomButtonText;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private OnClickListener oneBottomButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder showClose(boolean isShowClose) {
            this.showClose = isShowClose;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder body(View customContent) {
            this.customContent = customContent;
            return this;
        }

        public Builder positiveText(String positiveText) {
            this.positiveButtonText = positiveText;
            return this;
        }

        public Builder negativeText(String negativeText) {
            this.negativeButtonText = negativeText;
            return this;
        }

        public Builder oneBottomText(String oneBottomText) {
            this.oneBottomButtonText = oneBottomText;
            return this;
        }

        public Builder positiveClickListener(OnClickListener positiveClickListener) {
            this.positiveButtonClickListener = positiveClickListener;
            return this;
        }

        public Builder negativeClickListener(OnClickListener negativeClickListener) {
            this.negativeButtonClickListener = negativeClickListener;
            return this;
        }

        public Builder oneBottomClickListener(OnClickListener oneBottomClickListener) {
            this.oneBottomButtonClickListener = oneBottomClickListener;
            return this;
        }

        public CustomDialog build() {

            final CustomDialog dialog = new CustomDialog(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.dialog_normal, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView txtTitle = layout.findViewById(R.id.title);
            ImageView imageClose = layout.findViewById(R.id.close);
            View topLine = layout.findViewById(R.id.topLine);
            FrameLayout contentContainer = layout.findViewById(R.id.layoutBody);

            if (title == null) {
                txtTitle.setVisibility(View.GONE);
                topLine.setBackgroundColor(Color.TRANSPARENT);
                contentContainer.setBackgroundColor(Color.WHITE);
            } else {
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText(title);
                topLine.setBackgroundColor(Color.parseColor("#efeff4"));
                contentContainer.setBackgroundColor(Color.WHITE);
            }

            imageClose.setVisibility(showClose ? View.VISIBLE : View.GONE);
            imageClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else {
                layout.findViewById(R.id.message).setVisibility(View.GONE);
            }
            ((TextView) layout.findViewById(R.id.message)).setGravity(msgGravity);

            if (customContent != null) {
                contentContainer.removeAllViews();
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                contentContainer.addView(customContent, lp);
            }

            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }

            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }

            if (oneBottomButtonText != null) {
                ((Button) layout.findViewById(R.id.oneBottom)).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.twoBottom).setVisibility(View.GONE);
                ((Button) layout.findViewById(R.id.oneBottom))
                        .setText(oneBottomButtonText);
                if (oneBottomButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.oneBottom))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    oneBottomButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.oneBottom).setVisibility(
                        View.GONE);
            }

            dialog.setCanceledOnTouchOutside(false);//外部不可点击
            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.dialog_animation); // 添加动画
            dialog.setContentView(layout);
            dialog.setCancelable(cancelable);//能否通过返回键取消

            return dialog;
        }
    }

}
