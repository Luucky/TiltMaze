package mike.vic.tiltmaze;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

public class TiltGame extends ApplicationAdapter {

    public PerspectiveCamera cam;
    public CameraInputController camController;
    public ModelBatch modelBatch;

    public Model model;

    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;

    public Environment environment;
    public Array<Entity> instances = new Array<Entity>();
    ArrayMap<String, Entity.Creator> creators;

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded (int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
            instances.get(userValue0).moving = false;
            instances.get(userValue1).moving = false;
            return true;
        }
    }
    static class Entity extends ModelInstance implements Disposable {
        static class Creator implements Disposable {
            public final Model model;
            public final String node;
            public final btCollisionShape shape;
            public Creator(Model m, String n, btCollisionShape s) {
                model = m;
                node = n;
                shape = s;
            }

            public Entity create() {
                return new Entity(model, node, shape);
            }

            @Override
            public void dispose () {
                shape.dispose();
            }
        }
        public final btCollisionObject body;
        public boolean moving;
        public Entity(Model model, String node, btCollisionShape shape) {
            super(model, node);
            body = new btCollisionObject();
            body.setCollisionShape(shape);
        }

        @Override
        public void dispose() {
            body.dispose();
        }
    }

    @Override
    public void create() {
        Bullet.init();

        environment = new Environment();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.2f, -.5f, -0f, -0f));

        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 0f, 102);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.node().id = "ball";
        modelBuilder.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(10, 10, 10, 100, 100);
        modelBuilder.node().id = "ground";
        modelBuilder.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED))).box(50, 50, 1);
        model = modelBuilder.end();
        creators = new ArrayMap<String, Entity.Creator>(String.class, Entity.Creator.class);
        creators.put("ball", new Entity.Creator(model, "ball", new btSphereShape(5)));
        creators.put("ground", new Entity.Creator(model, "ground", new btBoxShape(new Vector3(25, 25, 0.5f))));
        instance.transform.setToTranslation(0, 0, 10);
        instances.add(instance);
        instances.add(new ModelInstance(model, "ground", ));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
        System.out.println(instances.get(0).toString());
    }

    boolean collision = false;

    @Override
    public void render() {
        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
        final float delta = Math.min(1f/30f, Gdx.graphics.getDeltaTime());

        if (!collision) {
            instances.get(0).transform.translate(0, 0, -delta);
            ballObject.setWorldTransform(instances.get(0).transform);
            collision = checkCollision(ballObject, groundObject);
        }

    }

    boolean checkCollision(btCollisionObject obj0, btCollisionObject obj1) {
        CollisionObjectWrapper co0 = new CollisionObjectWrapper(obj0);
        CollisionObjectWrapper co1 = new CollisionObjectWrapper(obj1);

        btCollisionAlgorithm algorithm = dispatcher.findAlgorithm(co0.wrapper, co1.wrapper);

        btDispatcherInfo info = new btDispatcherInfo();
        btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);

        algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);

        boolean r = result.getPersistentManifold().getNumContacts() > 0;

        dispatcher.freeCollisionAlgorithm(algorithm.getCPointer());
        result.dispose();
        info.dispose();
        co1.dispose();
        co0.dispose();

        return r;
    }

    @Override
    public void dispose() {
        groundObject.dispose();
        groundShape.dispose();

        ballObject.dispose();
        ballShape.dispose();

        dispatcher.dispose();
        collisionConfig.dispose();

        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}

