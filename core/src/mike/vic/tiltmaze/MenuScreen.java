package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by bobby_000 on 16/03/2016.
 */
public class MenuScreen extends ScreenAdapter {
    ImageButton playButton;
    FillViewport viewport;
    private Stage stage;

    TiltMaze game;

    Splash splashScreen;
    TiltGame gameScreen;

    private class Splash extends Actor {
        Splash() {}

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
            batch.draw(Assets.splashScreen, TiltMaze.WIDTH / 2 - Assets.splashScreen.getWidth() / 2,
                                            TiltMaze.HEIGHT / 2 - Assets.splashScreen.getHeight() / 2);
        }
    }

    public MenuScreen(TiltMaze gam) {
        game = gam;
        viewport = new FillViewport(TiltMaze.WIDTH, TiltMaze.HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage); //allow to fire input to child actors

        playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.play)),
                                     new TextureRegionDrawable(new TextureRegion(Assets.playPressed)));
        playButton.setPosition(TiltMaze.WIDTH / 2 - playButton.getWidth() / 2, TiltMaze.HEIGHT / 4);
        playButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                game.setScreen(gameScreen.showScreen(game));
            }
        });
        playButton.addAction(fadeOut(0));

        stage.addActor(playButton);
        stage.addActor(splashScreen = new Splash());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (gameScreen == null && Assets.loaded()) {
            splashScreen.addAction(fadeOut(7));
            playButton.addAction(sequence(delay(7), fadeIn(1)));
            gameScreen = new TiltGame();
        }

        if (gameScreen != null) gameScreen.render(delta);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose(){
        stage.dispose();
    }
}

      /*  Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);*/
//playButton = new TextButton("", skin.get("playButtonUp", TextButton.TextButtonStyle.class));
//playButton.setTransform(true);
//playButton.setFillParent(true);
//playButton.setRotation(90);
//playButton.setOrigin();
//table.add(playButton).setActorX(Gdx.graphics.getWidth() / 2);