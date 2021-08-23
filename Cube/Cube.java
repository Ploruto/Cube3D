package Cube;

class Vector3 {
    double x, y, z = 0;

    Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector3 crossProduct(Vector3 vect_A, Vector3 vect_B) {
        Vector3 cross_P = new Vector3(0, 0, 0);
        cross_P.x = vect_A.y * vect_B.z - vect_A.z * vect_B.y;
        cross_P.y = vect_A.z * vect_B.x - vect_A.x * vect_B.z;
        cross_P.z = vect_A.x * vect_B.y - vect_A.y * vect_B.x;
        return cross_P;
    }
}

public class Cube {
    private static short SIZE = 3;
    private short level = 0;
    private Cube innerCubes[][][] = new Cube[SIZE][SIZE][SIZE];
    private boolean hasChildren = false;
    private String address = "";

    private static double getDelta(Vector3 pointA, Vector3 pointB, Vector3 pointC, Vector3 pointInCubeToCheck) {

        /*
         * Get two different vectors which are in the plane, such as B−A=(3,0,−3) and
         * C−A=(3,3,3) . Compute the cross product of the two obtained vectors:
         * (B−A)×(C−A)=(9,−18,9) . This is the normal vector of the plane, so we can
         * divide it by 9 and get (1,−2,1) . The equation of the plane is thus
         * x−2y+z+k=0 . To get k, substitute any point and solve; we get k=−6. The final
         * equation of the plane is x−2y+z−6=0 .
         */

        /* calculate VectorAB & AC */
        Vector3 vecAB = new Vector3(pointB.x - pointA.x, pointB.y - pointA.y, pointB.z - pointA.z);
        Vector3 vecAC = new Vector3(pointC.x - pointA.x, pointC.y - pointA.y, pointC.z - pointA.z);
        /* take cross product of VectorAB & VectorAC */
        Vector3 cross_P = Vector3.crossProduct(vecAB, vecAC);
        /* deconstruct x,y,z -> for x+y+z+k=0 */
        double x = cross_P.x;
        double y = cross_P.y;
        double z = cross_P.z;
        /* solve for k (use any point) */
        double k = ((pointA.x * x) + (pointA.y * y) + (pointA.z * z)) * -1;

        /* solve with x,y,z of PointToCheck */

        return ((pointInCubeToCheck.x * x) + (pointInCubeToCheck.y * y) + (pointInCubeToCheck.z * z) + k);
    }

    private void fillInnerCubes() {
        this.hasChildren = true;
        for (short x = 0; x < 3; x++) {
            for (short y = 0; y < 3; y++) {
                for (short z = 0; z < 3; z++) {
                    this.innerCubes[x][y][z] = new Cube();
                    this.innerCubes[x][y][z].level = (short) (this.level + 1);
                    this.innerCubes[x][y][z].address = this.address + getLetter(x, y, z);
                }
            }
        }
    }

    private static Vector3[] getCornerOffsets(short level) {
        Vector3[] corners = new Vector3[8];
        double stepSize = getMinimalStepOnLevel(level);
        int i = 0;
        for (short x = 0; x <= 1; x++) {
            for (short y = 0; y <= 1; y++) {
                for (short z = 0; z <= 1; z++) {
                    // / 3 * level
                    corners[i] = new Vector3(x * stepSize, y * stepSize, z * stepSize);
                    i++;
                }
            }
        }

        return corners;
    }

    private String getLetter(short x, short y, short z) {
        return ";";
    }

    public boolean isBeingSplit(Vector3 pointA, Vector3 pointB, Vector3 pointC, double absoluteX, double absoluteY,
            double absoluteZ, short level) {
        if (level == 0)
            return true;

        Vector3 cornerOffsets[] = getCornerOffsets(level);
        short originDeltaSignum = (short) Math
                .signum(getDelta(pointA, pointB, pointC, new Vector3(absoluteX, absoluteY, absoluteZ)));

        for (int i = 1; i < cornerOffsets.length; i++) {
            if (Math.signum(getDelta(pointA, pointB, pointC, new Vector3(absoluteX + cornerOffsets[i].x,
                    absoluteY + cornerOffsets[i].y, absoluteZ + cornerOffsets[i].z))) != originDeltaSignum) {
                return true;
            }
        }

        return false;
    }

    private static double getMinimalStepOnLevel(int level) {
        return (1 / (Math.pow(3, level)));
    }

    private static short findIndexBits(boolean valueToFind, boolean bits[]) {
        for (short i = 0; i < bits.length; i++)
            if (bits[i] == valueToFind)
                return i;

        return -1;
    }

    private static short[] findAllIndeciesBits(boolean valueToFind, boolean bits[]) {
        short result[] = { 0, 0, 0, 0 };
        short pointer = 0;
        for (short i = 0; i < bits.length; i++)
            if (bits[i] == valueToFind) {
                result[pointer] = i;
                pointer++;
            }

        return result;
    }

    private static Vector3 coordinatesOfBitsIndex(short index) {
        /* only the first and second values are important */
        switch (index) {
            case 0:
                return new Vector3(0, 0, 0);
            case 1:
                return new Vector3(1, 0, 0);
            case 2:
                return new Vector3(0, 1, 0);
            case 3:
                return new Vector3(1, 1, 0);
            default:
                return new Vector3(0, 0, 0);
        }
    }

    public static Vector3[][] getPlanePointsFromTile(short bitsNumber) {
        /* create bits from Number */
        Vector3 resultPoints[][] = new Vector3[2][3];
        boolean bits[] = new boolean[4];
        if (bitsNumber > 7) {
            bits[3] = true;
            bitsNumber -= 8;
        }
        if (bitsNumber > 3) {
            bits[2] = true;
            bitsNumber -= 4;
        }
        if (bitsNumber > 1) {
            bits[1] = true;
            bitsNumber -= 2;
        }
        if (bitsNumber > 0) {
            bits[0] = true;

        }
        short count = countBits(bits);
        /* fill points -> 0,0,0 (default) */
        for (short i = 0; i < 3; i++)
            resultPoints[0][i] = new Vector3(i, i, 0);

        switch (count) {
            case 1: {
                resultPoints[0][0] = coordinatesOfBitsIndex(findIndexBits(true, bits));
                resultPoints[0][0].z = 1;

                resultPoints[0][1].x = Math.abs(resultPoints[0][0].x - 1);
                resultPoints[0][1].y = resultPoints[0][0].y;
                resultPoints[0][1].z = 0;

                resultPoints[0][2].x = resultPoints[0][0].x;
                resultPoints[0][2].y = Math.abs(resultPoints[0][0].y - 1);
                resultPoints[0][2].z = 0;
                break;
            }
            case 2: {
                // get all positive indecies
                short indecies[] = findAllIndeciesBits(true, bits);

                for (short i = 0; i < 2; i++) {
                    resultPoints[0][i] = coordinatesOfBitsIndex(indecies[i]);
                    resultPoints[0][i].z = 1;
                    System.out.println(resultPoints[0][i].x);
                }
                resultPoints[0][2] = new Vector3(resultPoints[0][0].x, resultPoints[0][1].y, 0);
                if ((bits[0] && bits[1]) || (bits[1] && bits[3]) || (bits[2] && bits[3]) || (bits[0] && bits[2]))
                    resultPoints[0][2] = new Vector3((resultPoints[0][0].x + 1) % 2, (resultPoints[0][1].y + 1) % 2, 0);
                break;
            }
            // two planes
            case 3: {
                short indecies[] = findAllIndeciesBits(true, bits);
                for (short i = 0; i < 3; i++) {
                    resultPoints[0][i] = coordinatesOfBitsIndex(indecies[i]);
                    resultPoints[0][i].z = 1;
                }

                // create second plane's points
            }
        }

        /* if vector [] length = 1 -> there is only one Plane to calcualte */
        return resultPoints;

    }

    private static short countBits(boolean bits[]) {
        short count = 0;
        for (boolean bit : bits) {
            if (bit)
                count++;
        }
        return count;
    }

    public static void main(String args[]) {
        Cube cube = new Cube();
        Vector3 points[][] = getPlanePointsFromTile((short) 1);
        for (Vector3[] vector3s : points) {
            for (Vector3 vector : vector3s) {
                System.out.println(vector.x + ", " + vector.y + ", " + vector.z);
            }
        }
        double one = 0.6;
        System.out.println(cube.isBeingSplit(points[0][0], points[0][1], points[0][2], one, one, one, (short) 2));
    }

}
