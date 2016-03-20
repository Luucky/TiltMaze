package mike.vic.tiltmaze;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TiltGame extends ScreenAdapter {
    private Viewport screen;
    private PerspectiveCamera cam;
    private CameraInputController camController;

    private Universe universe;

    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;

    TiltMaze game;

    public TiltGame(TiltMaze g) {
        game = g;

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = 1f;
        cam.far = 1000f;
        cam.position.set(0, 17, 0);
        cam.lookAt(0, 0, 0);
        cam.update();
        //camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer());

        screen = new ScreenViewport();
        stage = new Stage(screen);

        universe = new Universe(cam);

        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(label);
        stringBuilder = new StringBuilder();
    }

    @Override
    public void render(float delta) {
        universe.update();

        //camController.update();

        stringBuilder.setLength(0);
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

//    public Vector3 upVector(Matrix4 pos) {
//        float[] tmp = pos.getValues();
//        return new Vector3(tmp[Matrix4.M01], tmp[Matrix4.M11], tmp[Matrix4.M21]);
//    }

//    public void sphericalCamera(float radius, float polar, float elevation) {
//        float a = radius * MathUtils.cos(elevation);
//        cam.position.x = radius * MathUtils.sin(elevation);
//        cam.position.y = a * MathUtils.cos(polar);
//        cam.position.z = a * MathUtils.sin(polar);
//    }
