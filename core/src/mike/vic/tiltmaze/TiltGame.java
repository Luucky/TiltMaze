package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TiltGame extends ScreenAdapter {
    TiltMaze game;

    Viewport screen;
    PerspectiveCamera cam;
    private CameraInputController camControllers;

    private Universe universe;

    private Stage stage;
    Label timer, countdown;
    private StringBuilder stringBuilder;

    Slider slider;
    Label mazeValues;

    public TiltGame() {
        cam = new PerspectiveCamera(50, TiltMaze.WIDTH, TiltMaze.HEIGHT);
        cam.near = 1f;
        cam.far = 1000f;
        cam.update();
        camControllers = new CameraInputController(cam);

        screen = new FillViewport(TiltMaze.WIDTH, TiltMaze.HEIGHT);
        stage = new Stage(screen);

        universe = new Universe(cam);

        LabelStyle timeStyle = new LabelStyle(Assets.timeFont, Color.WHITE);
        timer = new Label("", timeStyle);
        timer.setPosition(Assets.timeFont.getSpaceWidth() * 2, TiltMaze.HEIGHT - 28);

        countdown = new Label("", timeStyle);
        countdown.setPosition(TiltMaze.WIDTH / 2, TiltMaze.HEIGHT / 2, Align.center);

        stringBuilder = new StringBuilder();
        stage.addActor(timer);
        stage.addActor(countdown);
    }

    public TiltGame showScreen(TiltMaze g, int mazeSizeAdjust) {
        game = g;

        universe.genesis(mazeSizeAdjust);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                universe.zoomSwitch();
                return true;
            }
        });

        return this;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        universe.update();

        if (game != null) {
            stringBuilder.append(TiltTimer.getNow(false));
            if (TiltTimer.getNow(false) > 100)
                stringBuilder.setLength(6);
            else if (TiltTimer.getNow(false) > 10)
                stringBuilder.setLength(5);
            else stringBuilder.setLength(4);

            if (TiltTimer.getNow(false) > 0) {
                stringBuilder.append("'");
                timer.setText(stringBuilder);
            }
            stringBuilder.setLength(0);

            if (TiltTimer.getNow(false) < 0) {
                stringBuilder.append(-TiltTimer.getNow(false));
                stringBuilder.setLength(1);
                countdown.setText(stringBuilder);
            } else if (TiltTimer.getNow(false) < 3) {
                stringBuilder.append("Go!");
                countdown.setPosition(TiltMaze.WIDTH / 2, TiltMaze.HEIGHT / 2, Align.center);
                countdown.setText(stringBuilder);
            } else countdown.setText(stringBuilder);
            stringBuilder.setLength(0);
        }
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        universe.dispose();
        stage.dispose();
    }

    @Override
    public void pause () {
        universe.freeze();
        TiltTimer.getNow(true);
        game.setScreen(new PauseScreen(game));
    }

    @Override
    public void resume() {
        TiltTimer.start();
        universe.unfreeze();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                universe.zoomSwitch();
                return true;
            }
        });
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

