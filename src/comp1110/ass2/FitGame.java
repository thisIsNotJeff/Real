package comp1110.ass2;

import javafx.util.Pair;

import java.util.*;

/**
 * This class provides the text interface for the IQ Fit Game
 * <p>
 * The game is based directly on Smart Games' IQ-Fit game
 * (https://www.smartgames.eu/uk/one-player-games/iq-fit)
 * @author Boyang Gao, Yuxuan Hu, Qinrui Cheng
 */
public class FitGame {

    /**
     * Determine whether a piece placement is well-formed according to the
     * following criteria:
     * - it consists of exactly four characters
     * - the first character is a valid piece descriptor character (b, B, g, G, ... y, Y)
     * - the second character is in the range 0 .. 9 (column)
     * - the third character is in the range 0 .. 4 (row)
     * - the fourth character is in valid orientation N, S, E, W
     *
     * @param piecePlacement A string describing a piece placement
     * @return True if the piece placement is well-formed
     * @author Boyang Gao
     */

    static boolean isPiecePlacementWellFormed(String piecePlacement) {

        /** test if the String has four characters. */
        if(piecePlacement.length()!=4){return false;}

        /** test if the first character is valid descriptor character. */
        String firstCharacter = String.valueOf(piecePlacement.charAt(0)).toUpperCase();
        switch (firstCharacter){
            case "B": case "G": case "I": case "L": case "N": case "O":case "P": case "R": case "S": case "Y": case "*":
                break; default: return false;
        }

        /** test if the second character is between 0 to 9. */
        String secondCharacter = String.valueOf(piecePlacement.charAt(1));
        switch (secondCharacter){case "0": case "1": case "2": case "3": case "4": case "5":case "6": case "7": case "8": case "9":
                break; default: return false;
        }

        /** test if the third character is between 0 to 4. */
        String thirdCharacter = String.valueOf(piecePlacement.charAt(2));
        switch (thirdCharacter){case "0": case "1": case "2": case "3": case "4":
            break; default: return false;
        }

        /** test if the fourth character in valid orientation N, S, E, W */
        String forthCharacter = String.valueOf(piecePlacement.charAt(3));
        switch(forthCharacter){
            case "N": case "E": case "S": case "W":
                break; default: return false;
        }
        return true; // FIXME Task 2: determine whether a piece placement is well-formed
    }
    /**
     * Determine whether a placement string is well-formed:
     * - it consists of exactly N four-character piece placements (where N = 1 .. 10);
     * - each piece placement is well-formed
     * - no shape appears more than once in the placement
     * - the pieces are ordered correctly within the string
     *
     * @param placement A string describing a placement of one or more pieces
     * @return True if the placement is well-formed
     * @author Qinrui Cheng
     */
    public static boolean isPlacementWellFormed(String placement) {
        boolean flag = true;

        // check if input satisfies condition 1.
        if (placement.length() % 4 == 0
                && placement.length() >= 4
                && placement.length() <= 40) {

            ArrayList<String> grouped = new ArrayList<>();

            // group input String as an ArrayList of 4 chars as it's element.
            for (int i = 0; i < placement.length(); i++) {
                grouped.add(placement.substring(i,i+4));
                i = i + 3;
            }
            // check if the input satisfies condition 2.
            for (String s : grouped) {
                if (!isPiecePlacementWellFormed(s)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                StringBuilder shapes = new StringBuilder();
                // flatten the ArrayList into a String which only preserves shapes from the input.
                for (String s : grouped) {
                    shapes.append(s.toLowerCase().charAt(0));
                }

                // check if the input satisfies condition 3.
                if (shapes.chars().distinct().count() == shapes.length()) {

                    char[] unsortedShapes = shapes.toString().toLowerCase().toCharArray();
                    //sort the array of shapes according to the alphabetical order.
                    Arrays.sort(unsortedShapes);

                    StringBuilder sortedShape = new StringBuilder();

                    for (char c : unsortedShapes) sortedShape.append(c);
                    //check if the input satisfies the last condition.
                    if (!shapes.toString().toLowerCase().equals(sortedShape.toString())) {
                        flag = false;
                    }

                } else flag = false;
            }
        } else flag = false;

        return flag;
    }




    /**
     * Determine whether a placement string is valid.
     *
     * To be valid, the placement string must be:
     * - well-formed, and
     * - each piece placement must be a valid placement according to the
     *   rules of the game:
     *   - pieces must be entirely on the board
     *   - pieces must not overlap each other
     *
     * @param placement A placement string
     * @return True if the placement sequence is valid
     */
    public static boolean isPlacementValid(String placement) {
        return validityOccupation(placement).getKey();
    }

    /**
     * Given a string describing a placement of pieces, and a location
     * that must be covered by the next move, return a set of all
     * possible next viable piece placements which cover the location.
     *
     * For a piece placement to be viable it must:
     *  - be a well formed piece placement
     *  - be a piece that is not already placed
     *  - not overlap a piece that is already placed
     *  - cover the location
     *
     * @param placement A starting placement string
     * @param col      The location's column.
     * @param row      The location's row.
     * @return A set of all viable piece placements, or null if there are none.
     */
    static Set<String> getViablePiecePlacements(String placement, int col, int row) {

        Set<String> result = new HashSet<>();

        Integer columns = col;
        Integer rows = row;
        String newPiece = "";
        // get all the possible locations
        var candidate = possibleLocation(col, row);

        // collect the used colors, so we don't need to iterate all the possibilities.
        Character[] usedColor = getUsedColor(placement);

        // first check if the input placement plus a test color(which only occupy that location) is valid
        if (isPlacementValid(sortStringPlacement(placement + '*' + columns.toString() + rows.toString() + 'W'))) {
            // then iterate through all the colors exclude the Test one.
            for (Color c : Color.values()) {
                if (c == Color.TEST) continue;

                // check if the board is empty or the current iterating color haven't been used.
                if (placement.length() == 0 || !Arrays.asList(usedColor).contains(Character.toLowerCase(c.value))) {

                    // iterate through every direction
                    for (Direction d : Direction.values()) {
                        //iterate through every possible location.
                        for (Pair<Integer, Integer> p : candidate) {
                            newPiece = c.value + p.getKey().toString() + p.getValue().toString() + d.value;
                            var pair = validityOccupation(sortStringPlacement(placement + newPiece));
                            var occupation = pair.getValue();
                            var valid = pair.getKey();
                            if (valid) {
                                //to see if that place is taken.
                                if (occupation[col][row] == 1)
                                    result.add(newPiece);
                            }
                        }
                    }
                }
            }
        }

        if (result.size() == 0) return null;
        else return result;
    }

    public static Set<Pair<Integer, Integer>> possibleLocation(int col, int row) {
        Set<Pair<Integer, Integer>> result = new HashSet<>();

        if (row == 0)
            getRange(col, row, 1, 0, result);
        else if (row == 1)
            getRange(col, row, 1, 1, result);
        else if (row == 2)
            getRange(col, row, 1, 2, result);
        else if (row == 3) {
            int newRow = row-1;
            getRange(col, newRow, 1, 2, result);
        } else {
            int newRow = row-2;
            getRange(col, newRow, 1, 1, result);
        }

        getRange(col, row, 3, 1, result);

        return result;
    }


    public static void getRange(int col, int row, int colChange, int rowChange, Set<Pair<Integer,Integer>> toAdd) {
        int rowStart = 0;
        int colStart = 0;

        for (int i = row; i > row - (rowChange+1); i--) {
            if (i < 0) break;
            else rowStart = i;
        }

        for (int i = col; i > col - (colChange+1); i--) {
            if (i < 0) break;
            else colStart = i;
        }

        for (int i = rowStart; i < row + 1; i++) {
            for (int j = colStart; j < col + 1; j++) {
                toAdd.add(new Pair<>(j,i));
            }
        }
    }

    /**
     * Given a placement string, return all the used color in the placement.
     *
     * @param placement A string of placement.
     * @return An array of used color.
     */
    public static Character[] getUsedColor(String placement) {
        ArrayList<Character> used = new ArrayList<>();
        for (int i = 0; i < placement.length(); i = i+4) {
            used.add(Character.toLowerCase(placement.charAt(i)));
        }
        return used.toArray(new Character[0]);
    }

    /**
     * Return the sorted StringPlacement, the input string must be composed by well formed String pieces.
     *
     * @param placement A String placement.
     * @return A sorted String placement according to the alphabetical order of the color of pieces.
     */
    public static String sortStringPlacement(String placement) {
        List<String> grouped = new ArrayList<>();

        // if the input is in the incorrect length, throw exception.
        if (placement.length() == 0 || placement.length() % 4 != 0)
            throw new IllegalArgumentException("Incorrect placement String");
            // if any of the pieces of the placement String is not well formed, throw exception.
        else {
            for (int i = 0; i < placement.length(); i+=4) {
                String piece = placement.substring(i,i+4);
                if (isPiecePlacementWellFormed(piece))
                    grouped.add(piece);
                else throw new IllegalArgumentException("Incorrect placement String");
            }
        }
        // sort the ArrayList of String pieces according to their color.
        grouped.sort(Comparator.comparing((String s) -> s.substring(0, 1).toLowerCase()));
        String sorted = "";
        // recompose the placement String.
        for (String s : grouped) sorted += s;
        return sorted;
    }


    /**
     * Given a placement string, return a pair contains the validity of the placement according to the rule of the game and
     * an int array of array illustrate the real occupation of each pieces in the placement string.
     * into a List of Pairs.
     *
     * @param placement A string of placement.
     * @return A Pair of contains the validity of placement and the occupation int array of array.
     */
    public static Pair<Boolean, int[][]> validityOccupation(String placement) {

        //create a container for pieces parsed from string
        ArrayList<String> pieces = new ArrayList<>();

        for (int i = 0; i < placement.length() - 3; i = i + 4) {
            pieces.add(placement.substring(i, i + 4));
        }


        //create a int array of array to illustrate the real occupation of pieces.
        int[][] occupationArray = new int[10][5];

        Pair<Boolean, int[][]> b = new Pair<>(false, null);

        if (placement.length() == 0) return new Pair<>(true,occupationArray);
        else if (!isPlacementWellFormed(placement)) return b;
        else {
            for (String piece : pieces) {

                int row = Character.getNumericValue(piece.charAt(2));
                int column = Character.getNumericValue(piece.charAt(1));
                char charColour = piece.charAt(0);
                char charDirection = piece.charAt(3);


                if ((column < 0) || (column > 9) || (row < 0) || (row > 4)) return b;

                if ((charDirection != 'N') && (charDirection != 'E') &&
                        (charDirection != 'S') && (charDirection != 'W')) return b;


                if (charColour == 'b') {
                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 3][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    }
                } else if (charColour == 'B') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    }
                } else if (charColour == 'g') {

                    if (charDirection == 'N') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                        }
                    }
                } else if (charColour == 'G') {

                    if (charDirection == 'N') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    }
                } else if (charColour == 'i') {

                    if (charDirection == 'N') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                        }
                    }
                } else if (charColour == 'I') {

                    if (charDirection == 'N') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                        }
                    }
                } else if (charColour == 'l') {

                    if (charDirection == 'N') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    }
                } else if (charColour == 'L') {

                    if (charDirection == 'N') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    }
                } else if (charColour == 'n') {

                    if (charDirection == 'N') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                        }
                    }
                } else if (charColour == 'N') {

                    if (charDirection == 'N') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 7) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 2)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    }
                } else if (charColour == 'o') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 3][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    }
                } else if (charColour == 'O') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    }
                } else if (charColour == 'p') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                        }
                    }
                } else if (charColour == 'P') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 3][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    }
                } else if (charColour == 'r') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 3][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    }
                } else if (charColour == 'R') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    }
                } else if (charColour == 's') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 3][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    }
                } else if (charColour == 'S') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                        }
                    }
                } else if (charColour == 'y') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row] += 1;
                        }
                    }
                } else if (charColour == 'Y') {

                    if (charDirection == 'N') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 2][row] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else if (charDirection == 'E') {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 1][row + 2] += 1;
                            occupationArray[column + 1][row + 3] += 1;
                        }
                    } else if (charDirection == 'S') {
                        if ((column > 6) || (row > 3)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                            occupationArray[column + 2][row + 1] += 1;
                            occupationArray[column + 3][row + 1] += 1;
                        }
                    } else {
                        if ((column > 8) || (row > 1)) return b;
                        else {
                            occupationArray[column][row] += 1;
                            occupationArray[column][row + 1] += 1;
                            occupationArray[column][row + 2] += 1;
                            occupationArray[column][row + 3] += 1;
                            occupationArray[column + 1][row] += 1;
                            occupationArray[column + 1][row + 1] += 1;
                        }
                    }
                } else if (charColour == '*') {

                    occupationArray[column][row] += 1;
                } else return b;
            }

            for(int i = 0; i < 10; i++) {
                for(int j = 0; j < 5; j++) {
                    if(occupationArray[i][j] > 1) return b;
                }
            }
        }

        return new Pair<>(true,occupationArray);
    }




    /**
     * Return the solution to a particular challenge.
     **
     * @param challenge A challenge string.
     * @return A placement string describing the encoding of the solution to
     * the challenge.
     */

    public static String getSolution(String challenge) {
        var occupationArray = validityOccupation(challenge).getValue();
        Set<Pair<Integer,Integer>> emptyLocation = new HashSet<>();
        Set<Pair<Integer,Integer>> priorityLocation = new HashSet<>();
//        for (int i = 1; i < 9; i++) {
//            for (int j = 1; j < 4; j++) {
//                if (occupationArray[i][j] == 0) emptyLocation.add(new Pair<>(i,j));
//            }
//        }

        for (int i = 0; i < 10; i++) {
            priorityLocation.add(new Pair<>(i,0));
            priorityLocation.add(new Pair<>(i,4));
        }

        for (int j = 0; j < 5 ; j++) {
            priorityLocation.add(new Pair<>(0,j));
            priorityLocation.add(new Pair<>(4,j));
        }

        for (Pair<Integer,Integer> p : priorityLocation) {

        }





        return null;
    }


}


