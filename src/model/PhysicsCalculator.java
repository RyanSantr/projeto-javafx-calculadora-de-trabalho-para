package model;

public class PhysicsCalculator {

    public static final double COULOMB_CONSTANT = 8.99E9;

    public double picoToCoulomb(double q) {
        return q * 1E-12;
    }

    public double cmToMeter(double a) {
        return a * 1E-2;
    }

    public double calculateWork(double q, double a) {
        if (a <= 0) {
            throw new IllegalArgumentException("A distância deve ser maior que zero.");
        }

        return (COULOMB_CONSTANT * Math.pow(q, 2) / a) * (Math.sqrt(2) - 4);
    }
}
