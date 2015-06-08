package lpool.logic.match;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Generates a valid position for the balls to start, according to the rules of the game.
 * 
 * RULES:
 * The 8 ball must be in the center of the rack (the second ball in the three balls wide row).
 * The first ball must be placed at the apex position (front of the rack and so the center of that ball is directly over the table's foot spot).
 * The two corner balls must be a stripe and a solid.
 * All balls other than the 8 ball are placed at random, but in conformance with the preceding corner ball rule.
 * @author Gustavo
 *
 */
public class Racker {
	
	/**
	 * Static-only class.
	 */
	private Racker() {
	}

	/**
	 * 
	 * @return an array containing the order of the balls (top-bottom, left-right).
	 */
	public static int[] rack()
	{
		int[] result = new int[2 * Match.ballsPerPlayer + 2];

		Random r = new Random();
		LinkedList<Integer> freePos = new LinkedList<Integer>();

		for (int i = 1; i <= 15; i++)
		{
			freePos.add(new Integer(i));
		}

		result[1] = 1; // The first ball must be placed at the apex position (front of the rack and so the center of that ball is directly over the table's foot spot).
		result[8] = 5; // The 8 ball must be in the center of the rack (the second ball in the three balls wide row).

		freePos.remove(new Integer(1));
		freePos.remove(new Integer(5));

		// The two corner balls must be a stripe and a solid.
		LinkedList<Integer> freeStripe = new LinkedList<Integer>();
		LinkedList<Integer> freeSolid = new LinkedList<Integer>();

		for (int i = 0; i < Match.ballsPerPlayer; i++)
		{
			if (i != 0)
				freeSolid.add(i + 1);
			freeStripe.add(i + 9);
		}
		int left;
		int right;
		if (r.nextBoolean())
		{
			left = removeRandom(r, freeSolid);
			right = removeRandom(r, freeStripe);
		}
		else
		{
			left = removeRandom(r, freeStripe);
			right = removeRandom(r, freeSolid);
		}
		result[left] = 11;
		result[right] = 15;
		freePos.remove(new Integer(11));
		freePos.remove(new Integer(15));

		// All balls other than the 8 ball are placed at random, but in conformance with the preceding corner ball rule.
		while (!freeSolid.isEmpty())
		{
			result[freeSolid.poll()] = removeRandom(r, freePos);
		}
		while (!freeStripe.isEmpty())
		{
			result[freeStripe.poll()] = removeRandom(r, freePos);
		}

		return result;
	}

	private static int removeRandom(Random r, LinkedList<Integer> list)
	{
		return list.remove(r.nextInt(list.size())).intValue();
	}
}
