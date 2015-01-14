package com.grid.structs.models;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.collect.Lists;

public class Publisher {
	final static List<String> WORD_PATTERNS = Lists.newArrayList(
			new String[] {
					"babb",
					"bbabba",
					"babbab",
					"bbaab",
					"babab",
					"baba",
					"abba",
					"abab",
					"bababa",
			});
	final static String COMMON_A = "aeiou";
	final static String COMMON_B = "bcdfghklmnprstvwy";
	
	public static String generateName() {
		final String pattern = WORD_PATTERNS.get(RandomUtils.nextInt(0, WORD_PATTERNS.size() - 1));
		String name = "";
		for (Character letter : pattern.toCharArray()) {
			name += randomLetterByPattern(letter);
		}
		return name;
	}
	
	private static String randomLetterByPattern(Character letter) {
		switch (letter) {
		case 'a':
			return COMMON_A.charAt(RandomUtils.nextInt(0, COMMON_A.length() - 1)) + "";
		case 'b':
			return COMMON_B.charAt(RandomUtils.nextInt(0, COMMON_B.length() - 1)) + "";
		case '0':
			return RandomUtils.nextInt(0, 9) + "";
		default:
			return letter + "";
		}
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(Publisher.generateName());
		}
	}
}
