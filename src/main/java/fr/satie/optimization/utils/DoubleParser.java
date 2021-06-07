package fr.satie.optimization.utils;

/**
 * File <b>DoubleParser</b> located on fr.satie.optimization.utils
 * DoubleParser is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * @author Dale Lane {@literal <dale.lane@gmail.com>}
 * Created the 18/05/2021 at 13:26
 * @since 0.2
 * <p>
 * Explanation in http://dalelane.co.uk/blog/?p=2936
 */
public class DoubleParser {

	private static final String   INFINITY               = "Infinity";
	private static final String   NEGINFINITY            = "-Infinity";
	private static final String   NAN                    = "NaN";
	private final static int      PRE_COMPUTED_EXP_RANGE = 256;
	private final static double[] POS_EXPS               = new double[PRE_COMPUTED_EXP_RANGE];
	private final static double[] NEG_EXPS               = new double[PRE_COMPUTED_EXP_RANGE];
	private final static int      LEN_INT                = 9;
	private final static int      LEN_LONG               = 18;

	static {
		for (int i = 0; i < PRE_COMPUTED_EXP_RANGE; i++) {
			POS_EXPS[i] = Math.pow(10.0, i);
			NEG_EXPS[i] = Math.pow(10.0, -i);
		}
	}

	public static double parseDouble(String str) {

		if (str.equals(INFINITY)) {
			return Double.POSITIVE_INFINITY;
		}
		if (str.equals(NEGINFINITY)) {
			return Double.NEGATIVE_INFINITY;
		}
		if (str.equals(NAN)) {
			return Double.NaN;
		}

		int exp = 0;
		str = stripUnnecessaryPlus(str);
		int expStrIdx = getExponentIdx(str);
		if (expStrIdx >= 0) {
			String expStr = stripUnnecessaryPlus(str.substring(expStrIdx + 1));
			exp = Short.parseShort(expStr);
			if (exp > 100 || exp < -100) {
				return Double.parseDouble(str);
			}
			str = str.substring(0, expStrIdx);
		}

		int decPointIdx = str.indexOf(".");
		int numDigits   = str.length();
		if (decPointIdx >= 0) {
			exp -= (numDigits - decPointIdx - 1);
			str = str.substring(0, decPointIdx) + str.substring(decPointIdx + 1);
			numDigits -= 1;
		}

		if (numDigits <= LEN_INT)
			return Integer.parseInt(str) * getExponentValue(exp);
		else if (numDigits <= LEN_LONG)
			return Long.parseLong(str) * getExponentValue(exp);
		else {
			final String mostSignificantDigitsStr = str.substring(0, LEN_LONG);
			final int    expToAdd                 = numDigits - LEN_LONG;
			return Long.parseLong(mostSignificantDigitsStr) * getExponentValue(exp + expToAdd);
		}
	}

	private static int getExponentIdx(String str) {
		int expIdx = str.indexOf("E");
		if (expIdx < 0)
			expIdx = str.indexOf("e");
		return expIdx;
	}

	private static double getExponentValue(int exp) {
		if (exp > -PRE_COMPUTED_EXP_RANGE)
			if (exp <= 0)
				return NEG_EXPS[-exp];
			else if (exp < PRE_COMPUTED_EXP_RANGE)
				return POS_EXPS[exp];
		return Math.pow(10.0, exp);
	}

	private static String stripUnnecessaryPlus(String str) {
		if (str.startsWith("+"))
			return str.substring(1);
		return str;
	}
}
