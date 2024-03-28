public class Coordinates
{
    private final int x;
    private final double y;

    public Coordinates(int x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getXFormatted() {
        return String.valueOf(x);
    }

    public String getYFormatted() {
        return String.valueOf(y);
    }
}