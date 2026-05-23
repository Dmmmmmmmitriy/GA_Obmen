package geneticalgorithm;

import org.junit.Test;
import static org.junit.Assert.*;

public class GeneticAlgorithmTest {

    @Test
    public void testChromosomeCreation() {
        Chromosome chr = new Chromosome();
        assertNotNull(chr.getBinaryString());
        assertEquals(16, chr.getBinaryString().length());
        assertTrue(chr.getX() >= 1.0 && chr.getX() <= 10.0);
    }

    @Test
    public void testFitnessCalculation() {
        // Для x=10: f(10) = 3*1000 - 20 + 5 = 2985
        String binary = "1111111111111111"; // максимум
        Chromosome chr = new Chromosome(binary);
        assertTrue(chr.getFitness() > 2900);
    }

    @Test
    public void testAlgorithmRun() {
        GeneticAlgorithm ga = new GeneticAlgorithm(
                10, 20, 0.7, 0.2, true, true, 1, 1
        );
        Chromosome best = ga.run();
        assertNotNull(best);
        assertTrue(best.getFitness() > 0);
    }
}