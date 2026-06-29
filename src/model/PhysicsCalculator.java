package model;

public class PhysicsCalculator {

    public static final double COULOMB_CONSTANT = 8.99E9;
    public static final double PICO_TO_COULOMB = 1E-12;
    public static final double CENTIMETER_TO_METER = 1E-2;
    public static final double SQUARE_CHARGE_FACTOR = Math.sqrt(2) - 4;

    public record WorkResult(
            double chargePc,
            double sideCm,
            double chargeCoulomb,
            double sideMeter,
            double workJoule
    ) {
    }

    public double picoToCoulomb(double q) {
        return q * PICO_TO_COULOMB;
    }

    public double cmToMeter(double a) {
        return a * CENTIMETER_TO_METER;
    }

    public WorkResult calculateFromUserUnits(double chargePc, double sideCm) {
        double chargeCoulomb = picoToCoulomb(chargePc);
        double sideMeter = cmToMeter(sideCm);
        double workJoule = calculateWork(chargeCoulomb, sideMeter);

        return new WorkResult(chargePc, sideCm, chargeCoulomb, sideMeter, workJoule);
    }

    public double calculateWork(double chargeCoulomb, double sideMeter) {
        if (sideMeter <= 0) {
            throw new IllegalArgumentException("A distância deve ser maior que zero.");
        }

        return (COULOMB_CONSTANT * Math.pow(chargeCoulomb, 2) / sideMeter) * SQUARE_CHARGE_FACTOR;
    }
}
