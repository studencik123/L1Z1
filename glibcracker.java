package zad2krypto;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class glibcracker
{
    public static BigInteger[] generate(int length)
    {
        BigInteger[] state = new BigInteger[length + 344];
        BigInteger[] sequence = new BigInteger[length];

        state[0] = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
        for(int i = 1; i < 31; i++)
        {
            state[i] = BigInteger.valueOf(16807).multiply(state[i - 1]).mod(BigInteger.valueOf(2147483647));
        }

        for(int i = 31; i < 34; i++)
        {
            state[i] = state[i - 31];
        }

        for(int i = 34; i < 344; i++)
        {
            state[i] = state[i - 31].add(state[i - 3]);
        }

        for(int i = 344; i < state.length; i++)
        {
            state[i] = state[i - 31].add(state[i - 3]);
            sequence[i - 344] = state[i].shiftRight(1);
        }
        
        return sequence;
    }

    public static BigInteger predict(BigInteger[] output)
    {
        return output[output.length - 31].add(output[output.length - 3]);
    }
    
    public static void main(String[] args)
    {
        BigInteger[] sequence = generate(32);
        BigInteger prediction = predict(Arrays.copyOfRange(sequence, 0, 31));

        System.out.println(sequence[31] + "      GLIB");
        System.out.println(prediction + "     PREDICTION ");
    }

}