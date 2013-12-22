package main;

import java.util.ArrayList;

public class Calc {

	public static ArrayList<Double> convertStringToVals(String vals) {
		vals = vals.replaceAll("(\r)?\n", " ");
		ArrayList<Double> arr = new ArrayList<>();
		String[] strArr = vals.split(" ");
		for (String s : strArr) {
			if (!s.equals(null) && s.length() > 0 && !s.equals("\r\n")) {
				try {
					arr.add(Double.parseDouble(s));
				} catch (NumberFormatException ex) {
					// System.out.print("wrong number format:" + s);
				}
			}
		}

		return arr;
	}

	public static double StandardDeviation(String vals, Double mean) {
		double result = 0;

		ArrayList<Double> arr = convertStringToVals(vals);
		for (Double d : arr) {
			// sum of squares
			result += Math.pow(d - mean, 2);
		}
		// square root of mean
		return Math.sqrt(mean / vals.length());
	}

	public static double MeanValue(String vals) {

		ArrayList<Double> arr = convertStringToVals(vals);
		Double sum = 0.0;
		for (Double d : arr) {
			sum += d;
		}
		return sum / arr.size();
	}
}
