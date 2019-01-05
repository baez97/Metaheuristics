package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import optimization.Configuration;
import optimization.OptimizationAlgorithm;

public class HillClimbing extends OptimizationAlgorithm {

    int nOfNeighbors = 0;
    
    @Override
    public void search() {
        boolean improves = true;
        ArrayList<Configuration> neighbors;
        double bestScore;
        // Algorithms must call this function always!
        initSearch();

        // Generates all the configurations.
        Configuration currentSolution;
        
        // Generates a configuration.
        currentSolution = problem.genRandomConfiguration();
        
        bestScore = this.evaluate(currentSolution);
        
        while ( improves ) {
            improves = false;
            
            neighbors = this.generateNeighbors(currentSolution);
            
            for ( Configuration neighbor : neighbors ) {
                this.evaluate(neighbor);
            }
            
            currentSolution = this.bestSolution;
            
            if ( bestScore < currentSolution.score() ) {
                this.bestScore = currentSolution.score();
                improves = true;
            }
        }
        
        stopSearch();
    }

    @Override
    public void showAlgorithmStats() {
        
    }

    @Override
    public void setParams(String[] args) {
    }
    
    public ArrayList<Configuration> generateNeighbors(Configuration configuration) {
        HashSet<Configuration> result = new HashSet<>();

        int length = configuration.getValues().length;
        Configuration newConfig;
        
        for ( int i = 0; i < length; i++ ) {
            for ( int j = 0; j < length && j!= i; j++ ) {
                newConfig = new Configuration(this.swap(configuration.getValues(), i, j));
                result.add(newConfig);
                this.nOfNeighbors++;
            }
        }

        return new ArrayList<>(result);
    }
    

    public int[] swap(int[] n, int i, int j) {
        int[] m = n.clone();
        int temp = m[i];
        m[i] = m[j];
        m[j] = temp;

        return m;
    }

}
