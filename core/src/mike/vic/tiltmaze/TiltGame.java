package mike.vic.tiltmaze;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class TiltGame extends InputAdapter implements ApplicationListener {

    ExtendViewport viewport;

    private Accelerometer accelerometer;
    private Universe universe;

    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;

    float xRot = 0;
    float[] camPos;
    Vector3 gravity;
    @Override
    public void create () {
        Bullet.init();

        accelerometer = new Accelerometer(0.1f);

        gravity = new Vector3();
        universe = new Universe();

        stage = new Stage();
        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(label);
        stringBuilder = new StringBuilder();


    }

    public Vector3 upVector(Matrix4 pos) {
        float[] tmp = pos.getValues();
        return new Vector3(tmp[Matrix4.M01], tmp[Matrix4.M11], tmp[Matrix4.M21]);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {

        xRot += 1;

        return true; // return true to indicate the event was handled
    }

//    public void sphericalCamera(float radius, float polar, float elevation) {
//        float a = radius * MathUtils.cos(elevation);
//        cam.position.x = radius * MathUtils.sin(elevation);
//        cam.position.y = a * MathUtils.cos(polar);
//        cam.position.z = a * MathUtils.sin(polar);
//    }



    @Override
    public void render () {
        //sphericalCamera(200f,  ((accelY / 10) * (MathUtils.PI / 2)), (MathUtils.PI / 2) +  ((accelX / 10) * (MathUtils.PI / 2)) );//-((accelY / 10) * (MathUtils.PI / 2))); //(MathUtils.PI / 2) + ((accelX / 10) * (MathUtils.PI / 2))
        //cam.position.set(upVector(instances.get(0).transform).scl(200f));
        //cam.direction.set(upVector(instances.get(0).transform).scl(-1f));
        //cam.direction.
        // cam.update();


        //rotation.setEulerAngles(0, ((((accelerometer.axisY() * 10) / 10f) / 10) * 90), ((((accelerometer.axisX() * 10) / 10f) / 10) * 90));

        //contactMatrix = new Matrix4(instances.get(1).transform);
        //contactMatrix.setTranslation(contactMatrix.getTranslation(new Vector3(0, 0, 0).sub(upVector(instances.get(0).transform))));
        //instances.get(0).transform.idt().rotate(rotation);
        //nstances.get(0).body.setCenterOfMassTransform(contactMatrix);

        //System.out.println(instances.get(0).body.get);
        //instances.get(0).body.setActivationState(Collision.DISABLE_DEACTIVATION);
        accelerometer.smoothing();
        universe.render();


        stringBuilder.setLength(0);
        gravity.set(Accelerometer.axisX(), 0, Accelerometer.axisY());
                stringBuilder.append("").append(gravity);
        label.setFontScale(3);
        label.setText(stringBuilder);
        stage.draw();
    }

    @Override
    public void dispose() {
        universe.dispose();
    }



    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void resize (int width, int height) {
    }
}

//boolean available = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer); checking accelerometer availability