package mike.vic.tiltmaze;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TiltGame extends InputAdapter implements Screen {

    private Viewport screen;
    private PerspectiveCamera cam;
    private CameraInputController camController;

    private Accelerometer accelerometer;
    private Universe universe;

    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;
    Vector3 gravity;
    MyGdxGame game;

    TiltGame(MyGdxGame game){
        this.game=game;

    }





    public Vector3 upVector(Matrix4 pos) {
        float[] tmp = pos.getValues();
        return new Vector3(tmp[Matrix4.M01], tmp[Matrix4.M11], tmp[Matrix4.M21]);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {


        return true; // return true to indicate the event was handled
    }

//    public void sphericalCamera(float radius, float polar, float elevation) {
//        float a = radius * MathUtils.cos(elevation);
//        cam.position.x = radius * MathUtils.sin(elevation);
//        cam.position.y = a * MathUtils.cos(polar);
//        cam.position.z = a * MathUtils.sin(polar);
//    }



    @Override
    public void show() {

        Bullet.init();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = 1f;
        cam.far = 1000f;
        cam.position.set(0, 17, 0);
        cam.lookAt(0, 0, 0);
        cam.update();
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(camController));

        screen = new ScreenViewport();
        stage = new Stage(screen);

        accelerometer = new Accelerometer(0.1f);

        gravity = new Vector3();
        universe = new Universe(cam);

        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(label);
        stringBuilder = new StringBuilder();
       // game.setScreen(new TiltGame(game));

    }

    @Override
    public void render(float delta) {
        accelerometer.smoothing();
        universe.render();

        camController.update();

        stringBuilder.setLength(0);
        gravity.set(Accelerometer.axisX(), 0, Accelerometer.axisY());
        stringBuilder.append("").append(gravity);
        label.setFontScale(3);
        label.setText(stringBuilder);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        universe.dispose();
        font.dispose();
    }



}

//boolean available = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer); checking accelerometer availability