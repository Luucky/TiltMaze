package mike.vic.tiltmaze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

/**
 * Created by Mike on 2016-03-03.
 */
class Marble extends Entity {
    private static final String node = "marble";

    private Vector3 gravity;

    public Marble(float diameter, float mass) {
        super(Universe.builder.createSphere(diameter, diameter, diameter, 100, 100, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.BLUE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)
                , new btSphereShape(diameter / 2), mass);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        transform.trn(0, 4.5f, 0);
        body.proceedToTransform(transform);
        body.setCcdSweptSphereRadius(diameter / 5);
        body.setFriction((float) Math.sqrt(0.4));
        body.setUserValue(99);
        gravity = new Vector3();
        body.setContactCallbackFlag(OBJECT_FLAG);
        body.setContactCallbackFilter(GROUND_FLAG);
    }

//        static public Model createModel(float diameter) {
//            model = Universe.builder.createSphere(diameter, diameter, diameter, 100, 100, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//            System.out.println(model.toString());
//            createCollisionObject(diameter);
//            return model;
//        }
//
//        static public btCollisionShape createCollisionObject(float diameter) {
//            collisionShape = ;
//            System.out.println(collisionShape);
//            return collisionShape;
//        }

    public void updateGravity() {
        gravity.set(-Accelerometer.axisX() / 3 * 9.8f, -9.8f, Accelerometer.axisY() / 3 * 9.8f);
        body.setGravity(gravity);
    }
}
//object.body.setDamping(0.1f, 0.1f);
//object.body.setLinearVelocity(new Vector3(0, 1, 0));
//object.body.setFriction(0.1f);




