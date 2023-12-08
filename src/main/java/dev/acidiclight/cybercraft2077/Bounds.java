package dev.acidiclight.cybercraft2077;

public class Bounds {
    public Point center = new Point(0, 0, 0);
    public Point extents = new Point(0, 0, 0);

    public boolean Contains(Point point) {
        double xMin = center.x - extents.x;
        double xMax = center.x + extents.x;
        double yMin = center.y - extents.y;
        double yMax = center.y + extents.y;
        double zMin = center.z - extents.z;
        double zMax = center.z + extents.z;

        // Check the X dimension
        if (point.x < xMin || point.x > xMax)
            return false;

        // Now the Y
        if (point.y < yMin || point.y > yMax)
            return false;

        // You know what's next
        if (point.z < zMin || point.z > zMax)
            return false;

        return true;
    }
}
