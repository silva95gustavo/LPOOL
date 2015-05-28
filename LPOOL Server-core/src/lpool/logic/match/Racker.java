package lpool.logic.match;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Racker {

	/*
	 * RULES:
	 * 
	 * The 8 ball must be in the center of the rack (the second ball in the three balls wide row).
	 * The first ball must be placed at the apex position (front of the rack and so the center of that ball is directly over the table's foot spot).
	 * The two corner balls must be a stripe and a solid.
	 * All balls other than the 8 ball are placed at random, but in conformance with the preceding corner ball rule.
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
		LinkedList<Integer> free = new LinkedList<Integer>();
		
		for (int i = 1; i <= 15; i++)
		{
			free.add(new Integer(i));
		}
		
		result[1] = 1; // The first ball must be placed at the apex position (front of the rack and so the center of that ball is directly over the table's foot spot).
		result[8] = 5; // The 8 ball must be in the center of the rack (the second ball in the three balls wide row).

		free.remove(new Integer(1));
		free.remove(new Integer(5));
		
		// The two corner balls must be a stripe and a solid.
		LinkedList<Integer> freeP1 = listPlayer1Free(free);
		LinkedList<Integer> freeP2 = listPlayer2Free(free);
		if (r.nextBoolean())
		{
			System.out.println("1... " + freeP1 + freeP2);
			result[11] = removeRandom(r, freeP1);
			result[15] = removeRandom(r, freeP2);
		}
		else
		{
			System.out.println("2... " + freeP1 + freeP2);
			result[11] = removeRandom(r, freeP2);
			result[15] = removeRandom(r, freeP1);
		}
		
		free.remove(new Integer(result[11]));
		free.remove(new Integer(result[15]));
		
		// All balls other than the 8 ball are placed at random, but in conformance with the preceding corner ball rule.
		for (int i = 2; i < 2 * Match.ballsPerPlayer + 2; i++)
		{
			if (i != 8 && i != 11 && i != 15)
			{
				result[i] = removeRandom(r, free);
			}
		}
		
		return result;
	}
	
	private static int removeRandom(Random r, LinkedList<Integer> list)
	{
		return list.remove(r.nextInt(list.size())).intValue();
	}
	
	private static LinkedList<Integer> listPlayer1Free(LinkedList<Integer> list)
	{
		LinkedList<Integer> result = new LinkedList<Integer>();
		for (ListIterator<Integer> it = list.listIterator(); it.hasNext();)
		{
			int curr = it.next();
			if (isPlayer1(curr))
				result.add(curr);
		}
		return result;
	}
	
	private static LinkedList<Integer> listPlayer2Free(LinkedList<Integer> list)
	{
		LinkedList<Integer> result = new LinkedList<Integer>();
		for (ListIterator<Integer> it = list.listIterator(); it.hasNext();)
		{
			int curr = it.next();
			if (isPlayer2(curr))
				result.add(curr);
		}
		return result;
	}
	
	private static boolean isPlayer1(int ID)
	{
		return ID >= 1 && ID <= 7;
	}
	
	private static boolean isPlayer2(int ID)
	{
		return ID >= 9 && ID <= 15;
	}
}
