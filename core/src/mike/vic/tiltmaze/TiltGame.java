package mike.vic.tiltmaze;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class TiltGame extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	BitmapFont font;
	FreeTypeFontGenerator generator;
	FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<>";
	CharSequence str = "";
	World world;
//	Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	FixtureDef fdef;
	Fixture fixture;
	BodyDef bdeff;
	Body body;

	CircleShape ball;

	@Override
	public void create () {
		Box2D.init();
		batch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 22;
		parameter.characters = FONT_CHARACTERS;
		font = generator.generateFont(parameter);
		generator.dispose();

		world = new World(new Vector2(0, -10), true);

		bdeff  = new BodyDef();
		bdeff.type = BodyType.DynamicBody;
		bdeff.position.set(50, 400);
		body = world.createBody(bdeff);

		ball = new CircleShape();
		ball.setRadius(6f);
		System.out.println(ball.getType());

		fdef = new FixtureDef();
		fdef.shape = ball;
		fdef.density = 0.5f;
		fdef.friction = 0.4f;
		fdef.restitution = 0.6f;

		fixture = body.createFixture(fdef);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		shapeRenderer = new ShapeRenderer();

		ball.dispose();
	}

	ShapeRenderer shapeRenderer;

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);


		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.circle(body.getPosition().x, body.getPosition().y,  6f);
		shapeRenderer.end();

		batch.begin();
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		//font.draw(batch, "some string", 25, 160);
		batch.end();
		//debugRenderer.render(world, camera.combined);
		world.step(1 / 60f, 6, 2);
		//if (Gdx.input.getAccelerometerX())

	}
}

