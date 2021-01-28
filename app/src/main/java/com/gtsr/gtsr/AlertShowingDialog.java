package com.gtsr.gtsr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by .
 */
public class AlertShowingDialog extends Dialog {
    Dialog dialog;
    ImageView imageView;
    Context context;

    public AlertShowingDialog(Context context1, String message, String message1, String ok) {
        super(context1);
        context = context1;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_custom_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        //
        TextView txtView = (TextView) dialog.findViewById(R.id.textView);
        TextView txtView1 = (TextView) dialog.findViewById(R.id.text_info);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);


        txtView1.setText(message1);
        txtView.setText(message);
        btnOk.setText(ok);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        if (!((Activity) context).isFinishing()) {
            dialog.show();
        }
    }
    public AlertShowingDialog(Context context1, String message, String ok) {
        super(context1);
        context = context1;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_custom_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        //
        TextView txtView = (TextView) dialog.findViewById(R.id.textView);
        TextView txtView1 = (TextView) dialog.findViewById(R.id.text_info);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);


        txtView1.setText("");
        txtView.setText(message);
        btnOk.setText(ok);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
                context.startActivity(new Intent(context,HomeActivity.class));
            }
        });
        if (!((Activity) context).isFinishing()) {
            dialog.show();
        }
    }
}
