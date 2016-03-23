package mike.vic.tiltmaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.bullet.Bullet;


public class TiltMaze extends Game {
    // resolution used
    static public final int WIDTH = 600;
    static public final int HEIGHT = 1024;

    private Accelerometer accelerometer;

    public TiltGame universeScreen; // universe reachable through this class, which all screens require to reference for screen changing

    @Override
    public void create () {
        Bullet.init(); // initialize bullet physics

        Assets.load(); // load the dependencies/files/textures/models/scenes
        TiltTimer.initialize(); // initialize the timer

        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            //dialog saying you need accelerometer
        }
        accelerometer = new Accelerometer(0.1f); // initialize the accelerometer, specifying smoothing factor
    }

    @Override
    public void render() {
        // very important without this, the scene doesn't change
        Gdx.gl.glClearColor(0f, 0f, 0f, 0); //clear the screen to a black colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); // apply the color buffer and depth buffer to screen

        if (universeScreen == null && Assets.loaded()) { // when assets loaded, it will execute once
            universeScreen = new TiltGame(); // creates an instance of the universeScreen to achieve the menu with 3d world in the background effect
            setScreen(new MenuScreen(this)); // after universe created, proceed to main menu
            TiltTimer.initialize();
        } else universeScreen.render(Gdx.graphics.getDeltaTime()); // render all the time

        accelerometer.smoothing(); //accelerometer values are smoothed out and updated
        super.render();
    }
}
