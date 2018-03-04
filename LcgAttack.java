package kryptoL1Z1;
/*
 * https://security.stackexchange.com/questions/4268/cracking-a-linear-congruential-generator
*/

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public final class LcgAttack
{
	public static BigInteger seed;
	public static BigInteger xn1;
	public static int sequenceLength = 10;

    public static void main(String[] args)
    {
        BigInteger[] sequence = generate(sequenceLength);
        
        for(int i = 0; i < sequenceLength; i++)
        {
        	System.out.println("sequence i = " + i + "\nx = " + sequence[i] + "\n");
        }
        
        BigInteger m = computeM(sequence);
        BigInteger a = computeA(sequence[2], sequence[1], sequence[0], m);
        BigInteger b = computeB(sequence[1], sequence[0], a, m);

        System.out.println("Computed m = " + m);
        System.out.println("Computed a = " + a);
        System.out.println("Computed b = " + b + "\n");
        
        BigInteger[] sequenceComputed = generateFromComputed(sequenceLength, m, a, b, xn1);
        
        for(int i = 0; i < sequenceLength; i++)
        {
        	System.out.println("sequenceComputed i = " + i + "\nx = " + sequenceComputed[i] + "\n");
        }
        System.out.println("Attack completed.");
    }

    public static BigInteger[] generate(int length)
    {
        BigInteger m = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
        BigInteger a = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(1, m.longValue()));
        BigInteger b = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(1, m.longValue()));

        seed = BigInteger.valueOf(ThreadLocalRandom.current().nextLong(0, m.longValue()));
        xn1 = seed;
      
        BigInteger[] sequence = new BigInteger[length];
        for(int i = 0; i < length; i++)
        {
            sequence[i] = a.multiply(seed).add(b).mod(m);
            seed = sequence[i];
        }

        System.out.println("Chosen m = " + m);
        System.out.println("Chosen a = " + a);
        System.out.println("Chosen b = " + b + "\n");
        return sequence;
    }

    public static BigInteger[] generateFromComputed(int length, BigInteger m, BigInteger a, BigInteger b, BigInteger xn1)
    {
    	
    	BigInteger[] sequence = new BigInteger[length];
    	
    	for(int i = 0; i < length; i++)
    	{
    		sequence[i] = a.multiply(xn1).add(b).mod(m);
    		xn1 = sequence[i]; 
    	}
    	
    	return sequence;
    }
    
    public static BigInteger computeM(BigInteger[] sequence)
    {
        BigInteger[] t_sequence = new BigInteger[sequence.length - 1];
        BigInteger[] u_sequence = new BigInteger[sequence.length - 2];

        for(int i = 0; i < sequence.length - 1; i++)
        {
            t_sequence[i] = sequence[i + 1].subtract(sequence[i]);
        }

        for(int i = 0; i < t_sequence.length - 2; i++)
        {
            u_sequence[i] = t_sequence[i + 2].multiply(t_sequence[i]).subtract(t_sequence[i + 1].pow(2)).abs();
        }

        return gcd(u_sequence);
    }

    public static BigInteger gcd(BigInteger[] input)
    {
        BigInteger result = input[0];
        for(int i = 1; i < input.length - 1; i++)
        {
            result = gcd(result, input[i]);
        }

        return result;
    }

    public static BigInteger gcd(BigInteger a, BigInteger b)
    {
        while(b.compareTo(BigInteger.ZERO) > 0)
        {
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }

        return a;
    }

    public static BigInteger computeA(BigInteger x2, BigInteger x1, BigInteger x0, BigInteger m)
    {
        BigInteger a = null;
        try
        {
            a = x2.subtract(x1).multiply(x1.subtract(x0).modInverse(m)).mod(m);
        }
        catch(ArithmeticException e)
        {
            System.out.println("Attack failed.");
            System.exit(0);
        }

        return a;
    }

    public static BigInteger computeB(BigInteger x1, BigInteger x0, BigInteger a, BigInteger m)
    {
        return x1.subtract(a.multiply(x0)).mod(m);
    }
}
