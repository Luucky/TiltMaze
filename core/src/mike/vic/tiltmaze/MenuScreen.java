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

    private class Splash extends Actor {
        Splash() {}

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
            batch.draw(Assets.splashScreen, TiltMaze.WIDTH / 2 - Assets.splashScreen.getWidth() / 2,
                                            TiltMaze.HEIGHT / 2 - Assets.splashScreen.getHeight() / 2);
        }
    }


    public MenuScreen(TiltMaze g) {
        game = g;
        viewport = new FillViewport(TiltMaze.WIDTH, TiltMaze.HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.play)),
                                     new TextureRegionDrawable(new TextureRegion(Assets.playPressed)));
        playButton.setPosition(TiltMaze.WIDTH / 2 - playButton.getWidth() / 2, TiltMaze.HEIGHT / 4);
        playButton.addAction(sequence(fadeOut(0), delay(6), fadeIn(1)));
        playButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) { // on tap event
                if (playButton.getActions().size == 0) // if play button has finished fading in
                    game.setScreen(new MazeSettingsScreen(game, viewport)); // brings you to the maze options screen
            }
        });


        stage.addActor(splashScreen = new Splash());
        splashScreen.addAction(sequence(delay(2),fadeOut(4)));
        stage.addActor(playButton);
    }

    @Override
    public void render(float delta) {


//            gameScreen = new TiltGame(); // creates an instance of the game screen to achieve the menu with 3d world in the background effect

        //if (gameScreen != null) gameScreen.render(delta); // the 3d world needs to be rendered to be visible, needs a check to avoid nullPointerException

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