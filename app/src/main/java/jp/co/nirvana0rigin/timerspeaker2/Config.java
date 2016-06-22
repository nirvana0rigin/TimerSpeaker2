package jp.co.nirvana0rigin.timerspeaker2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class Config extends Sync implements View.OnClickListener {


    private Button[] cFlag = new Button[6];
    private Button[] iFlag = new Button[6];
    private Button[][] flag = {cFlag, iFlag};

    private OnConfigListener mListener;

    private Button car1;
    private Button car2;
    private Button car3;
    private Button car4;
    private Button interval1;
    private Button interval10;
    private Button interval60;
    private Button interval300;

    private View v;







    //__________________________________________________for life cycles

    /*
    //初回は必ずここから起動
    public static Config newInstance(int[] param) {
        Config fragment = new Config();
        args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    */

    public Config() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //NOTHING
    }

    //Viewの生成のみ、表示はonStart
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_config, container, false);

        car1 = (Button) v.findViewById(R.id.car1);
        car1.setOnClickListener(this);
        cFlag[1] = car1;
        car2 = (Button) v.findViewById(R.id.car2);
        car2.setOnClickListener(this);
        cFlag[2] = car2;
        car3 = (Button) v.findViewById(R.id.car3);
        car3.setOnClickListener(this);
        cFlag[3] = car3;
        car4 = (Button) v.findViewById(R.id.car4);
        car4.setOnClickListener(this);
        cFlag[4] = car4;
        interval1 = (Button) v.findViewById(R.id.interval1);
        interval1.setOnClickListener(this);
        iFlag[1] = interval1;
        interval10 = (Button) v.findViewById(R.id.interval10);
        interval10.setOnClickListener(this);
        iFlag[2] = interval10;
        interval60 = (Button) v.findViewById(R.id.interval60);
        interval60.setOnClickListener(this);
        iFlag[3] = interval60;
        interval300 = (Button) v.findViewById(R.id.interval300);
        interval300.setOnClickListener(this);
        iFlag[4] = interval300;

        Button[][] flag2 = {cFlag, iFlag};
        flag = flag2;

        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    onBackButtonPressed();
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //NOTHING
    }

    //選択状況を表示
    @Override
    public void onStart() {
        super.onStart();
        setSelectColor();
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
        mListener = null;
    }








    //________________________________________________for connection on Activity

    public interface OnConfigListener {
        public void onConfig();
        public void onConfigBackButton();
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onConfig();
        }
    }

	public void onBackButtonPressed() {
        if (mListener != null) {
            mListener.onConfigBackButton();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnConfigListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnConfigListener");
        }
        con = context.getApplicationContext();
    }








    //_________________________________________________for work on this Fragment

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int choice = 0;
        int target = 0;
        switch (id) {
            case R.id.car1:     choice = 1;target = 0;break;
            case R.id.car2:     choice = 2;target = 0;break;
            case R.id.car3:     choice = 3;target = 0;break;
            case R.id.car4:     choice = 4;target = 0;break;
            case R.id.interval1:        choice = 1;target = 1;break;
            case R.id.interval10:        choice = 10;target = 1;break;
            case R.id.interval60:        choice = 60;target = 1;break;
            case R.id.interval300:        choice = 300;target = 1;break;
        }

        if(target==0){
            param.setCarNo(choice);
        }else{
            param.setInterval(choice);
        }
        setSelectColor();
        toActivity(param);
    }

    private void setSelectColor(){
        setNotSelectColor();
        for(int i=0; i<2; i++) {
            if (i == 0) {
                String cs = "choice_car0" + param.getCarNo() + "t";
                int ci = res.getIdentifier(cs, "drawable", con.getPackageName());
                ((Button) flag[i][param.getCarNo()]).setBackgroundResource(ci);
            }else{
                int x = (int)param.getInterval();
                if(x == 1){}  //NOTHING
                else if(x == 10){x = 2;}
                else if(x == 60){x = 3;}
                else{x = 4;}
                ((Button) flag[i][x]).setBackgroundResource(R.drawable.choice_true);
            }
        }
    }

    private void setNotSelectColor(){
        for(int i=0; i<2; i++) {
            if (i == 0) {
                for (int j = 1; j < 5; j++) {
                    if (flag[i][j] == null) {
                        //NOTHING
                    } else {
                        String cs = "choice_car0"+j+"f";
                        int ci = res.getIdentifier(cs, "drawable", con.getPackageName());
                        ((Button) flag[i][j]).setBackgroundResource(ci);
                    }
                }
            } else {
                for (int j = 1; j < 5; j++) {
                    if (flag[i][j] == null) {
                        //NOTHING
                    } else {
                        ((Button) flag[i][j]).setBackgroundResource(R.drawable.choice_false);
                    }
                }
            }
        }
    }



}
