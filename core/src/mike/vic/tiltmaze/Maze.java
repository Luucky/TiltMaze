package mike.vic.tiltmaze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Mike on 2016-03-03.
 */
public class Maze {
    private final float WIDTH, HEIGHT, DEPTH, THICKNESS = 0.1f;

    private final int maxCellsX, maxCellsZ;
    private final int[][] maze;

    private final float cellUnitX, cellUnitZ, offsetX, offsetZ;
    private Array<Entity> walls;

    public Array<Entity> renderEntities() {
        return walls;
    }

    public Maze(float width, float height, float depth, int mazeLengthX, int mazeLengthZ) {
        WIDTH = width;
        HEIGHT = height;
        DEPTH = depth;

        maxCellsX = mazeLengthX;
        maxCellsZ = mazeLengthZ;

        walls = new Array<Entity>();
        walls.add(buildPlane());

        cellUnitX = WIDTH / maxCellsX; //16 / 15 =
        cellUnitZ = DEPTH / maxCellsZ; //26 / 15
        offsetX = -WIDTH / 2;
        offsetZ = -DEPTH / 2;

        maze = new int[maxCellsX][maxCellsZ];

        generateMaze(0, 0); // random starting point to do
        buildMaze();
//        walls.add(buildWall(0, 10, 10, true));
//        walls.add(buildWall(8, 0,  4, false));
//        walls.add(buildWall(8, 5,  5, false));
//        walls.add(buildWall(5, 5, 5, true));
//        walls.add(buildWall(5, 5, 5, false));

        display();
    }

    private void buildMaze() {
        int continuousWallX = 0;
        int continuousWallZ = 0;

        walls.add(buildWall(0, 0, maxCellsX, true));
        walls.add(buildWall(0, maxCellsZ, maxCellsX, true));
        for (int i = 0; i < maxCellsZ; i++) {
            for (int j = 0; j < maxCellsX; j++) {
                if ((maze[j][i] & 1) == 0) {
                    continuousWallX++;
                } else if (continuousWallX != 0) {
                    walls.add(buildWall(j - continuousWallX, i, continuousWallX, true));
                    System.out.println("NORTH: " + j + " " + i + " " + continuousWallX);
                    continuousWallX = 0;
                }
            }
            if (continuousWallX != 0) walls.add(buildWall(maxCellsX - continuousWallX, i, continuousWallX, true));
            continuousWallX = 0;
        }

        walls.add(buildWall(0, 0, maxCellsZ, false)); // WEST WALL
        walls.add(buildWall(maxCellsX, 0, maxCellsZ, false)); //EAST WALL;
        for (int i = 0; i < maxCellsX; i++) {
            for (int j = 0; j < maxCellsZ; j++) {
                if ((maze[i][j] & 8) == 0)
                  continuousWallZ++;
                else if (continuousWallZ != 0) {
                    walls.add(buildWall(i, j - continuousWallZ, continuousWallZ, false));
                    System.out.println("west: " + i + " " + j + " " + continuousWallZ);
                    continuousWallZ = 0;
                }
            }
            if (continuousWallZ != 0) walls.add(buildWall(i, maxCellsZ - continuousWallZ, continuousWallZ, false));
            continuousWallZ = 0;
        }
        //buildWall(maxCellsX, maxCellsZ,  true);
    }

    private Entity buildWall(int cellsX, int cellsZ, int length, boolean horizontal) {
        float posX = cellsX * cellUnitX, posZ = cellsZ * cellUnitZ;
        float wallLength = (horizontal ? cellUnitX : cellUnitZ) * length;
        //wallLength += horizontal ? THICKNESS : -THICKNESS ;

        Entity entity = new Entity((Universe.builder.createBox(horizontal ? wallLength : THICKNESS, HEIGHT, !horizontal ? wallLength : THICKNESS, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal)),
                new btBoxShape(new Vector3((horizontal ? wallLength : THICKNESS) / 2, HEIGHT / 2, (!horizontal ? wallLength : THICKNESS) / 2)), 0);
        entity.body.setCollisionFlags(entity.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        entity.transform.trn((horizontal ? wallLength / 2 : 0) + posX + offsetX + THICKNESS, HEIGHT, (!horizontal ? wallLength / 2 : 0) + posZ + offsetZ - THICKNESS);
        entity.body.proceedToTransform(entity.transform);
        entity.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        entity.body.setFriction((float) Math.sqrt(0.4));
        return entity;
    }


//    public void print() {
//        for(int i = 0; i < maxCellsX; i++) {
//            for(int j = 0; j < maxCellsZ; j++)
//                if (maze[j][i] > 9) System.out.print(" " + maze[j][i] + " ");
//                else System.out.print("  " + maze[j][i] + " ");
//            System.out.println();
//        }
//    }
    public void display() {
        for (int i = 0; i < maxCellsZ; i++) {
            // draw the north edge
            for (int j = 0; j < maxCellsX; j++) {
                System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
            }
            System.out.println("+");
            // draw the west edge
            for (int j = 0; j < maxCellsX; j++) {
                System.out.print((maze[j][i] & 8) == 0 ? "|   " : "    ");
            }
            System.out.println("|");
        }
        // draw the bottom line
        for (int j = 0; j < maxCellsX; j++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }


    private Entity buildPlane() {
        Entity entity = new Entity((Universe.builder.createBox(WIDTH, HEIGHT, DEPTH, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal)),
                new btBoxShape(new Vector3(WIDTH / 2, HEIGHT / 2, DEPTH / 2)), 0);
        entity.body.setCollisionFlags(entity.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        //entity.transform.trn(2, 0, 2);
        entity.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        entity.body.setFriction((float) Math.sqrt(0.4));
        entity.body.setContactCallbackFlag(Entity.GROUND_FLAG);
        entity.body.setContactCallbackFilter(0);
        return entity;
    }


    static DIRECTION[] directions = DIRECTION.values();
    static Random rnd = new Random();

    private void generateMaze(int cx, int cy) {
        int dir;
        for(dir = directions.length - 1; dir >= 0; dir--)
            swap(directions, dir, rnd.nextInt(directions.length - 1));

        for (dir = 0; dir < 4; dir++) {
            if (between(cx + directions[dir].dx, maxCellsX) && between(cy + directions[dir].dy, maxCellsZ) && (maze[cx + directions[dir].dx][cy + directions[dir].dy] == 0)) {
                maze[cx][cy] |= directions[dir].bit;
                maze[cx + directions[dir].dx][cy + directions[dir].dy] |= directions[dir].opposite.bit;
                generateMaze(cx + directions[dir].dx, cy + directions[dir].dy);
            }
        }
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

        private DIRECTION(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    };

    public void dispose() {
        for (Entity ent: walls)
            ent.dispose();
        walls.clear();
        walls = null;
    }
}
