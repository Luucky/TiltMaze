package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;

public class Timer {
    static private float time = 0f;
    static private boolean progression = true;

    static public float getNow(boolean halt) {
        progression = !halt;
        return time;
    }

    static public boolean getState() {
        return progression;
    }

    static public void reset() {
        time = 0f;
        progression = true;
    }

    static public void progress() {
        if (!progression) return;
        time += Gdx.graphics.getDeltaTime();
    }
}
