package mike.vic.tiltmaze;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

class Marble extends Entity {
    private Vector3 position;
    private float diameter;

    public Marble(float diameter, float mass, float friction) {
        super(Universe.builder.createSphere(diameter, diameter, diameter, 100, 100, GL20.GL_TRIANGLES,
                new Material(TextureAttribute.createDiffuse(Assets.metal)), Usage.Position | Usage.TextureCoordinates | Usage.Normal),
                new btSphereShape(diameter / 2), mass);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        position = new Vector3(0, 5, 0);
        this.diameter = diameter;
        transform.trn(position);
        body.proceedToTransform(transform);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        body.setFriction((float) Math.sqrt(friction));
        body.setUserValue(99);
        body.setContactCallbackFlag(OBJECT_FLAG);
        body.setContactCallbackFilter(GROUND_FLAG);
    }

    public float getRadius() {
        return diameter / 2;
    }


    public void activate() {
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        body.setLinearVelocity(lastVelocity);
    }

    private Vector3 lastVelocity;

    public void deactivate() {
        lastVelocity = body.getLinearVelocity();
        body.setLinearVelocity(new Vector3());
        body.setAngularVelocity(new Vector3());
        body.setActivationState(0);
    }

    public void setPosition(Vector3 newPosition) {
        transform.set(newPosition, new Quaternion());
        body.proceedToTransform(transform);
        update();
    }

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




