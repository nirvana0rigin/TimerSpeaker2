package jp.co.nirvana0rigin.timerspeaker2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;



public class Info extends Sync {

    private TextView settingTime ;
    private View v;






    //___________________________________________________for life cycles

    /*
    public static Info newInstance(int[] param) {
        Info fragment = new Info();
        args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    */

    public Info() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //NOTHING
    }

	//Viewの生成のみ、表示はonStart
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_info, container, false);

        settingTime = (TextView) v.findViewById(R.id.setting_time);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //NOTHING
    }

    //選択された状況を表示
    @Override
    public void onStart() {
        super.onStart();
        switch ((int)param.getInterval()) {
            case 1:
                settingTime.setText(R.string.sec1);
                break;
            case 10:
                settingTime.setText(R.string.sec10);
                break;
            case 60:
                settingTime.setText(R.string.min1);
                break;
            case 300:
                settingTime.setText(R.string.min5);
                break;
        }
    }

    /*
    public void onResume() {
    public void onStop() {
    public void onSaveInstanceState(Bundle outState) {
        //NOTHING
    */

    @Override
    public void onDetach() {
        super.onDetach();
    }







    //_____________________________________________________for connection on Activity









    //____________________________________________________for work on Fragment




}
