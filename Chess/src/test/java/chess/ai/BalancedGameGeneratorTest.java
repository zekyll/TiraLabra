package chess.ai;

import chess.domain.GameState;
import chess.domain.Pieces;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BalancedGameGeneratorTest
{
	public BalancedGameGeneratorTest()
	{
	}

	@Before
	public void setUp()
	{
	}

	@Test
	public void returnsValidGame() throws InterruptedException
	{
		GameState state = BalancedGameGenerator.createGame(123, 2.0);
		MinMaxAI testAI = new MinMaxAI(null, 5, 30, 0.0, 0);
		testAI.getMove(state);
		int score = testAI.getSearchTree().score;
		assertTrue(Math.abs(score) < 2.0 * Scores.PIECE_VALUES[Pieces.PAWN]);
	}
}
