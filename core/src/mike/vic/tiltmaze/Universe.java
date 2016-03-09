package mike.vic.tiltmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;

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

    private Marble marble;
    private Maze maze;

    Array<Entity> walls;

    class ContactOverseer extends ContactListener {
        @Override
        public boolean onContactAdded(int userValue0, int partId0, int index0, boolean match0, int userValue1, int partId1, int index1, boolean match1) {
            if (userValue0 == 99) {
                marble.updateGravity();
            }
            if (userValue1 == 99) {
                marble.updateGravity();
            }
            return true;
        }
    }

    public Universe(PerspectiveCamera camera) {
        cam = camera;

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
        maze = new Maze(16f, 1, 26f, 30, 35);
        marble = new Marble(0.3f, 0.1f);
//        marble.transform.setTranslation(0, 100, 0);
//        btCollisionShape shape = new btBoxShape(new Vector3(16 / 2, 1, 1 / 2));
//        walls.add(new Entity(Universe.builder.createBox(16, 2, 1, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.YELLOW)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
//                shape, 0));
//        walls.peek().transform.trn(0, 0.5f, -13);
//        walls.add(walls.peek());
//        walls.peek().transform.trn(0, 0.5f, 13);
//        shape = new btBoxShape(new Vector3(1 / 2, 1, 26 / 2));
//        walls.add(new Entity(Universe.builder.createBox(1, 2, 26, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.YELLOW)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
//                shape, 0));
//        walls.peek().transform.trn(-8, 0.5f, 0);
//        walls.add(walls.peek());
//        walls.peek().transform.trn(8, 0.5f, 0);


//        for (int i = 0; i < 30; i++)
//            for(int j = 0; j < 30; j++) {
//                walls.add(new Entity(Universe.builder.createBox( 10 / 30, 1, 10 / 30, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.YELLOW)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
//                        shape, 0));
//                walls.peek().transform.trn((10 / 30) * (i - 15), 3,(10 / 30) * (j - 15));
//                walls.peek().body.setUserValue(9);
//                walls.peek().body.setCollisionFlags(walls.peek().body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
//                walls.peek().body.setActivationState(Collision.ACTIVE_TAG);
//            }
    }



    public void render() {
        final float delta = Math.min(1f / 60f, Gdx.graphics.getDeltaTime());
        dynamicsWorld.stepSimulation(delta, 5, 1f / 120f);

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(marble, environment);
        modelBatch.render(maze.renderEntities(), environment);
        modelBatch.end();
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
