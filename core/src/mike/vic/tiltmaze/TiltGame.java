package mike.vic.tiltmaze;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class TiltGame extends InputAdapter implements ApplicationListener {
    final static short GROUND_FLAG = 1 << 8;
    final static short OBJECT_FLAG = 1 << 9;
    final static short ALL_FLAG = -1;

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded (int userValue0, int partId0, int index0, boolean match0, int userValue1, int partId1, int index1, boolean match1) {
            if (match0) ((ColorAttribute)instances.get(userValue0).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            if (match1) ((ColorAttribute)instances.get(userValue1).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            return true;
        }
    }

    static class MotionState extends btMotionState {
        Matrix4 transform;

        @Override
        public void getWorldTransform (Matrix4 worldTrans) {
            worldTrans.set(transform);
        }

        @Override
        public void setWorldTransform (Matrix4 worldTrans) {
            transform.set(worldTrans);
        }
    }

    static class Entity extends ModelInstance implements Disposable {
        public final btRigidBody body;
        public final MotionState motionState;

        public Entity (Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
            super(model, node);
            motionState = new MotionState();
            motionState.transform = transform;
            //constructionInfo.setRestitution(0.000001f);
            body = new btRigidBody(constructionInfo);
           // body.setGravity(new Vector3(0, -9.8f, 0));
            body.setMotionState(motionState);
        }

        @Override
        public void dispose () {
            body.dispose();
            motionState.dispose();
        }

        static class Creator implements Disposable {
            public final Model model;
            public final String node;
            public final btCollisionShape shape;
            public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
            private static Vector3 localInertia = new Vector3();

            public Creator (Model model, String node, btCollisionShape shape, float mass) {
                this.model = model;
                this.node = node;
                this.shape = shape;
                if (mass > 0f)
                    shape.calculateLocalInertia(mass, localInertia);
                else
                    localInertia.set(0, 0, 0);
                this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
            }

            public Entity create () {
                return new Entity(model, node, constructionInfo);
            }

            @Override
            public void dispose () {
                shape.dispose();
                constructionInfo.dispose();
            }
        }
    }

    PerspectiveCamera cam;
    ExtendViewport viewport;
    CameraInputController camController;
    ModelBatch modelBatch;
    Environment environment;
    Model model;
    Array<Entity> instances;
    ArrayMap<String, Entity.Creator> constructors;
    Accelerometer accelerometer;

    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    MyContactListener contactListener;
    btBroadphaseInterface broadphase;
    btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;

    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;

    float xRot = 0;
    float[] camPos;

    @Override
    public void create () {
        Bullet.init();

        accelerometer = new Accelerometer(0.2f);
        stage = new Stage();
        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(label);
        stringBuilder = new StringBuilder();

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(150f, 4f, 200f);
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(10f, 10f, 10f, 100, 100);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                .box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
                .cone(1f, 2f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
                .capsule(0.5f, 2f, 10);
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
                .cylinder(1f, 2f, 1f, 10);
        model = mb.end();

        constructors = new ArrayMap<String, Entity.Creator>(String.class, Entity.Creator.class);
        constructors.put("nothing", new Entity.Creator(model, "", new btBoxShape(new Vector3(75f, 2, 100f)), 0));
        constructors.put("ground", new Entity.Creator(model, "ground", new btBoxShape(new Vector3(75f, 2f, 100f)), 0));
        constructors.put("sphere", new Entity.Creator(model, "sphere", new btSphereShape(5f), 0.1f));
        constructors.put("box", new Entity.Creator(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("cone", new Entity.Creator(model, "cone", new btConeShape(0.5f, 2f), 1f));
        constructors.put("capsule", new Entity.Creator(model, "capsule", new btCapsuleShape(.5f, 1f), 1f));
        constructors.put("cylinder", new Entity.Creator(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f)), 1f));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));
        contactListener = new MyContactListener();

        instances = new Array<Entity>();
        Entity object = constructors.get("ground").create();
        object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        object.body.setHitFraction(0);
        object.body.setRollingFriction(0.1f);
        object.transform.setFromEulerAngles(0, 0, 0);
        object.body.proceedToTransform(object.transform);
        instances.add(object);
        dynamicsWorld.addRigidBody(object.body);
        object.body.setContactCallbackFlag(GROUND_FLAG);
        object.body.setContactCallbackFilter(0);

        object = constructors.get("nothing").create();
        object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        object.body.setHitFraction(0);
        object.body.setRollingFriction(0.1f);
        object.transform.setFromEulerAngles(0, 0, 0);
        object.body.proceedToTransform(object.transform);
        instances.add(object);
        dynamicsWorld.addRigidBody(object.body);
        object.body.setContactCallbackFlag(GROUND_FLAG);
        object.body.setContactCallbackFilter(0);

        object = constructors.get("sphere").create();
        object.transform.trn(0, 4.5f, 0);
        object.body.proceedToTransform(object.transform);
        object.body.setUserValue(instances.size);
        object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        object.body.setDamping(0.1f, 0.1f);
        object.body.setLinearVelocity(new Vector3(0, -1, 0));
        object.body.setFriction(0.1f);
        instances.add(object);
        dynamicsWorld.addRigidBody(object.body);
        object.body.setContactCallbackFlag(OBJECT_FLAG);
        object.body.setContactCallbackFilter(GROUND_FLAG);
        object.body.setActivationState(Collision.DISABLE_DEACTIVATION);

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = 1f;
        cam.far = 400f;
        cam.position.set(0, 300, 0);
        cam.lookAt(0, 0, 0);
        cam.update();
       // viewport = new ExtendViewport(800, 480, cam);
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(this));
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Input.Keys.LEFT:

                break;
            case Input.Keys.RIGHT:
                System.out.println(xRot + " x");
                xRot += 1;
                instances.get(0).transform.setFromEulerAngles(xRot, 0, 0);
                instances.get(0).body.proceedToTransform(instances.get(0).transform);
                break;
        }
        return true; // return true to indicate the event was handled
    }



    public Vector3 upVector(Matrix4 pos) {
        float[] tmp = pos.getValues();
        return new Vector3(tmp[Matrix4.M01], tmp[Matrix4.M11], tmp[Matrix4.M21]);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        System.out.println(accelerometer.axisX() + ", " + accelerometer.axisY());
        System.out.println(upVector(instances.get(0).transform));
        xRot += 1;
        cam.rotate(10, 0, 1, 0);
        return true; // return true to indicate the event was handled
    }
    Quaternion rotation = new Quaternion();



    public void sphericalCamera(float radius, float polar, float elevation) {
        float a = radius * MathUtils.cos(elevation);
        cam.position.x = radius * MathUtils.sin(elevation);
        cam.position.y = a * MathUtils.cos(polar);
        cam.position.z = a * MathUtils.sin(polar);
    }

    @Override
    public void render () {
        //sphericalCamera(200f,  ((accelY / 10) * (MathUtils.PI / 2)), (MathUtils.PI / 2) +  ((accelX / 10) * (MathUtils.PI / 2)) );//-((accelY / 10) * (MathUtils.PI / 2))); //(MathUtils.PI / 2) + ((accelX / 10) * (MathUtils.PI / 2))
        //cam.position.set(upVector(instances.get(0).transform).scl(200f));
        //cam.direction.set(upVector(instances.get(0).transform).scl(-1f));
        //cam.direction.
        // cam.update();

        accelerometer.update();

        final float delta = Math.min(1f / 60f, Gdx.graphics.getDeltaTime());

        rotation.setEulerAngles(0, ((((accelerometer.axisY() * 10) / 10f) / 10) * 90), ((((accelerometer.axisX() * 10) / 10f) / 10) * 90));

        instances.get(0).transform.idt().rotate(rotation).translate(0, 0, 0);
        instances.get(0).body.setActivationState(Collision.ACTIVE_TAG);

        dynamicsWorld.stepSimulation(delta, 5, 1f / 120f);

        //camController.update();

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();

        stringBuilder.setLength(0);

        stringBuilder.append(" AceX: ").append(Gdx.input.getAccelerometerX());
        stringBuilder.append(" AceY: ").append(Gdx.input.getAccelerometerY());
        label.setFontScale(3);
        label.setText(stringBuilder);
        stage.draw();
    }

    @Override
    public void dispose () {
        for (Entity obj : instances)
            obj.dispose();
        instances.clear();

        for (Entity.Creator ctor : constructors.values())
            ctor.dispose();
        constructors.clear();

        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();

        contactListener.dispose();

        modelBatch.dispose();
        model.dispose();
    }



    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void resize (int width, int height) {
    }
}

//boolean available = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer); checking accelerometer availability