package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by bobby_000 on 16/03/2016.
 */
public class MenuScreen extends ScreenAdapter {
    private Texture background;
    private Texture play;
    private Texture playPressed;
    private TextButton playButton;

    Camera camera;
    Viewport viewport;
    private Stage stage;
    TiltMaze game;

    private class Splash extends Actor {
        private Texture credits;

        Splash() {
            credits = new Texture("splashSprite.png");
            setRotation(90);
            addAction(fadeOut(6));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
            batch.draw(credits, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth() / 2, credits.getHeight() / 2, credits.getWidth() / 2, credits.getHeight(), credits.getWidth(), 2, 2, 90, 0, 0, credits.getHeight(), credits.getWidth(), false, false);
        }
    }

    public MenuScreen(TiltMaze g) {
        game = g;

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("data/BeTrueToYourSchool.ttf"));
        FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = 20;
        BitmapFont tiltFont = fontGenerator.generateFont(fontParameters); // font size 12 pixels
        fontGenerator.dispose();

        background = new Texture(Gdx.files.internal("wood.jpg"));
        play = new Texture("plays.png");
        playPressed = new Texture(Gdx.files.internal("playPressed.png"));


        viewport = new ScreenViewport();
        Skin skin =  new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage); //allow to fire input to child actors

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);

        playButton = new TextButton("Play", skin, "toggle");
        //playButton = new TextButton("", skin.get("playButtonUp", TextButton.TextButtonStyle.class));
        playButton.getStyle().font = tiltFont;
        playButton.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
        //playButton.setTransform(true);
        //playButton.setFillParent(true);
        //playButton.setRotation(90);
        //playButton.setScale(3.5f);
        //playButton.setOrigin();
        playButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                game.setScreen(new TiltGame(game));
            }
        });

        table.add(playButton).width(Gdx.graphics.getWidth() / 3).height(Gdx.graphics.getHeight() / 8);
        stage.addActor(table);
        stage.addActor(new Splash());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
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

