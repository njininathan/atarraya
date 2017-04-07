/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package atarraya.element.commmodel;

import atarraya.constants;

/**
 *
 * @author pwightman
 */
public class MICA2CommModel implements constants, CommModel{

    double lambda, lp, ptx, prx, N, cn, fbbw, ebno=0;
    
    
        // Implements the Gauss error function.
    // erf(z) = 2 / sqrt(pi) * integral(exp(-t*t), t = 0..z)
    // Copyright © 2007, Robert Sedgewick and Kevin Wayne.
    // Last updated: Tue Sep 29 16:17:41 EDT 2009.
    // http://www.cs.princeton.edu/introcs/21function/ErrorFunction.java.html

    // fractional error in math formula less than 1.2 * 10 ^ -7.
    // although subject to catastrophic cancellation when z in very close to 0
    // from Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2
    public static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                            t * ( 1.00002368 +
                                            t * ( 0.37409196 +
                                            t * ( 0.09678418 +
                                            t * (-0.18628806 +
                                            t * ( 0.27886807 +
                                            t * (-1.13520398 +
                                            t * ( 1.48851587 +
                                            t * (-0.82215223 +
                                            t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    }

    // Gaussian Q-function
    // Check document "Q function and error function"
    // http://www.eng.tau.ac.il/~jo/academic/Q.pdf
    public static double Q(double x) {
        return 0.5 * (1 - erf(x / Math.sqrt(2)));
    }
    
    
    public double calcBER(double distance){
    
        lambda = 299792458 / 915E6; // f: 915 MHz
        lp = 22 + 20*Math.log10(distance / lambda); // Path loss
        ptx = 10*Math.log10(1); // Ptx = 1 mW (2 dBi antenna)
        prx = ptx - lp - 18; // Fade margin 18 dB, for 900 MHz
        N = 10*Math.log10(1.3806503E-23 * 290 * 500E3 / 1E-3); // Bandwidth 500 KHz
        cn = prx - N;
        fbbw = 10*Math.log10(19200 / 500E3); // Bitrate 19200 bps
        ebno = cn + fbbw;
        // BER for binary orthogonal FSK (M = 2)
        double bit_error_rate = Q(Math.sqrt(Math.pow(10, ebno/10))); // Equation (8.42)
        
        return bit_error_rate;
    }
    
}
