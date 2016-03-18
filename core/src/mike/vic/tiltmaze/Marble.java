package mike.vic.tiltmaze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

class Marble extends Entity {
    private Vector3 position;

    public Marble(float diameter, float mass, float friction, Vector3 startPoint) {
        super(Universe.builder.createSphere(diameter, diameter, diameter, 100, 100, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.BLUE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)
                , new btSphereShape(diameter / 2), mass);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        position = startPoint;
        transform.trn(position);
        body.proceedToTransform(transform);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        body.setFriction((float) Math.sqrt(friction));
        body.setUserValue(99);
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

    public void update() {
        transform.getTranslation(position);
    }

    public Vector3 getPosition() {
        return position;
    }
}
//object.body.setDamping(0.1f, 0.1f);
//object.body.setLinearVelocity(new Vector3(0, 1, 0));
//object.body.setFriction(0.1f);




