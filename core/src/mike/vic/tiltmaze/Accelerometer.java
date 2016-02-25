package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mike on 2016-02-24.
 * This class is going to handle accelerometer data and smooth it out with exponential moving average.
     * alpha = 0 : data becomes 0
     * alpha = 0->1 : values towards zero give most smoothing, weakers towards 1
     * alpha = 1 : no smoothing, real dat

 */
public class Accelerometer {
    private Vector2 accelerometerData;

    private float alpha;
    private Vector2 oldData;

    public Accelerometer(float a) {
        alpha = a;
    }

    private Vector2 getReading(){
        return new Vector2(MathUtils.clamp(Gdx.input.getAccelerometerX(), -3, 3), MathUtils.clamp(Gdx.input.getAccelerometerY(), -3, 3));
    }

    public float axisX() {
        return accelerometerData.x;
    }

    public float axisY() {
        return accelerometerData.y;
    }

    public synchronized void update() {
        accelerometerData = getReading();
        if (oldData == null) {
            oldData = accelerometerData;
            return;
        }
        Vector2 tmp = accelerometerData;
        accelerometerData.x = oldData.x + alpha * (tmp.x - oldData.x);
        accelerometerData.y = oldData.y + alpha * (tmp.y - oldData.y);
        oldData = accelerometerData;
    }
}

//    public void accelerometer() {
//        if (accelData.size < 5) accelData.add());
//        else {
//            accelIndex = accelData.iterator();
//
//            while (accelIndex.hasNext()) {
//                accelX = accelY = 0;
//                MathUtils.
//                Vector2 point = (Vector2) accelIndex.next();
//                accelIndex.remove();
//                accelX += point.x;
//                accelY += point.y;
//            }
//            accelX /= 5;
//            accelY /= 5;
//        }
//    }
