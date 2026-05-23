package geneticalgorithm;

import java.util.*;

/**
 * Основной класс генетического алгоритма
 */
public class GeneticAlgorithm {
    private List<Chromosome> population;
    private int populationSize;
    private int maxGenerations;
    private double crossoverProbability;
    private double mutationProbability;

    // Настройки операторов
    private boolean useBlanketStrategy;      // true = Одеяло, false = Фокусировка
    private boolean useRandomSelection;      // true = Случайная, false = По шкале
    private int crossoverType;               // 1=Одноточечный, 2=Двухточечный, 3=Циклический
    private int mutationType;                // 1=Простая, 2=Инверсия

    private Random random;
    private Chromosome bestChromosome;

    // История для построения графика
    private List<Double> historyBestFitness = new ArrayList<>();

    public GeneticAlgorithm(int populationSize, int maxGenerations,
                            double crossoverProbability, double mutationProbability,
                            boolean useBlanketStrategy, boolean useRandomSelection,
                            int crossoverType, int mutationType) {
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.useBlanketStrategy = useBlanketStrategy;
        this.useRandomSelection = useRandomSelection;
        this.crossoverType = crossoverType;
        this.mutationType = mutationType;
        this.random = new Random();
        this.population = new ArrayList<>();
    }

    public Chromosome run() {
        historyBestFitness.clear();

        // 1. Инициализация
        initializePopulation();

        // 2. Эволюция
        for (int generation = 0; generation < maxGenerations; generation++) {
            evaluateFitness();

            List<Chromosome> selected = selection();
            List<Chromosome> offspring = crossover(selected);
            mutate(offspring);

            population = elitistSelection(population, offspring);
            updateBestChromosome(generation);

            // Сохраняем лучшее значение для графика
            historyBestFitness.add(bestChromosome.getFitness());
        }

        return bestChromosome;
    }

    // Получение истории для графика
    public List<Double> getHistory() {
        return new ArrayList<>(historyBestFitness);
    }

    private void initializePopulation() {
        population.clear();
        if (useBlanketStrategy) {
            // Стратегия "Одеяло": равномерное распределение
            for (int i = 0; i < populationSize; i++) {
                population.add(new Chromosome());
            }
        } else {
            // Стратегия "Фокусировка": на интервале [5, 10], так как функция растет
            for (int i = 0; i < populationSize; i++) {
                double focusedX = 5 + random.nextDouble() * 5;
                String binary = encodeX(focusedX);
                population.add(new Chromosome(binary));
            }
        }
    }

    private String encodeX(double x) {
        int maxDecimal = (int)Math.pow(2, 16) - 1;
        int decimal = (int)((x - 1.0) / 9.0 * maxDecimal);
        String binary = Integer.toBinaryString(decimal);
        while (binary.length() < 16) binary = "0" + binary;
        return binary;
    }

    private void evaluateFitness() {
        for (Chromosome chr : population) {
            chr.calculateFitness();
        }
    }

    private List<Chromosome> selection() {
        List<Chromosome> selected = new ArrayList<>();
        if (useRandomSelection) {
            // Случайная селекция
            for (int i = 0; i < populationSize; i++) {
                selected.add(population.get(random.nextInt(population.size())).copy());
            }
        } else {
            // Селекция по шкале (Рулетка)
            double sumFitness = population.stream().mapToDouble(Chromosome::getFitness).sum();
            for (int i = 0; i < populationSize; i++) {
                double r = random.nextDouble() * sumFitness;
                double current = 0;
                for (Chromosome chr : population) {
                    current += chr.getFitness();
                    if (current >= r) {
                        selected.add(chr.copy());
                        break;
                    }
                }
            }
        }
        return selected;
    }

    private List<Chromosome> crossover(List<Chromosome> parents) {
        List<Chromosome> offspring = new ArrayList<>();
        for (int i = 0; i < parents.size(); i += 2) {
            Chromosome p1 = parents.get(i);
            Chromosome p2 = (i + 1 < parents.size()) ? parents.get(i + 1) : parents.get(0);

            if (random.nextDouble() < crossoverProbability) {
                // Выбор типа кроссинговера
                if (crossoverType == 1) offspring.addAll(singlePointCrossover(p1, p2));
                else if (crossoverType == 2) offspring.addAll(twoPointCrossover(p1, p2));
                else offspring.addAll(cyclicCrossover(p1, p2)); // Упрощенный
            } else {
                offspring.add(p1.copy());
                offspring.add(p2.copy());
            }
        }
        return offspring;
    }

    private List<Chromosome> singlePointCrossover(Chromosome p1, Chromosome p2) {
        int k = 1 + random.nextInt(15);
        String c1 = p1.getBinaryString().substring(0, k) + p2.getBinaryString().substring(k);
        String c2 = p2.getBinaryString().substring(0, k) + p1.getBinaryString().substring(k);
        return Arrays.asList(new Chromosome(c1), new Chromosome(c2));
    }

    private List<Chromosome> twoPointCrossover(Chromosome p1, Chromosome p2) {
        int k1 = random.nextInt(14);
        int k2 = k1 + 1 + random.nextInt(15 - k1 - 1);
        String b1 = p1.getBinaryString();
        String b2 = p2.getBinaryString();
        String c1 = b1.substring(0, k1) + b2.substring(k1, k2) + b1.substring(k2);
        String c2 = b2.substring(0, k1) + b1.substring(k1, k2) + b2.substring(k2);
        return Arrays.asList(new Chromosome(c1), new Chromosome(c2));
    }

    private List<Chromosome> cyclicCrossover(Chromosome p1, Chromosome p2) {
        // Упрощенная реализация для бинарного кодирования (аналогично одноточечному)
        return singlePointCrossover(p1, p2);
    }

    private void mutate(List<Chromosome> pop) {
        for (Chromosome chr : pop) {
            if (random.nextDouble() < mutationProbability) {
                if (mutationType == 1) chr.mutateSimple();
                else chr.mutateInversion();
            }
        }
    }

    private List<Chromosome> elitistSelection(List<Chromosome> current, List<Chromosome> offspring) {
        List<Chromosome> combined = new ArrayList<>();
        combined.addAll(current);
        combined.addAll(offspring);
        combined.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        List<Chromosome> newPop = new ArrayList<>();
        for (int i = 0; i < populationSize && i < combined.size(); i++) {
            newPop.add(combined.get(i).copy());
        }
        return newPop;
    }

    private void updateBestChromosome(int generation) {
        for (Chromosome chr : population) {
            if (bestChromosome == null || chr.getFitness() > bestChromosome.getFitness()) {
                bestChromosome = chr.copy();
                bestChromosome.setGeneration(generation);
            }
        }
    }
}