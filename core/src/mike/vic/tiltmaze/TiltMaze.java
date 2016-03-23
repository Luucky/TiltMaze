package mike.vic.tiltmaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.bullet.Bullet;


public class TiltMaze extends Game {
    static public final int WIDTH = 600;
    static public final int HEIGHT = 1024;

    private Accelerometer accelerometer;

    public TiltGame universeScreen;

    @Override
    public void create () {
        Bullet.init();
        //Settings.load();
        Assets.load();
        TiltTimer.initialize();

        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            //dialog saying you need accelerometer
        }
        accelerometer = new Accelerometer(0.1f);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);



        if (universeScreen == null && Assets.loaded()) { // when assets loaded, it will execute once
            universeScreen = new TiltGame(); // creates an instance of the game screen to achieve the menu with 3d world in the background effect
            setScreen(new MenuScreen(this));
        } else universeScreen.render(Gdx.graphics.getDeltaTime());
        accelerometer.smoothing();
        super.render();
    }
}
