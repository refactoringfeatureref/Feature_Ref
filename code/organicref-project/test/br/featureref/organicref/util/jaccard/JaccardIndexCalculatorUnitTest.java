package br.featureref.organicref.util.jaccard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class JaccardIndexCalculatorUnitTest {
	
	@Test
	public void distanceBetweenIdenticalLists_shouldReturn_zero() {
		List<String> left = new ArrayList<>();
		List<String> right = new ArrayList<>();
		
		left.add("save");
		right.add("save");
		
		Double distance = JaccardIndexCalculator.distance(left, right);
		assertEquals(0d, distance, 0.00001d);
	}
	
	@Test
	public void distanceBetweenHalfIdenticalLists_shouldReturn_0_5() {
		List<String> left = new ArrayList<>();
		List<String> right = new ArrayList<>();
		
		left.add("save");
		left.add("update");
		left.add("delete");
		left.add("find");
		
		right.add("save");
		right.add("update");
		
		Double distance = JaccardIndexCalculator.distance(left, right);
		assertEquals(0.5d, distance, 0.00001d);
	}
	
	@Test
	public void distanceBetweenDisjunctLists_shouldReturn_one() {
		List<String> left = new ArrayList<>();
		List<String> right = new ArrayList<>();
		
		left.add("delete");
		left.add("find");
		
		right.add("save");
		right.add("update");
		
		Double distance = JaccardIndexCalculator.distance(left, right);
		assertEquals(1d, distance, 0.00001d);
	}

}
