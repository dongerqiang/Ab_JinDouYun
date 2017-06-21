package com.qdigo.deq.blesdk.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qdigo.deq.blesdk.R;
import com.qdigo.deq.blesdk.fragment.Num1Fragment;
import com.qdigo.deq.blesdk.fragment.Num2Fragment;
import com.qdigo.deq.blesdk.fragment.Num3Fragment;
import com.qdigo.deq.blesdk.fragment.Num4Fragment;
import com.qdigo.deq.blesdk.utils.LogUtils;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, Num1Fragment.OnFragmentInteractionListener, Num2Fragment.OnFragmentInteractionListener, Num3Fragment.OnFragmentInteractionListener, Num4Fragment.OnFragmentInteractionListener {

    private static final java.lang.String TAG = "MapActivity";
    private android.widget.FrameLayout fragmentcontainer;
    private android.support.v7.widget.AppCompatRadioButton num1, num2, num3, num4;
    private TextView actionBarTitle;
    private ImageButton actionBarMore, actionBarBack;
    private FragmentManager supportFragmentManager;
    private Num1Fragment num1Fragment;
    private Num2Fragment num2Fragment;
    private Num3Fragment num3Fragment;
    private Num4Fragment num4Fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        initView();
        jumpFragment(1);

    }

    private void initView() {
        //init actionbar
        actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        actionBarMore = (ImageButton) findViewById(R.id.action_bar_more);
        actionBarBack = (ImageButton) findViewById(R.id.action_bar_back);

        //init content view
        this.num3 = (AppCompatRadioButton) findViewById(R.id.num3);
        this.num2 = (AppCompatRadioButton) findViewById(R.id.num2);
        this.num4 = (AppCompatRadioButton) findViewById(R.id.num4);
        this.num1 = (AppCompatRadioButton) findViewById(R.id.num1);
        this.fragmentcontainer = (FrameLayout) findViewById(R.id.fragment_container);

        actionBarTitle.setOnClickListener(this);
        actionBarMore.setOnClickListener(this);
        actionBarBack.setOnClickListener(this);
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //aciton bar
            case R.id.action_bar_back:
                finish();
                break;
            case R.id.action_bar_more:
                LogUtils.i(TAG,"search somewhere");
                break;
            // radio button
            case R.id.num1:
                jumpFragment(1);
                break;
            case R.id.num2:
                jumpFragment(2);
                break;
            case R.id.num3:
                jumpFragment(3);
                break;
            case R.id.num4:
                jumpFragment(4);
                break;
        }
    }

    private void jumpFragment(int i) {
        Fragment choose;
        switch (i) {
            case 1:
                num1Fragment = new Num1Fragment();
                choose = num1Fragment;
                break;
            case 2:
                num2Fragment = new Num2Fragment();
                choose = num2Fragment;
                break;
            case 3:
                num3Fragment = new Num3Fragment();
                choose = num3Fragment;
                break;
            case 4:
                num4Fragment = new Num4Fragment();
                choose = num4Fragment;
                break;
            default:
                choose = num1Fragment;
                break;
        }
        if (supportFragmentManager == null) {
            supportFragmentManager = getSupportFragmentManager();
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, choose).commit();
    }

    // fragment back
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
