package mike.vic.tiltmaze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

/**
 * Created by Mike on 2016-03-03.
 */
public class Maze extends Entity {
    private static final String node = "maze";

    public Maze(float width, float height, float depth, float mass) {
        super(Universe.builder.createBox(width, height, depth, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
                 new btBoxShape(new Vector3(width / 2, height / 2, depth / 2)), mass);
        body.setUserValue(1);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        body.setRollingFriction(0.6f);
        body.setContactCallbackFlag(GROUND_FLAG);
        body.setContactCallbackFilter(0);
    }
}
