package mike.vic.tiltmaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


public class TiltMaze extends Game {
    private Accelerometer accelerometer;

    @Override
    public void create () {
        Bullet.init();
        //Settings.load();
        //Assets.load();
        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {

        }

         accelerometer = new Accelerometer(0.1f);

        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        accelerometer.smoothing();
        super.render();
    }
}
