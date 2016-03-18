package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;





/**
 * Created by bobby_000 on 16/03/2016.
 */
public class StartScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH =224; // size of the image (aspect ratio for the image)
    private static final float WORLD_HEIGHT = 224;
    private Texture SingleTexture;
    private Texture SinglePressTexture;
    private Stage stage;
    private Texture backgroundTexture;
    private Image image;
    private OrthographicCamera camera;
    private FillViewport viewport;
    private MyGdxGame game;

    public StartScreen(MyGdxGame game){
        this.game=game;
    }

    public void show()
    {
        viewport = new FillViewport(WORLD_WIDTH,WORLD_HEIGHT);// it stretches the image to fit the screen
        camera=new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        stage = new Stage();
        stage.setViewport(viewport);

        // stage = new Stage(new FitViewport(world_width,world_height ));
        Gdx.input.setInputProcessor(stage); //allow to fire input to child actors
        backgroundTexture = new Texture(Gdx.files.internal("wood.jpg"));

        image =new Image(backgroundTexture);
        stage.addActor(image);

        SingleTexture = new Texture(Gdx.files.internal("Play.png"));
        SinglePressTexture = new
                Texture(Gdx.files.internal("playPressed.png"));



        ImageButton singlePlayer = new ImageButton(new TextureRegionDrawable(new
                TextureRegion(SingleTexture)), new TextureRegionDrawable(new
                TextureRegion(SinglePressTexture)));
        stage.addActor(singlePlayer);

        singlePlayer.setPosition(WORLD_WIDTH/2,WORLD_HEIGHT/2, Align.center); // place the button at the center



        singlePlayer.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count,
                            int button) {
                super.tap(event,x,y,count,button);
                Gdx.app.log("click", "performed");
                // action performed when clicked
                game.setScreen(new TiltGame(game));
            }
        });



    }
    public void render(float delta)
    {
        stage.act(delta);
        stage.draw();
    }
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
    public void dispose(){
        stage.dispose();
        SingleTexture.dispose();
        SinglePressTexture.dispose();
        backgroundTexture.dispose();
    }
}

