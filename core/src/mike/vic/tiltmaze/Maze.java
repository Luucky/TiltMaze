package mike.vic.tiltmaze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Maze {
    static public final int minXcells = 20, minZcells = 23;

    final float WIDTH, DEPTH, THICKNESS = 0.1f;
    static final float HEIGHT = 0.6f;

    final float minWidth = 16f, minDepth = 26f;
    final float minCameraHeight = 32f;

    private final int cellsX, cellsZ;
    private final int[][] maze;

    private final float cellUnitX, cellUnitZ, offsetX, offsetZ;
    private Vector3 finishArea;

    private Array<Entity> walls;

    private float friction;

    public Array<Entity> renderEntities() {
        return walls;
    }

    public Maze(float friction, int sizeAdjust) {
        this.friction = friction;

        cellsX = minXcells + sizeAdjust; //20 min
        cellsZ = minZcells + sizeAdjust; //23 min

        WIDTH = minWidth + (sizeAdjust) * 0.8f; //16f min
        DEPTH = minDepth + (sizeAdjust) * 1.3f; //26f min

        walls = new Array<Entity>();

        cellUnitX = WIDTH / cellsX;
        cellUnitZ = DEPTH / cellsZ;
        offsetX = -WIDTH / 2;
        offsetZ = -DEPTH / 2;

     //   finishArea = new Vector3(-offsetX - cellUnitX / 2, 0, -offsetZ - cellUnitZ / 2);
        finishArea = getStartPoint().add(1, 0, 1);

        maze = new int[cellsX][cellsZ];
    }

    public Vector3 getStartPoint() {
        return new Vector3(offsetX + cellUnitX / 2, 0, offsetZ + cellUnitZ / 2);
    }

    public Vector3 getFinishArea() {
        return finishArea;
    }

    public float getCameraHeight() {
        return minCameraHeight + (cellsX - minXcells) * 1.65f;
    }

    public boolean generateMaze() {
        while (maze[cellsX - 1][cellsZ - 1] == 0) generateMaze(0, 0);
        buildMaze();

        return maze[cellsX - 1][cellsZ - 1] != 0;
    }

    private void buildMaze() {
       // if (walls.size > 0) clearEntities();
        walls.add(buildPlane());

        int continuousWallX = 0;
        int continuousWallZ = 0;

        walls.add(buildWall(0, 0, cellsX, true)); // NORTH WALL
        walls.add(buildWall(0, cellsZ, cellsX, true)); // SOUTH WALL
        for (int i = 0; i < cellsZ; i++) {
            for (int j = 0; j < cellsX; j++) {
                if ((maze[j][i] & 1) == 0) {
                    continuousWallX++;
                } else if (continuousWallX != 0) {
                    walls.add(buildWall(j - continuousWallX, i, continuousWallX, true));
                    System.out.println("NORTH: " + j + " " + i + " " + continuousWallX);
                    continuousWallX = 0;
                }
            }
            if (continuousWallX != 0) walls.add(buildWall(cellsX - continuousWallX, i, continuousWallX, true));
            continuousWallX = 0;
        }

        walls.add(buildWall(0, 0, cellsZ, false)); // WEST WALL
        walls.add(buildWall(cellsX, 0, cellsZ, false)); //EAST WALL;
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsZ; j++) {
                if ((maze[i][j] & 8) == 0)
                  continuousWallZ++;
                else if (continuousWallZ != 0) {
                    walls.add(buildWall(i, j - continuousWallZ, continuousWallZ, false));
                    continuousWallZ = 0;
                }
            }
            if (continuousWallZ != 0) walls.add(buildWall(i, cellsZ - continuousWallZ, continuousWallZ, false));
            continuousWallZ = 0;
        }
        walls.add(buildFinishCell());
    }

    private Entity buildFinishCell() {
        Entity entity = new Entity((Universe.builder.createBox(cellUnitX, HEIGHT, cellUnitZ, GL20.GL_TRIANGLES,
                new Material(TextureAttribute.createDiffuse(Assets.finishArea)), Usage.Position | Usage.TextureCoordinates | Usage.Normal)),
                new btBoxShape(new Vector3(cellUnitX / 2, HEIGHT / 2, cellUnitZ / 2)), 0);
        entity.body.setCollisionFlags(entity.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        entity.transform.trn(-offsetX - cellUnitX / 2, -HEIGHT + 0.05f, -offsetZ - cellUnitZ / 2);
        entity.body.proceedToTransform(entity.transform);
        entity.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        entity.body.setFriction((float) Math.sqrt(friction));
        entity.body.setUserValue(77);
        entity.body.setContactCallbackFlag(Entity.WALL_FLAG);
        entity.body.setContactCallbackFilter(Entity.OBJECT_FLAG);
        return entity;
    }

    private Entity buildWall(int cellsX, int cellsZ, int length, boolean horizontal) {
        float posX = cellsX * cellUnitX - THICKNESS, posZ = cellsZ * cellUnitZ + THICKNESS;
        float wallLength = (horizontal ? cellUnitX : cellUnitZ) * length;

        Entity entity = new Entity((Universe.builder.createBox(horizontal ? wallLength : THICKNESS, HEIGHT, !horizontal ? wallLength : THICKNESS, GL20.GL_TRIANGLES,
                new Material(TextureAttribute.createDiffuse(Assets.darkWood)), Usage.Position | Usage.TextureCoordinates | Usage.Normal)),
                new btBoxShape(new Vector3((horizontal ? wallLength : THICKNESS) / 2, HEIGHT / 2, (!horizontal ? wallLength : THICKNESS) / 2)), 0);
        entity.body.setCollisionFlags(entity.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        entity.transform.trn((horizontal ? wallLength / 2 : 0) + posX + offsetX + THICKNESS, 0, (!horizontal ? wallLength / 2 : 0) + posZ + offsetZ - THICKNESS);
        entity.body.proceedToTransform(entity.transform);
        entity.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        entity.body.setFriction((float) Math.sqrt(friction));
        entity.body.setUserValue(77);
        entity.body.setContactCallbackFlag(Entity.WALL_FLAG);
        entity.body.setContactCallbackFilter(Entity.OBJECT_FLAG);
        return entity;
    }

    private Entity buildPlane() {
        Entity entity = new Entity((Universe.builder.createBox(WIDTH, HEIGHT, DEPTH, GL20.GL_TRIANGLES,
                new Material(TextureAttribute.createDiffuse(Assets.lightWood)), Usage.Position | Usage.TextureCoordinates | Usage.Normal)),
                new btBoxShape(new Vector3(WIDTH / 2, HEIGHT / 2, DEPTH / 2)), 0);
        entity.body.setCollisionFlags(entity.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        entity.transform.trn(0, -HEIGHT, 0);
        entity.body.proceedToTransform(entity.transform);
        entity.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        entity.body.setFriction((float) Math.sqrt(friction));
        entity.body.setUserValue(88);
        entity.body.setContactCallbackFlag(Entity.GROUND_FLAG);
        entity.body.setContactCallbackFilter(Entity.OBJECT_FLAG);
        return entity;
    }

    static DIRECTION[] directions = DIRECTION.values();
    static Random rnd = new Random();

    private void generateMaze(int cx, int cy) {
        int dir;
        for(dir = directions.length - 1; dir >= 0; dir--)
            swap(directions, dir, rnd.nextInt(directions.length - 1));

        for (dir = 0; dir < 4; dir++) {
            if (between(cx + directions[dir].dx, cellsX) && between(cy + directions[dir].dy, cellsZ) && (maze[cx + directions[dir].dx][cy + directions[dir].dy] == 0)) {
                maze[cx][cy] |= directions[dir].bit;
                maze[cx + directions[dir].dx][cy + directions[dir].dy] |= directions[dir].opposite.bit;
                generateMaze(cx + directions[dir].dx, cy + directions[dir].dy);
            }
        }
    }

    public void clearEntities() {
        for (Entity ent: walls)
            ent.dispose();

        walls.clear();
    }

    private static void swap(DIRECTION[] arr, int i, int j) {
        DIRECTION tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private static boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }

    private enum DIRECTION {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
        private final int bit;
        private final int dx;
        private final int dy;
        private DIRECTION opposite;

        // use the static initializer to resolve forward references
        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }

        DIRECTION(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    }

    public void dispose() {
        for (Entity ent: walls)
            ent.dispose();
        walls.clear();
        walls = null;
    }
}

//    public void display() {
//        for (int i = 0; i < maxCellsZ; i++) {
//            // draw the north edge
//            for (int j = 0; j < maxCellsX; j++) {
//                System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
//            }
//            System.out.println("+");
//            // draw the west edge
//            for (int j = 0; j < maxCellsX; j++) {
//                System.out.print((maze[j][i] & 8) == 0 ? "|   " : "    ");
//            }
//            System.out.println("|");
//        }
//        // draw the bottom line
//        for (int j = 0; j < maxCellsX; j++) {
//            System.out.print("+---");
//        }
//        System.out.println("+");
//    }
