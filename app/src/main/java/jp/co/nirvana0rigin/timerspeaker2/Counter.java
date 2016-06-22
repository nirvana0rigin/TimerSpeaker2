package jp.co.nirvana0rigin.timerspeaker2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Counter extends Sync {

    private static TextView nowTime;
    private View v;
    static String ss = "00";
    static String mm = "00";







    //_________________________________________________for life cycles

    /*
    public static Counter newInstance(int[] p) {
        Counter fragment = new Counter();
        args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    */

    public Counter() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //NOTHING
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(v == null) {
            v = inflater.inflate(R.layout.fragment_counter, container, false);

            nowTime = (TextView) v.findViewById(R.id.now_time);
        }
        return v;
    }

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //NOTHING
    }

	@Override
    public void onStart() {
        super.onStart();
        createCounterText();
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
        //NOTHING
    }








    //______________________________________________for connection on Activity









    //_______________________________________________for work this Fragment

    public void resetCounterText(){
        String now;
        if(param.getInterval() <= 10){
            now = "0" + " " + con.getString(R.string.seconds);
        }else{
            now = "00" + " " + con.getString(R.string.minute) + "  " + "00" + " " + con.getString(R.string.seconds);
        }
        nowTime.setText(now);
    }

    public void createCounterText(){
        String now;
        if(param.getInterval() <= 10){
            now = sec + " " + con.getString(R.string.seconds);
        }else{
            mm = getXX(m);
            ss = getXX(s);
            now = mm + " " + con.getString(R.string.minute) + "  " + ss + " " + con.getString(R.string.seconds);
        }
        nowTime.setText(now);
    }

    private String getXX(long i) {
        String ii;
        if (i < 10) {
            ii = ("" + 0) + i;
        } else {
            ii = "" + i;
        }
        return ii;
    }




}
