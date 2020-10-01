package comp1110.ass2;

/**
 * This enumeration type specifies the four general directions that
 * individual piece may be place (North, South, West and East).
 * @author Boyang Gao, Yuxuan Hu, Qinrui Cheng
 */

public enum Direction {

    NORTH('N'), EAST('E'), SOUTH('S'), WEST('W');

    public char value;

    Direction(char value) {
        this.value = value;
    }

}
