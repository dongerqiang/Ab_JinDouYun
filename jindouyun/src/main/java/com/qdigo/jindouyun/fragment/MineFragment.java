package com.qdigo.jindouyun.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdigo.jindouyun.MyApplication;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.activity.MainActivity;
import com.qdigo.jindouyun.activity.ScanActivity;
import com.qdigo.jindouyun.activity.SettingActivity;
import com.qdigo.jindouyun.activity.WebActivity;
import com.qdigo.jindouyun.utils.DeviceDB;
import com.qdigo.jindouyun.utils.DialogCallback;
import com.qdigo.jindouyun.utils.DialogUtils;
import com.xiaofu_yan.blux.smart_bike.SmartBike;

import static com.qdigo.jindouyun.MyApplication.app;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageView connectimage;
    private ImageView connecttext;
    private LinearLayout item1;
    private LinearLayout item2;
    private LinearLayout item3;
    private LinearLayout item4;
    public static final String TAG = "MineFragment";

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.mine_fragment_layout, container, false);
        item3 = (LinearLayout) inflate.findViewById(R.id.item3);
        item2 = (LinearLayout) inflate.findViewById(R.id.item2);
        item1 = (LinearLayout) inflate.findViewById(R.id.item1);
        item4 = (LinearLayout) inflate.findViewById(R.id.item4);
        connectimage = (ImageView) inflate.findViewById(R.id.connect_image);
        connecttext = (ImageView) inflate.findViewById(R.id.connect_text);
        initView();
        initLisener();
        return inflate;
    }
    private void initLisener() {
        connecttext.setOnClickListener(this);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);

    }



    private void initView() {
        int item1ChildCount = item1.getChildCount();
        for (int i = 0; i <item1ChildCount ; i++) {
            if(i == 0){
                ((ImageView)item1.getChildAt(i)).setImageResource(R.drawable.change_password);
                ((ImageView)item2.getChildAt(i)).setImageResource(R.drawable.app_detail);
                ((ImageView)item3.getChildAt(i)).setImageResource(R.drawable.solve_detail);
                ((ImageView)item4.getChildAt(i)).setImageResource(R.drawable.setting);
            }else if (i==1){
                ((TextView)item1.getChildAt(i)).setText("修改密码");
                ((TextView)item2.getChildAt(i)).setText("app介绍");
                ((TextView)item3.getChildAt(i)).setText("常见问题");
                ((TextView)item4.getChildAt(i)).setText("参数设置");
            }
        }
        connectNotify(((MainActivity)(getActivity())).connect);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.connect_text:
                if(app.ble.isSmartBikeAvailable()){
                    app.ble.disConnectDevice();
                }else if(app.ble.getSmartBike() !=null){
                    SmartBike smartBike = app.ble.getSmartBike();
                    boolean connected = app.ble.getSmartBike().connected();
                    if(connected){
                        //取消
                        smartBike.cancelConnect();
                    }else{
                        DeviceDB.Record load = DeviceDB.load(getActivity());
                        app.ble.initBle(getActivity());
                        if(load != null){
                            if(!TextUtils.isEmpty(load.key)){
                                MyApplication.logBug("key pair === "+load.key);
                                smartBike.setConnectionKey(load.key);
                                smartBike.connect();
                            }else{
                                startActivity(new Intent(getActivity(), ScanActivity.class));
                            }
                        }

                        startActivity(new Intent(getActivity(), ScanActivity.class));

                    }
                }else{
                    startActivity(new Intent(getActivity(), ScanActivity.class));
                }
                break;
            case R.id.item1:
                //change password 0
                Dialog passDialog = DialogUtils.createPassDialog(getActivity(), new DialogCallback() {
                    @Override
                    public void confirm(String imei) {
                        super.confirm();

                        app.ble.setPair(imei);

                    }
                });
                passDialog.show();
                break;
            case R.id.item2:
                //app 介绍 1
                openWebActivity(1,"App介绍");
                break;
            case R.id.item3:
                //常见问题 2
                openWebActivity(2,"常见问题");
                break;
            case R.id.item4:
                //solve detail
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            default:
                break;
        }
    }

    public void openWebActivity(int index,String title){
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra("index",index);
        intent.putExtra("title",title);
        startActivity(intent);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

   public void connectNotify(boolean connect){
       if(connect){
           connectimage.setImageResource(R.drawable.connect_image);
           connecttext.setImageResource(R.drawable.disconnect_text);
       }else{
           connectimage.setImageResource(R.drawable.diconnect_image);
           connecttext.setImageResource(R.drawable.connect_text);
       }
   }


}
