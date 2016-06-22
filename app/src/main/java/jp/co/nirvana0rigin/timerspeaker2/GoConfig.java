package jp.co.nirvana0rigin.timerspeaker2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class GoConfig extends Sync {

    private OnGoConfigListener mListener;
    private LinearLayout base;
    private Button goConfig;





    //_______________________________________________for life cycles

    /*
    public static GoConfig newInstance(int[] param) {
        GoConfig fragment = new GoConfig();
        args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    */

    public GoConfig() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//NOTHING
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_go_config, container, false);

        base = (LinearLayout) v.findViewById(R.id.base_go_config);
        goConfig = (Button) v.findViewById(R.id.go_config_b);
        goConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!param.isReset()){
            removeButton();
        }else{
            addButton();
        }
    }
	/*
	public void onStart()
    public void onStop() {
    public void onSaveInstanceState(Bundle outState) {
        //NOTHING
    */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }







    //__________________________________________________for connection on Activity

    public interface OnGoConfigListener {
        public void onGoConfig();
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onGoConfig();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnGoConfigListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnGoConfigListener");
        }
    }








    //_________________________________________________for work on Fragment

    public void removeButton(){
        if(base.getChildAt(0) != null){
            base.removeView(goConfig);
        }
    }

    public void addButton() {
        if (base.getChildAt(0) == null) {
            base.addView(goConfig);
        }
    }

}
