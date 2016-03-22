package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

/**
 * Created by Mike on 2016-03-20.
 */
public class Assets {
    public static ModelLoader loader;
    private static boolean loading;

    public static Model skydome;

    public static Texture metal;
    public static Texture lightWood;
    public static Texture darkWood;

    public static Texture splashScreen;
    public static Texture play;
    public static Texture playPressed;

    public static Texture loadTexture(String file) {
        return new Texture(Gdx.files.internal(file));
    }

    public static void load() {
        loader = new ObjLoader();
        loading = true;

        metal = loadTexture("materials/metal.jpg");
        lightWood = loadTexture("materials/wood-light.jpg");
        darkWood = loadTexture("materials/woodgrain-dark.jpg");

        splashScreen = loadTexture("splashSprite.png");
        play = loadTexture("buttons/plays.png");
        playPressed = loadTexture("buttons/playPressed.png");
    }

    public static boolean loaded() {
        if (loading && skydome == null) {
            skydome = loader.loadModel(Gdx.files.internal("scene/m16_ex.obj"));
            loading = false;
        }
        return !loading;
    }
}
