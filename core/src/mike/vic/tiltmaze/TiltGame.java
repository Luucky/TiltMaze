package mike.vic.tiltmaze;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.omg.CORBA.MARSHAL;

public class TiltGame extends ScreenAdapter {
    TiltMaze game;

    Viewport screen;
    PerspectiveCamera cam;
    private CameraInputController camControllers;

    private Universe universe;

    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;


    public TiltGame() {
        cam = new PerspectiveCamera(50, TiltMaze.WIDTH, TiltMaze.HEIGHT);
        cam.near = 1f;
        cam.far = 1000f;
        cam.update();
        camControllers = new CameraInputController(cam);
        //Gdx.input.setInputProcessor(new InputMultiplexer(camController));

        screen = new FillViewport(TiltMaze.WIDTH, TiltMaze.HEIGHT);
        stage = new Stage(screen);

        universe = new Universe(cam);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("BeTrueToYourSchool.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 60;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        label = new Label("", new Label.LabelStyle(font, Color.WHITE));
        label.setPosition(font.getSpaceWidth() * 2, TiltMaze.HEIGHT - font.getCapHeight());
        stage.addActor(label);
        stringBuilder = new StringBuilder();
    }

    public TiltGame showScreen(TiltMaze gam) {
        game = gam;
        universe.genesis();
        return this;
    }

    @Override
    public void render(float delta) {
        universe.update();
 //       camController.update();

        if (game != null) {
            stringBuilder.setLength(0);

            stringBuilder.append(Timer.getNow(false));
            if (Timer.getNow(false) > 100)
                stringBuilder.setLength(6);
            else if (Timer.getNow(false) > 10)
                stringBuilder.setLength(5);
            else stringBuilder.setLength(4);
            stringBuilder.append("'");
            label.setText(stringBuilder);
        }
        stage.draw();
    }

    @Override
    public void dispose() {
        universe.dispose();
        stage.dispose();
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

