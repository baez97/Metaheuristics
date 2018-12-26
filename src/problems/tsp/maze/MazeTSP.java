package problems.tsp.maze;

import optimization.Configuration;
import problems.tsp.TSP;
import utils.Position;
import visualization.*;

/**
 * Extends the TSP to represent it in a maze where movements are either
 * horizontal or vertical, and uses manhattan as distance.
 */
public class MazeTSP extends TSP implements ProblemVisualizable {

    /**
     * Constructors
     */
    public MazeTSP() {
        generateInstance(20, 10, 0);
    }

    public MazeTSP(int rangeXY, int numCities) {
        generateInstance(rangeXY, numCities, 0);
    }

    public MazeTSP(int rangeXY, int numCities, int seed) {
        generateInstance(rangeXY, numCities, seed);
    }

    /**
     * Returns a view of the problem.
     */
    @Override
    public ProblemView getView() {
        MazeTSPView mazeView = new MazeTSPView(this, 600);
        return mazeView;
    }

    /**
     * Calculates the score of a configuration as the sum of the path.
     */
    @Override
    public double score(Configuration configuration) {

        /**
         * COMPLETAR
         */
        int distance = 0;

        int values[] = configuration.getValues();

        // Distancia desde el agente hasta el primer queso
        distance += this.dist(this.posAgent, this.posCities.get(values[0]));

        // Distancia entre los quesos
        int i = 0;
        for (; i < values.length - 1; i++) {
            distance += this.dist(this.posCities.get(values[i]), this.posCities.get(values[i + 1]));
        }

        // Distancia desde el último queso hasta la posición de salida
        distance += this.dist(this.posCities.get(values[i]), this.posExit);
        return 0;
    }

    /**
     * Calculates the distance between two points.
     */
    private double dist(Position from, Position to) {
        return Math.abs(from.x - to.x) + Math.abs(from.y - to.y);
    }
}
