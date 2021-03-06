package jp.co.nirvana0rigin.timerspeaker2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Sync extends Fragment {

	/*
	paramは全てのFragmentに共通で使う。
    */
    public static Param param ;
    private static final String PARAM = "param";
    public static long sec;
    public static long s ; 
    public static long m ;
    public static boolean isTimerStopped;
    
    private static Bundle args;
    private OnSyncListener mListener;
    public static Context con;
    public static Resources res;
    private static ScheduledExecutorService scheduler;
    private static ScheduledFuture<?> future;
    private static Handler handler;





    //__________________________________________________for life cycles

    //初回は必ずここから起動
    public static Sync newInstance(Param p) {
        param =p;
        Sync fragment = new Sync();
        args = new Bundle();
        args.putSerializable(PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    public Sync() {
        // Required empty public constructor
    }

    //paramの復帰とコンテキスト取得
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("_______sync_____","onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param = (Param)getArguments().getSerializable(PARAM);
        }
		//activity再生成時に破棄させないフラグを立てる
		setRetainInstance(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		//NOTHING
    }

    //paramを復帰
    @Override
    public void onStart() {
    	Log.d("_______sync_____","onStart");
        super.onStart();
        handler = new Handler();
        param = (Param)args.getSerializable(PARAM);
        if(param.isRunning()){
            isTimerStopped  = false;
        	onRestartSync(true);
        }
        if(param.isAlreadyEnded()){
            resetParam();
        }else {
            sec = param.getSec();
            s = param.getS();
            m = param.getM();
            if (param.isRunning()) {
                startTimer();
            }
        }
    }

    @Override
    public void onResume() {
    	Log.d("_______sync_____","onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
    	Log.d("_______sync_____","onPause");
        args.putSerializable(PARAM, param);
        toActivity(param);
        super.onPause();
    }

    @Override
    public void onStop() {
    	Log.d("_______sync_____","onStop");
        stopTimer();
        if (param.isRunning()) {
        	onRestartSync(false);
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("_______sync_____","onSave");
        outState.putSerializable(PARAM, param);
        toActivity(param);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

	@Override
    public void onDestroy() {
    	Log.d("_______sync_____","onD");
        super.onDestroy();
    }








    //________________________________________________for connection on Activity

    public interface OnSyncListener {
        public void onSyncParam(Param param);
        public void onSyncSec();
        public void onSyncMin(String min);
        public void onSyncTimeUp();
        public void onSyncRestart(boolean run);
    }

    public void toActivity(Param param) {
        if (mListener != null) {
            mListener.onSyncParam(param);
        }
    }

    public void sendSec() {
        if (mListener != null) {
            mListener.onSyncSec();
        }
    }

    public void onTime(String time) {
        if (mListener != null) {
            mListener.onSyncMin(time);
        }
    }

    public void onTimeUp(){
        if (mListener != null) {
            mListener.onSyncTimeUp();
        }
    }

    public void onRestartSync(boolean run){
        if (mListener != null) {
            mListener.onSyncRestart(run);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnSyncListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSynkListener");
        }
        con = context.getApplicationContext();
        res = getResources();
    }








    //_________________________________________________for work on this Fragment

    public void startTimer() {
        isTimerStopped = false;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        long delay = param.getDelay();
        future = scheduler.scheduleAtFixedRate(new Task(), delay, 1000, TimeUnit.MILLISECONDS);
    }

    public void stopTimer() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        isTimerStopped = true;
    }

    private class Task implements Runnable {
        public void run() {
            if (isTimerStopped) {
                handler.getLooper().quit();
            }
            handler.post(new Runnable() {
                public void run() {
                    sec++;
                    createTime();
                    //設定時間で終了させる
                    if (param.getEndingTime() <= sec * 1000) {
                        resetParam();
                    }
                    //強制終了
                }
            });
        }
    }

    private void createTime(){
        if(param.getInterval() <= 10){
            createTimeSec();
        }else{
            createTimeMin();
        }
    }

    private void createTimeMin() {
        s = sec % 60;
        m = sec / 60;
        sendSec();
        if (s == 0) {
            if (param.getInterval() == 60) {
                onTime("" + m);
            }else{
            	if(m % 5 == 0){
            		onTime("" + m);
            	}
            }
        }
    }

    private void createTimeSec(){
        sendSec();
        if(param.getInterval() == 1){
        	onTime("" + sec);
        }else{
        	if(sec%10 == 0){
        		onTime("" + sec);
        	}
        }
    }

    private void resetParam() {
        param.resetParam();
        toActivity(param);
        stopTimer();
        onTimeUp();
    }
    
    
}


