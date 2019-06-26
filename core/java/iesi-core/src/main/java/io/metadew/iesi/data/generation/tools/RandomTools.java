package io.metadew.iesi.data.generation.tools;

import java.security.SecureRandom;
import java.util.List;

public class RandomTools {

    private SecureRandom secureRandom;

    public RandomTools() {
        secureRandom = new SecureRandom();
    }

    public <T> T sample(List<T> options) {
        return options.get(secureRandom.nextInt(options.size()));
    }

    public String digit() {
        return Integer.toString(secureRandom.nextInt(10));
    }

    public boolean randBoolean() {
        return secureRandom.nextBoolean();
    }

    public int number(int max) {
        return secureRandom.nextInt(max);
    }

    public long number(long max) {
        return Math.abs(secureRandom.nextLong()) % max;
    }

    public int numberByLength(int length) {
        return secureRandom.nextInt((int) Math.pow(10, length));
    }

    public int range(int min, int max) {
        if (min == max) {
            return min;
        }
        if (max < min) {
            int temp = max;
            max = min;
            min = temp;
        }
        return number(max - min + 1) + min;
    }

    public long range(long min, long max) {
        if (min == max) {
            return min;
        }
        if (max < min) {
            long temp = max;
            max = min;
            min = temp;
        }
        return number(max - min + 1) + min;
    }

    public double range(double min, double max) {
        if (max < min) {
            double temp = max;
            max = min;
            min = temp;
        }
        return secureRandom.nextDouble() * (max - min) + min;
    }

    public double randDouble() {
        return secureRandom.nextDouble();
    }
}
