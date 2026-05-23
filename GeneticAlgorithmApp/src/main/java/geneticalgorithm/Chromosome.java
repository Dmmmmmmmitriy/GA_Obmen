package geneticalgorithm;

/**
 * Класс хромосомы (особи)
 */
public class Chromosome {
    private String binaryCode;
    private double x;
    private double fitness;
    private int generation;

    // Параметры задачи
    private static final double MIN_X = 1.0;
    private static final double MAX_X = 10.0;
    private static final int CHROMOSOME_LENGTH = 16; // Длина бинарной строки

    public Chromosome() {
        generateRandom();
        decode();
        calculateFitness();
    }

    public Chromosome(String binaryCode) {
        this.binaryCode = binaryCode;
        decode();
        calculateFitness();
    }

    private void generateRandom() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            sb.append(Math.random() < 0.5 ? "0" : "1");
        }
        this.binaryCode = sb.toString();
    }

    // Декодирование бинарного кода в значение x
    private void decode() {
        int decimal = Integer.parseInt(binaryCode, 2);
        int maxDecimal = (int)Math.pow(2, CHROMOSOME_LENGTH) - 1;
        this.x = MIN_X + (MAX_X - MIN_X) * decimal / maxDecimal;
    }

    // Расчет целевой функции: f(x) = 3x^3 - 2x + 5
    void calculateFitness() {
        this.fitness = 3 * Math.pow(x, 3) - 2 * x + 5;
    }

    // Мутация: инверсия одного бита
    public void mutateSimple() {
        int pos = (int)(Math.random() * binaryCode.length());
        char[] chars = binaryCode.toCharArray();
        chars[pos] = (chars[pos] == '0') ? '1' : '0';
        this.binaryCode = new String(chars);
        decode();
        calculateFitness();
    }

    // Мутация: инверсия подстроки (переворот)
    public void mutateInversion() {
        int start = (int)(Math.random() * (binaryCode.length() - 1));
        int end = start + 1 + (int)(Math.random() * (binaryCode.length() - start - 1));

        String part1 = binaryCode.substring(0, start);
        String part2 = new StringBuilder(binaryCode.substring(start, end)).reverse().toString();
        String part3 = binaryCode.substring(end);

        this.binaryCode = part1 + part2 + part3;
        decode();
        calculateFitness();
    }

    public Chromosome copy() {
        Chromosome copy = new Chromosome(this.binaryCode);
        copy.generation = this.generation;
        return copy;
    }

    // Getters
    public String getBinaryString() { return binaryCode; }
    public double getX() { return x; }
    public double getFitness() { return fitness; }
    public int getGeneration() { return generation; }

    public void setGeneration(int generation) { this.generation = generation; }
}