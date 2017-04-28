package com.week1.practice1.morsecodeflashlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    public static String [][] morse = {{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"," "},{"012","2101010","2101210","21010","0","010120","21210","0101010","010","0121212","21012","0121010","212","210","21212","0121210","2121012","01210","01010","2","01012","0101012","01212","2101012","2101212","2121010","111"}};
    public boolean flash = true;
    public Camera camera = Camera.open();
    public Parameters p = camera.getParameters();

    protected void init(){

        SeekBarAction();
        ButtonAction();

        TextView messsage = (TextView)findViewById(R.id.message);

        SendMessage();


    }
    protected int roundToNearest(int x) {
        if (x%50 < 25) {
            return x - (x%50);
        }
        else if (x%50 > 25) {
            return x + (50 - (x%50));
        }
        else if (x%50 == 25) {
            return x + 25; //when it is halfawy between the nearest 50 it will automatically round up, change this line to 'return x - 25' if you want it to automatically round down
        }

        return 0;
    }

    protected void SeekBarAction(){
        SeekBar sBar = (SeekBar)findViewById(R.id.timeUnitSeek);
        sBar.setProgress(0);
        sBar.setMax(150);

        final TextView sBarValue = (TextView)findViewById(R.id.timeUnitLabel);

        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar sBar, int progress, boolean fromUser) {

                int set = sBar.getProgress();

                set = roundToNearest(set);
                sBar.setProgress(set);
                double current = sBar.getProgress();
                sBarValue.setText("1 Time Unit = " + Double.toString(Math.round((current/1000 + 0.1) * 20.0) / 20.0) + "s");
            }
            @Override
            public void onStartTrackingTouch (SeekBar sBar){

            }

            @Override
            public void onStopTrackingTouch (SeekBar sBar){

            }
        });
    }
    protected void ButtonAction(){
        Button btn = (Button)findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MorseCode();
            }
        });

    }
    protected void SendMessage() {
        final EditText tv = (EditText)findViewById(R.id.message);
        final int n = 0;
        tv.setOnClickListener(new EditText.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (n == 0) {
                    tv.setText("");
                }
            }
        });

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && !Character.isSpace(source.charAt(i))) {
                        return "";
                    }
                    else{
                        return Character.toString(Character.toUpperCase(source.charAt(i)));
                    }
                }
                return null;
            }
        };

        tv.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        tv.setFilters(new InputFilter[] {filter,new InputFilter.LengthFilter(10)});
    }


    protected void ChangeTorchState(){
        if(flash){
            TorchOn();
            flash = false;
        }
        else{
            TorchOff();
            flash = true;
        }

    }
    protected void TorchOn(){
        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        camera.startPreview();
    }
    protected void TorchOff(){
        p.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(p);
        camera.stopPreview();
    }

    public void MorseCode(){
        SeekBar SBar = (SeekBar)findViewById(R.id.timeUnitSeek);
        int time = (SBar.getProgress() + 100);
        EditText ET = (EditText)findViewById(R.id.message);

        final String[] converted = {ConvertToMorse(ET.getText().toString())};
        int i = 0;

        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run(){
                if(converted[0].equals(".")){
                    timer.cancel();
                    timer.purge();
                }
                converted[0] = SingleOperation(converted[0]);
        }};


        timer.scheduleAtFixedRate(task,time,time);
    }

    public String SingleOperation(String txt){

        String sw;

        if(txt.equals(".")){return ".";}

        else {
            sw = txt.substring(0, 2);
            txt = txt.substring(1);

            if (sw.equals(".=") || sw.equals("=.")) {
                ChangeTorchState();
            }
            Log.d(TAG, "TXT: " + txt);
            Log.d(TAG, "SW: " + sw);
            return txt;
        }
    }

    public String ConvertToMorse(String text){;
        String a = ".";
        for(int i = 0; i < text.length(); i++){
            for(int k = 0; k < morse[0].length;k++){
                if((Character.toString(text.charAt(i))).equals(morse[0][k])) {
                    a = a + "" + morse[1][k];
                }

            }
            a+=".";
        }

        a = a.replaceAll("1",".");
        a = a.replaceAll("0","=");
        a = a.replaceAll("2","===");

        Log.d(TAG, "ConvertToMorse: " + a);
        return a;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
}
