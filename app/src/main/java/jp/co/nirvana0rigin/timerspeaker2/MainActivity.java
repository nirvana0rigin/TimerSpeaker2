package jp.co.nirvana0rigin.timerspeaker2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.Calendar;

public class MainActivity
        extends AppCompatActivity
        implements Config.OnConfigListener, GoConfig.OnGoConfigListener, Reset.OnResetListener, Start.OnStartListener, Sync.OnSyncListener {

    private static Context con;
    private Resources res;
    private Bundle b ;
    private FragmentManager fm;

    private static Param param ;
    private static final String PARAM = "param";

    private Counter counter;
    private Info info;
    private Start start;
    private GoConfig goConfig;
    private Reset reset;
    private Config config;
    private static Speak speak;
    private static Sync sync;
    private LinearLayout base;

    private Fragment[] fragments ;
    private int[] fragmentsID;
    private String[] fragmentsTag = {"counter","info","start","reset","go_config"};
    /*
        0: counter
        1: info
        2: start
        3: go_config
        4: reset
     */
	private static AlarmManager alarm;
    private static PendingIntent pen;






    //________________________________________________________for life cycles

    //リソース生成のみ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();
        con = getApplicationContext();
        b = savedInstanceState;
        fm = getSupportFragmentManager();

        if (b != null) {
            param = (Param)b.getSerializable(PARAM);
        } else {
            b = new Bundle();
            if(sync != null){
                param = sync.param;
            }else{
                param = new Param(1,1,0,0,false,true,300*1000);
            }
            b.putSerializable(PARAM, param);
        }

        int[] fragmentsID2 = {R.id.counter, R.id.info, R.id.start, R.id.reset, R.id.go_config};
        fragmentsID = fragmentsID2;

    }

    //描画生成
    @Override
    public void onStart() {
    	Log.d("_______main_____","onStart");
        super.onStart();
        if(sync == null) {
            param = (Param)b.getSerializable(PARAM);
        }else {
            if (sync != null) {
                param = sync.param;
            } else {
                param = new Param(1, 1, 0, 0, false, true, 300);
            }
        }
        createMainFragments();
        addMainFragments() ;
    }

	@Override
    public void onResume() {
    	Log.d("_______main_____","onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
    	Log.d("_______main_____","onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
    	Log.d("_______main_____","onStop");
        b.putSerializable(PARAM, param);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    	Log.d("_______main_____","onSave");
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onDestroy() {
    	Log.d("_______main_____","onD");
        super.onDestroy();
    }








    //___________________________________________________for connection on Fragments

    @Override
    public void onConfig(){
        //NOTHING
    }

    @Override
    public void onConfigBackButton(){
        createMainFragments();
        addMainFragments() ;
    }

    @Override
    public void onGoConfig() {
        removeMainFragments();
    }

    @Override
    public void onReset() {
        if (sync != null) {
            sync.stopTimer();
        }
        if( counter != null ){
            counter.resetCounterText();
        }
        if (goConfig != null) {
            goConfig.addButton();
        }
        if (reset != null) {
            reset.removeButton();
        }
    }

    @Override
    public void onStartButton() {
        if(param.isRunning()){
            goConfig.removeButton();
            reset.removeButton();
            speak.speakMinute("start");
        }else if(!param.isReset() && param.isHalfwayStopped()){
            reset.addButton();
        }else if (param.isReset() && param.isHalfwayStopped()) {
            //スレッド終了まではここではしないので、
            //上２つ以外の変更（この３つ目）はここではありえない。
            //スレッド終了に関してはresetにて。
        }
        if(!param.isHalfwayStopped()){
            sync.startTimer();
        }else{
            sync.stopTimer();
        }
    }

    @Override
    public void onSyncParam(Param p) {
        param = p;
        b.putSerializable(PARAM, param);
    }

    @Override
    public void onSyncSec() {
        counter.createCounterText();
    }

    @Override
    public void onSyncMin(String min) {
        addNewSpeakFragment();
        if (speak.getLang() < 2) {
            speak.speakMinute(min);
        } else if (speak.getLang() == 2) {
            //NOTHING
        } else {
            removeFragment(speak, "speak");
            speak = null;
            addNewSpeakFragment();
        }
    }

    @Override
    public void onSyncTimeUp(){
        reset.addButton();
        start.startButtonStatus();
    }

    @Override
    public void onSyncRestart(boolean run){
    	if(run){
    		sync.param = param;
    		alarmStop();
    	}else{
            Log.d("_______main_____","s->m,onSyncRestart,FALSE");
    		long d = param.getDelay();
            AlarmManager am = alarmCreate();
            Calendar cal = getCalendar(d);
            PendingIntent pen =  getPending(d);
    		alarmStart(am,cal,pen);
    	}
    }








    //___________________________________________________________for work on Activity

    private void createMainFragments() {
        addNewSyncFragment();
        addNewSpeakFragment();
        if(counter == null) {
            counter = new Counter();
        }
        if (info == null) {
            info = new Info();
        }
        if (start == null) {
            start = new Start();
        }
        if (goConfig == null) {
            goConfig = new GoConfig();
        }
        if (reset == null) {
            reset = new Reset();
        }
        Fragment[] fragments2 = {counter, info, start, reset,goConfig};
        fragments = fragments2;
    }

    private void addMainFragments(){
        FragmentTransaction transaction = fm.beginTransaction();
        if(isAlive("config")){
            transaction.remove(config);
            config = null;
        }
        for (int i = 0; i < 5; i++) {
            if (!isAlive(fragmentsTag[i])) {
                transaction.add(fragmentsID[i], fragments[i], fragmentsTag[i]);
            }
        }
        transaction.commit();
    }

    private void removeMainFragments(){
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i=0; i<5; i++){
            if(isAlive(fragmentsTag[i]) ) {
                transaction.remove(fragments[i]);
            }
        }
        transaction.addToBackStack(null);

        if(!(isAlive("config")) ){
            if(config ==null) {
                config = new Config();
            }
            transaction.add(R.id.config, config, "config");
        }else{
            transaction.show(config);
        }
        transaction.commit();
    }

    private void addNewSpeakFragment() {
        if (!(isAlive("speak"))) {
            if (speak == null) {
                speak = new Speak();
            }
            fm.beginTransaction().add(speak, "speak").commit();
        }
    }

    private void addNewSyncFragment() {
        if (!(isAlive("sync"))) {
            if (sync == null) {
                sync = Sync.newInstance(param);
            }
            fm.beginTransaction().add(sync, "sync").commit();
        }
    }

    private void removeFragment(Fragment fra, String tag) {
        if (isAlive(tag)) {
            fm.beginTransaction().remove(fra).commit();
        }
    }

    private boolean isAlive(String tag){
        if(fm.findFragmentByTag(tag) == null){
            return false;
        }else{
            return true;
        }
    }




    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // スクリーンオフのまま、cpuのみ起こす
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "alarm");
            wl.acquire(5000);

            long delay = intent.getLongExtra("delay",0);
            long endingTime = param.getEndingTime() - param.getInterval() - delay;
            param.setEndingTime(endingTime);
            String min = "" + (param.getSec());
            if (speak == null) {
                speak = new Speak();
            }
            speak.speakMinute(min);
            alarm = alarmCreate();
            pen = getPending(0);
            alarmStart(alarm, getCalendar(0), pen);
        }
    }
    
    private static AlarmManager alarmCreate(){
        AlarmManager alarmManager = (AlarmManager)con.getSystemService(Context.ALARM_SERVICE);
        return alarmManager;
    }
    
    private static void alarmStart(AlarmManager am, Calendar cal, PendingIntent pen){
    	if(android.os.Build.VERSION.SDK_INT >= 19){
    		am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pen);  
    	}else{
    		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pen);  
    	}
    }
    
    private void alarmStop(){
        if(alarm != null) {
            alarm.cancel(pen);
        }
    }
    
    private static Calendar getCalendar(long delay){
    	Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.MILLISECOND, (int)(param.getInterval()*1000+delay));
        return cal;
    }
    
    private static PendingIntent getPending(long delay){
    	Intent intent = new Intent(con, AlarmReceiver.class);
    	intent.putExtra("delay",delay);
        PendingIntent pen = PendingIntent.getBroadcast(con, 0, intent, 0);
    	return pen;
    }
    











}
