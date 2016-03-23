package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;


/**
 * Created by Mike on 2016-03-23.
 */
public class PauseScreen extends ScreenAdapter {
    private Stage pauseStage;
    private ImageButton proceed;


    TiltMaze game;

    PauseScreen(TiltMaze g) {
        game = g;
        pauseStage = new Stage(new FillViewport(TiltMaze.WIDTH, TiltMaze.HEIGHT));
        Gdx.input.setInputProcessor(pauseStage);

        proceed = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.proceed)),
                new TextureRegionDrawable(new TextureRegion(Assets.proceedPressed)));
        proceed.setPosition(TiltMaze.WIDTH / 2 - proceed.getWidth() / 2, TiltMaze.HEIGHT / 4);

        proceed.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.universeScreen.resume();
                game.setScreen(game.universeScreen);
            }
        });

        pauseStage.addActor(proceed);
    }

    @Override
    public void render(float delta) {
        pauseStage.act(delta);
        pauseStage.draw();
    }
}
