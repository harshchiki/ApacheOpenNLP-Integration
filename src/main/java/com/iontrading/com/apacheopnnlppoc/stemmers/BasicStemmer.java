package com.iontrading.com.apacheopnnlppoc.stemmers;

import java.util.Arrays;

import opennlp.tools.stemmer.PorterStemmer;

public class BasicStemmer {
	public static void main(String[] args) {
		new BasicStemmer().performStemming();
	}
	public void performStemming(){
		PorterStemmer stemmer = new PorterStemmer();
		
		
		
		String s = "open the following below above these book against with for from by them with roll break bid offer offered";
		
		Arrays.stream(s.split(" ")).forEach(st -> {
			System.out.println(st);
			System.out.println(stemmer.stem(st));
			System.out.println("Length = "+stemmer.getResultLength());
			System.out.println();
		});
		
//		System.out.println(stemmer.stem("banking"));
//		System.out.println("Length = "+stemmer.getResultLength());
	}
}
