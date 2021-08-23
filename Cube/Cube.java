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

    public static void main(String args[]) {
        System.out.println(Cube.getDelta(new Vector3(0, 0, 0), new Vector3(1, 1, 1), new Vector3(0, 1, 1),
                new Vector3(0.2, 0.2, 0.2)));
    }

}
