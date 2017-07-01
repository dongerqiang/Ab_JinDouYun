package com.qdigo.jindouyun.utils;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.wheel.StringAdapter;
import com.qdigo.jindouyun.wheel.WheelView;

import static com.qdigo.jindouyun.MyApplication.app;


/***
 * dialog  工具类。
 *
 * @author fu
 */
public class DialogUtils {
    private static DialogUtils dialogUtils;

    public static DialogUtils getInstance() {
        return dialogUtils == null ? dialogUtils = new DialogUtils() : dialogUtils;
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
//        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        tipTextView.setText(msg);// 设置加载信息  

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消  

        WindowManager.LayoutParams lp = loadingDialog.getWindow().getAttributes();
        lp.width = 400;
        lp.height = 200;

        loadingDialog.setContentView(v);// 设置布局  
        return loadingDialog;

    }

    public  void showSelectList(Context ctx,String title,String[] items,final DialogCallback callback){
         final Dialog dialog = createDialog(ctx, R.layout.dialog_select_list_layout);
         final WheelView wheelView = (WheelView) dialog.findViewById(R.id.wheelView);

        TextView   mTitle = (TextView) dialog.findViewById(R.id.list_title);
        mTitle.setText(title);
         final StringAdapter adapter = new StringAdapter(ctx, items);
        wheelView.setViewAdapter(adapter);
        dialog.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.confirmBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(callback!= null){
                    callback.typeStr(adapter.getItemText(wheelView.getCurrentItem()).toString());
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public  Dialog createDialog(Context ctx,int resLayout){
        Dialog dialog = new Dialog(ctx, R.style.custom_dialog);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = app.widthPixels;
        lp.height = app.heightPixels;
        dialog.setContentView(resLayout);
        return dialog;
    }

    public static Dialog createPassDialog(Context context,final DialogCallback callback) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.password_dialog, null);// 得到加载view
        final EditText et_pw = (EditText) v.findViewById(R.id.et_pw);// 提示文字
        final String trim = et_pw.getText().toString().trim();

        Button ok = (Button) v.findViewById(R.id.ok);// 提示文字
        String opePassWord = app.deviceNotes.opePassWord(false, "000000");
        et_pw.setText(opePassWord);
        et_pw.setSelection(opePassWord.length());
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!= null){
                    callback.confirm();
                }
                app.deviceNotes.opePassWord(true,et_pw.getText().toString().trim());
                loadingDialog.dismiss();
            }
        });
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消

        WindowManager.LayoutParams lp = loadingDialog.getWindow().getAttributes();
        lp.width = app.widthPixels;
        lp.height = app.heightPixels;

        loadingDialog.setContentView(v);// 设置布局
        return loadingDialog;

    }

    public static Dialog createChangeMapDialog(Context context,final DialogCallback callback) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_change_map, null);// 得到加载view
        final LinearLayout gaode_map = (LinearLayout) v.findViewById(R.id.gaode_map);// 提示文字
        final LinearLayout baidu_map = (LinearLayout) v.findViewById(R.id.baidu_map);// 提示文字

        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(true);//可以用“返回键”取消
        baidu_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!= null){
                    callback.confirm();
                }
                loadingDialog.dismiss();
            }
        });
        gaode_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!= null){
                    callback.camareClick();
                }
                loadingDialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = loadingDialog.getWindow().getAttributes();
        lp.width = app.widthPixels;
        lp.height = app.heightPixels;

        loadingDialog.setContentView(v);// 设置布局
        return loadingDialog;

    }
}



