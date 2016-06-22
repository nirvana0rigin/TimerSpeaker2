package jp.co.nirvana0rigin.timerspeaker2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class Reset extends Sync {

    private OnResetListener mListener;
	private Button reset;
    private LinearLayout base;






    //______________________________________________________for life cycles

    /*
    public static Reset newInstance(int[] param) {
        Reset fragment = new Reset();
        args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    */

    public Reset() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//NOTHING
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_reset, container, false);

        base = (LinearLayout) v.findViewById(R.id.reset_base);
        reset = (Button)v.findViewById(R.id.reset_b);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                param.resetParam();
                toActivity(param);
                onButtonPressed();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(param.isHalfwayStopped() && !param.isReset()){
        	addButton();
        }else{
            removeButton();
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





    //____________________________________________________for connection on Activity

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onReset();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnResetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnResetListener");
        }
    }

    public interface OnResetListener {
        public void onReset();

    }






    //____________________________________________________for work on Fragment

    public void removeButton() {
        if (base.getChildAt(0) != null) {
            base.removeView(reset);
        }
    }

    public void addButton() {
        if (base.getChildAt(0) == null) {
            base.addView(reset);
        }
    }




}
