package jp.co.nirvana0rigin.timerspeaker2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

public class MainActivity
        extends AppCompatActivity
        implements Config.OnConfigListener, GoConfig.OnGoConfigListener, Reset.OnResetListener, Start.OnStartListener, Sync.OnSyncListener {

    private Context con;
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
    private Speak speak;
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
    public void onStop() {
        b.putSerializable(PARAM, param);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        if(!param.isReset() && !param.isHalfwayStopped()){
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
    public void onSyncRestart(){



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



}
