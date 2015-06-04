package lpool.test;

import static org.junit.Assert.*;
import lpool.logic.match.Racker;

import org.junit.Test;

public class MatchTest {

	@Test
	public void testRacker() {
		for (int i = 0; i < 100; i++)
		{
			rackerTestIteration();
		}
	}

	
	private void rackerTestIteration()
	{
		int[] result = Racker.rack();
		assertEquals(1, result[1]);
		
		boolean[] visited = new boolean[result.length];
		int left = 8;
		int right = 8;
		for (int i = 1; i < result.length; i++)
		{
			assertEquals(false, visited[i]);
			visited[i] = true;
			if (result[i] == 11)
				left = i;
			if (result[i] == 15)
				right = i;
		}
		
		for (int i = 1; i < result.length; i++)
		{
			assertEquals(true, visited[i]);
		}
		
		assertEquals(false, left < 8 && right < 8);
		assertEquals(false, left > 8 && right > 8);
	}
}
