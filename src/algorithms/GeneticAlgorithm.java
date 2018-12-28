package algorithms;

import java.util.ArrayList;
import utils.Utils;
import optimization.Configuration;
import optimization.OptimizationAlgorithm;

public class GeneticAlgorithm extends OptimizationAlgorithm{
    
    int populationSize;
    int nGenerations;
    double probCrossover;
    double probMutation;
    
    ArrayList<Configuration> population;
    ArrayList<Configuration> populationAux;
    

    @Override
    public void search() {
        initSearch();
        
        int generationsCounter = 1;
        System.out.println(nGenerations);
        System.out.println(populationSize);

        this.population = generatePopulation(this.populationSize);
        
        this.evaluate(population);
        
        while ( generationsCounter <= nGenerations ) {
            populationAux = this.selection(population);
            this.crossover(populationAux);
            this.mutation(populationAux);
            this.evaluate(populationAux);
            this.population = this.combine(this.population, populationAux);
            generationsCounter++;
        }
        
        
        stopSearch();
    }

    public ArrayList<Configuration> clonePopulation(ArrayList<Configuration> population) {
        ArrayList<Configuration> result = new ArrayList<>();
        
        for ( Configuration c : population ) {
            result.add(c.clone());
        }
        
        return result;
    }
    
    public ArrayList<Configuration> selection(ArrayList<Configuration> population) {
        ArrayList<Configuration> result = new ArrayList<>();
        Configuration individual;

        for ( int i = 0; i < population.size(); i++ ) {
            individual = this.selectIndividual(population);
            result.add(individual.clone());
        }
        
        return result;
    }
    
    // Tournament based selection with S=2
    public Configuration selectIndividual(ArrayList<Configuration> population) {
        int indexA = Utils.random.nextInt(population.size());
        int indexB = indexA;
        
        while ( indexB == indexA ) {
            indexB = Utils.random.nextInt(population.size());
        }
        
        Configuration indA = population.get(indexA);
        Configuration indB = population.get(indexB);
        
        if ( indA.score() < indB.score() ) {
            return indA;
        } else {
            return indB;
        }
    }
    
    // Two point crossover
    public void crossover(ArrayList<Configuration> population) {
        Configuration parent1, parent2;
        Configuration[] sons;
        ArrayList<Configuration> newPopulation = new ArrayList<>();
        
        for (int i = 0; i < population.size(); i+=2) {
            parent1 = population.get(i);
            parent2 = population.get(i+1);
            
            if ( Utils.random.nextDouble() <= probCrossover ) {
                sons = crossIndividuals(parent1, parent2);
                newPopulation.add(sons[0]);
                newPopulation.add(sons[1]);
            } else {
                newPopulation.add(parent1);
                newPopulation.add(parent2);
            }
        }
        
        population = newPopulation;
    }
    
    // Auxiliar method that crosses two given configurations
    public Configuration[] crossIndividuals(Configuration c1, Configuration c2) {
        int[] parent1 = c1.getValues();
        int[] parent2 = c2.getValues();
        
        int r = Utils.random.nextInt(parent1.length -1) +1;
        int l = Utils.random.nextInt(r);
        
        int[] child1 = new int[parent1.length];
        int[] child2 = new int[parent1.length];
        
        ArrayList<Integer> remaining1 = new ArrayList<>();
        ArrayList<Integer> remaining2 = new ArrayList<>();
        
        for ( int i = 0; i < parent1.length; i++ ) {
            if ( i <= l || i > r ) {
                child1[i] = parent1[i];
                child2[i] = parent2[i];
            } else {
                remaining1.add(parent1[i]);
                remaining2.add(parent2[i]);
            }
        }
        
        ArrayList<Integer> sorted1 = sortByCollection(remaining1, parent2);
        ArrayList<Integer> sorted2 = sortByCollection(remaining2, parent1);

        for ( int i = l+1; i <= r; i++ ) {
            child1[i] = sorted1.get(i- (l+1));
            child2[i] = sorted2.get(i- (l+1));
        }
        
        Configuration conf1 = new Configuration(child1);
        Configuration conf2 = new Configuration(child2);
        
        Configuration[] result = new Configuration[2];
        result[0] = conf1;
        result[1] = conf2;
        return result;
    }
    
    public ArrayList<Integer> sortByCollection(ArrayList<Integer> wantToSort, int[] collection) {
        ArrayList<Integer> sorted = new ArrayList<>();
        for ( int i = 0; i < collection.length; i++ ) {
            for ( Integer n : wantToSort ) {
                if ( n.equals(collection[i]) ) {
                    sorted.add(n);
                    wantToSort.remove(n);
                    break;
                }
            }
        }

        return sorted;
    }
    
    // Mutation of each element of the configuration by swapping with a probability of probMutation (~= 0.9)
    public void mutation(ArrayList<Configuration> population) {
        int[] values, mutatedValues;
        for ( int i = 0; i < population.size(); i++ ) {
            values = population.get(i).getValues();
            for ( int j = 0; j < values.length; j++ ) {
                if ( Utils.random.nextDouble() <= probMutation ) {
                    mutate(population.get(i), j);
                }
            }
        }
    }
    
    // Auxiliar method that swaps the element at index index1 with another element at a random (different) index
    public void mutate(Configuration c, int index1) {
        int values[] = c.getValues();
        int index2 = index1;
        
        while (index2 == index1) {
            index2 = Utils.random.nextInt(values.length);
        }
        
        int temp = values[index1];
        values[index1] = values[index2];
        values[index2] = temp;
    }
    
    // Replacement by elitism
    public ArrayList<Configuration> combine( ArrayList<Configuration> population, ArrayList<Configuration> populationAux) {
        ArrayList<Configuration> result = new ArrayList<>();
        result = populationAux;
        result.remove(getWorst(populationAux));
        result.add(getBest(population));
        
        return result;
    }
    
    public Configuration getWorst(ArrayList<Configuration> population) {
        double worstScore = Double.MIN_VALUE;
        Configuration worstConfig = population.get(0);
        
        for ( Configuration c : population ) {
            if ( c.score() > worstScore ) {
                worstScore = c.score();
                worstConfig = c;
            }
        }
        
        return worstConfig;
    }
    
    public Configuration getBest(ArrayList<Configuration> population) {
        double bestSc = Double.MAX_VALUE;
        Configuration bestConfig = population.get(0);
        
        for ( Configuration c : population ) {
            if ( c.score() < bestSc ) {
                bestSc = c.score();
                bestConfig = c;
            }
        }
        
        return bestConfig;
    }
    
    // Generates a population with size random configurations
    public ArrayList<Configuration> generatePopulation(int size) {
        ArrayList<Configuration> result = new ArrayList<>();
        
        for ( int i = 0; i < size; i++ ) {
            result.add(problem.genRandomConfiguration());
        }
        
        return result;
    }
    
    // (Polimorphic method) that evaluates all the configurations of a population
    public void evaluate(ArrayList<Configuration> population) {
        for (Configuration c : population) {
            this.evaluate(c);
        }
    }
    @Override
    public void showAlgorithmStats() {
    }

    @Override
    public void setParams(String[] args) {
        if ( args.length == 4 ) {
            try {
                this.populationSize = Integer.parseInt(args[0]);
                this.nGenerations   = Integer.parseInt(args[1]);
                this.probCrossover  = Double.parseDouble(args[2]);
                this.probMutation   = Double.parseDouble(args[3]);
            } catch( Exception e ) {
                System.out.println("Parameters are incorrect, they must be:");
                System.out.println("Population size, number of generations, probability of crossover and probability of mutation");
                System.exit(-1);
            } 
        } else {
            System.out.println("Genetic algorithms require 4 parameters:");
            System.out.println("Population size, number of generations, probability of crossover and probability of mutation");
            System.exit(-1);
        }
    }
    

}
