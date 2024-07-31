package fr.alcyons.phiwms_mobile;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Handler;
import android.util.Log;

import java.util.logging.LogRecord;

public class MinuteurView extends AppCompatTextView {

    private int minutes;
    private int secondes;
    private int startMinutes;
    private int startSecondes;
    private boolean continuer;
    private Runnable onEndOfTimer;
    private Handler handler;
    private Runnable runnable;

    public MinuteurView(@NonNull Context context) {
        super(context);
        continuer = true;
        init();
    }

    public MinuteurView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        continuer = true;
        init();
    }

    public MinuteurView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        continuer = true;
        init();
    }

    private void init(){
        handler = new Handler() ;
        runnable = new Runnable() {
            @Override
            public void run() {
                updateTimer();
                if (! (secondes == 0 && minutes == 0) && continuer){
                    handler.postDelayed(runnable, 1000);
                }
            }
        };
        String timeText = "00:00";
        setText(timeText);
    }

    private void updateTimer(){
        if (minutes == 0 && secondes == 1){
            secondes = 0;
            if (onEndOfTimer != null){
                onEndOfTimer.run();
            }
        }
        else {
            if (secondes == 0){
                secondes = 59;
                minutes = minutes - 1;
            }
            else {
                secondes = secondes - 1;
            }
        }
        String timeText = String.format("%02d:%02d", minutes, secondes);
        setText(timeText);
    }

    public void startTimer(int minutes, int secondes, Runnable toRunOnEnd){
        if (toRunOnEnd != null){
            this.onEndOfTimer = toRunOnEnd;
        }
        startMinutes = minutes;
        this.minutes = minutes;
        startSecondes = secondes;
        this.secondes = secondes;
        continuer = true;
        handler.postDelayed(runnable, 1000);
        String timeText = String.format("%02d:%02d", minutes, secondes);
        setText(timeText);
    }

    public void stopTimer(){
        Log.d("test", "stopTimer appelé");
        handler.removeCallbacks(runnable);
        continuer = false;
        setText("00:00");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
