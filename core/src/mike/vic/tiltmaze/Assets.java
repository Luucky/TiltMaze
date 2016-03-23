package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

/**
 * Created by Mike on 2016-03-20.
 */
public class Assets {
    static public ModelLoader loader;
    static private boolean loading;

    static public Model skydome;

    static public Texture metal;
    static public Texture lightWood;
    static public Texture darkWood;

    static public Texture splashScreen;
    static public Texture play;
    static public Texture playPressed;

    static public Texture start;
    static public Texture startPressed;
    static public Texture sliderBar;
    static public Texture sliderKnob;
    static public Texture settingsBoard;

    static public Texture proceed;
    static public Texture proceedPressed;

    static public BitmapFont timeFont;
    static public BitmapFont settingsFont;

    static public Texture loadTexture(String file) {
        return new Texture(Gdx.files.internal(file));
    }

    static public void load() {
        loader = new ObjLoader();
        loading = true;

        metal = loadTexture("materials/metal.jpg");
        lightWood = loadTexture("materials/wood-light.jpg");
        darkWood = loadTexture("materials/woodgrain-dark.jpg");

        splashScreen = loadTexture("splash.png");
        play = loadTexture("buttons/plays.png");
        playPressed = loadTexture("buttons/playPressed.png");

        start = loadTexture("buttons/start.png");
        startPressed = loadTexture("buttons/startPressed.png");
        sliderBar = loadTexture("slider-bar.png");
        sliderKnob = loadTexture("slider-knob.png");
        settingsBoard = loadTexture("settingsBoard.png");

        proceed = loadTexture("buttons/proceed.png");
        proceedPressed = loadTexture("buttons/proceedPressed.png");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("rosewood.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        timeFont = generator.generateFont(parameter);
        parameter.size = 40;
        settingsFont = generator.generateFont(parameter);
        generator.dispose(); //ispose to avoid memory leaks!
    }

    public static boolean loaded() {
        if (loading && skydome == null) {
            skydome = loader.loadModel(Gdx.files.internal("scene/m16_ex.obj"));
            loading = false;
        }
        return !loading;
    }
}
