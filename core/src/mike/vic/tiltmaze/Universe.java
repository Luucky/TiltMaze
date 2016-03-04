package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

/**
 * Created by Mike on 2016-03-03.
 */
public class Universe extends TiltGame {
    static public btDynamicsWorld dynamicsWorld;
    static public ModelBuilder builder;
    private Environment environment;
    private ModelBatch modelBatch;
    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private ContactOverseer contactListener;
    private btBroadphaseInterface broadphase;
    private btConstraintSolver constraintSolver;

    private PerspectiveCamera cam;
    private CameraInputController camController;

    private Marble marble;
    private Maze maze;

    public Universe() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));
        contactListener = new ContactOverseer();

        builder = new ModelBuilder();
      //  Marble.createModel(100f);
        marble = new Marble(10f, 0.1f);
//        marble.transform.setTranslation(0, 100, 0);
        maze = new Maze(200f, 5f, 250f, 0);

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = 1f;
        cam.far = 1000f;
        cam.position.set(0, 300, 0);
        cam.lookAt(0, 0, 0);
        cam.update();
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(camController));
    }

    public void render() {
        final float delta = Math.min(1f / 10f, Gdx.graphics.getDeltaTime());
        dynamicsWorld.stepSimulation(delta, 5, 1f / 30f);

        Matrix4 tmp = new Matrix4();
        maze.motionState.getWorldTransform(tmp);
        marble.updateGravity();


        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(marble, environment);
        modelBatch.render(maze, environment);
        modelBatch.end();

        camController.update();
    }

    public void dispose () {

        marble.dispose();
        maze.dispose();

        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();

        contactListener.dispose();

        modelBatch.dispose();
    }
}
