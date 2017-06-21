package com.qdigo.jindouyun.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.blesdkhelp.IBlueCallback;
import com.qdigo.jindouyun.utils.DeviceDB;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.qdigo.jindouyun.MyApplication.app;

public class ScanActivity extends BaseActivity implements View.OnClickListener {

    private BlueGuardList mDeviceList;
    BleCallBack bleCallBack;

    @Bind(R.id.list_devices)
    SwipeMenuListView listView;
    @Bind(R.id.hintTv)
    TextView hintTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_scan,"蓝牙设备");
    }

    @Override
    protected void initView() {
        mIvTitleRight.setVisibility(View.VISIBLE);
        mIvTitleRight.setImageResource(R.drawable.stop);
        mDeviceList = new BlueGuardList();
        mDeviceList.reset();
        mIvTitleRight.setOnClickListener(this);
        listView.setAdapter(mDeviceList);
        initMenu();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                DeviceDB.Record record = mDeviceList.getDevice(arg2);
                app.ble.closeDisconverBleDevice();
                app.ble.disConnectDevice();
                app.ble.connectDevice(record.identifier);
                finish();
            }
        });

        app.ble.addBlueCallback(bleCallBack = new BleCallBack());

        app.ble.disconverBleDevice();
    }
    private void initMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mContext);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:
                        // delete
                        mDeviceList.removePostion(position);
                        break;

                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.ble.closeDisconverBleDevice();
        app.ble.removeBlueCallBack(bleCallBack);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_title_right:
                if (app.ble.isScanning()) {
                    mIvTitleRight.setImageResource(R.drawable.start);
                    app.ble.closeDisconverBleDevice();
                } else {
                    app.ble.disconverBleDevice();
                    mIvTitleRight.setImageResource(R.drawable.stop);
                    mDeviceList.reset();
                }

                break;
        }
    }

    @SuppressLint({ "InflateParams", "ViewHolder" })
    private class BlueGuardList extends BaseAdapter {
        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // General ListView optimization code.
            LayoutInflater inflater = ScanActivity.this.getLayoutInflater();
            View row = inflater.inflate(R.layout.listitem_device, null);//viewGroup);
            updateListItem(row, getDevice(i));
            return row;
        }

        // Public members.
        void addDevice(DeviceDB.Record r) {
            for(int i=0; i<mDevices.size(); i++) {
                //ListItem t = mDevices.get(i);
                //if(t.rec.identifier.equals(r.identifier)) return;
                DeviceDB.Record t = mDeviceList.getDevice(i);
                if(t.identifier.equals(r.identifier)) return;
            }

            ListItem item = new ListItem(r);
            mDevices.add(item);
            notifyDataSetChanged();
        }

        DeviceDB.Record getDevice(int index) {
            return mDevices.get(index).rec;
        }

        void reset() {
            mDevices.clear();
//			DeviceDB.Record rec = DeviceDB.load(ScanActivity.this);
//			if(rec != null)
//				mDeviceList.addDevice(rec);
//			notifyDataSetChanged();
        }

        // Private members.
        private class ListItem {
            DeviceDB.Record rec;
            ListItem(DeviceDB.Record r) {
                rec = r;
            }
        }
        private List<ListItem> mDevices = new ArrayList<ListItem>();

        private void updateListItem(View row, DeviceDB.Record rec) {
            TextView txt;
            txt = (TextView)row.findViewById(R.id.device_name);
            txt.setText(rec.name);
            txt = (TextView)row.findViewById(R.id.device_address);
            txt.setText(rec.identifier);
        }

        public void removePostion(int position){
            mDevices.remove(position);
            notifyDataSetChanged();
        }

    }


    class BleCallBack extends IBlueCallback {
        @Override
        public void discoverDevice(DeviceDB.Record record) {
            hintTv.setVisibility(View.GONE);
            mDeviceList.addDevice(record);
        }
    }


}
