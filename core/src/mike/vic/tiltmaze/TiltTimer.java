package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

public class TiltTimer {
    static private Timer timer;
    static private float time = 0f;
    static private boolean stopped;

    static public float getNow(boolean halt) {
        if (halt) {
            timer.stop();
            stopped = halt;
        }
        return time;
    }

    static public boolean getState() {
        return stopped;
    }

    static public void start() {
        timer.start();
        stopped = false;
    }

    static public void initialize() {
        timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                time += 0.01f;
            }
        }, 0, 0.01f);
        stopped = false;
    }

    static public void reset() {
        time = 0f;
    }

    static public void countdown() {
        time = -3f;
    }
}
