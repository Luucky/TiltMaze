package mike.vic.tiltmaze;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Mike on 2016-03-03.
 */
class Entity extends ModelInstance implements Disposable {
    public btRigidBody body;
    public MotionState motionState;

    static public final short WALL_FLAG = 1 << 7;
    static public final short GROUND_FLAG = 1 << 8;
    static public final short OBJECT_FLAG = 1 << 9;
    static public final short ALL_FLAG = -1;

    public Entity (Model model, btCollisionShape shape,  float mass) {
        super(model);
        motionState = new MotionState();
        motionState.transform = transform;
        Vector3 inertia = new Vector3();
        if (mass > 0)
            shape.calculateLocalInertia(mass, inertia);
        else inertia = new Vector3();
        btRigidBody.btRigidBodyConstructionInfo structInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, inertia);
        body = new btRigidBody(structInfo);
        structInfo.dispose();
        body.setMotionState(motionState);
        Universe.dynamicsWorld.addRigidBody(body);
    }

    @Override
    public void dispose () {
        body.dispose();
        motionState.dispose();
    }

}