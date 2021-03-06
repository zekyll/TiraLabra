package chess.domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BitBoardTest
{
	BitBoard bb;

	@Before
	public void setUp()
	{
		bb = new BitBoard();
	}

	@Test
	public void newBitBoardIsEmpty()
	{
		assertEquals(0, bb.getPieces(Players.WHITE));
		assertEquals(0, bb.getPieces(Players.BLACK));
		assertEquals(0, bb.getPieces(Players.WHITE, Pieces.KING));
		assertEquals(0, bb.getPieces(Players.BLACK, Pieces.PAWN));
	}

	@Test
	public void testAddPiece()
	{
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 35);
		bb.addPiece(Players.BLACK, Pieces.BISHOP, 63);
		assertEquals(1L << 35, bb.getPieces(Players.BLACK, Pieces.KNIGHT));
		assertEquals(1L << 63, bb.getPieces(Players.BLACK, Pieces.BISHOP));
		assertEquals(1L << 35 | 1L << 63, bb.getPieces(Players.BLACK));
		assertEquals(0, bb.getPieces(Players.WHITE));
	}

	@Test
	public void testRemovePiece2()
	{
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 63);
		bb.addPiece(Players.BLACK, Pieces.PAWN, 25);
		bb.removePiece(Players.BLACK, Pieces.KNIGHT, 27);
		assertEquals(1L << 63, bb.getPieces(Players.BLACK, Pieces.KNIGHT));
		assertEquals(1L << 63 | 1L << 25, bb.getPieces(Players.BLACK));
	}

	@Test
	public void testRemovePiece3()
	{
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 63);
		bb.addPiece(Players.BLACK, Pieces.PAWN, 25);
		bb.removePiece(Players.BLACK, Pieces.KNIGHT, 27);
		assertEquals(1L << 63, bb.getPieces(Players.BLACK, Pieces.KNIGHT));
		assertEquals(1L << 63 | 1L << 25, bb.getPieces(Players.BLACK));
	}

	@Test
	public void testClear()
	{
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 63);
		bb.addPiece(Players.WHITE, Pieces.PAWN, 25);
		bb.clear();
		assertEquals(0, bb.getPieces(Players.WHITE));
		assertEquals(0, bb.getPieces(Players.BLACK));
		assertEquals(0, bb.getPieces(Players.WHITE, Pieces.PAWN));
		assertEquals(0, bb.getPieces(Players.BLACK, Pieces.KNIGHT));
	}

	@Test
	public void testHasPiece1()
	{
		bb.addPiece(Players.WHITE, Pieces.KNIGHT, 27);
		assertTrue(bb.hasPiece(27));
		assertFalse(bb.hasPiece(28));
	}

	@Test
	public void testHasPiece2()
	{
		bb.addPiece(Players.WHITE, Pieces.KNIGHT, 27);
		assertTrue(bb.hasPiece(Players.WHITE, 27));
		assertFalse(bb.hasPiece(Players.BLACK, 27));
		assertFalse(bb.hasPiece(Players.WHITE, 28));
	}

	@Test
	public void testHasPiece3()
	{
		bb.addPiece(Players.WHITE, Pieces.KNIGHT, 27);
		assertTrue(bb.hasPiece(Players.WHITE, Pieces.KNIGHT, 27));
		assertFalse(bb.hasPiece(Players.WHITE, Pieces.PAWN, 27));
		assertFalse(bb.hasPiece(Players.BLACK, Pieces.KNIGHT, 27));
		assertFalse(bb.hasPiece(Players.WHITE, Pieces.KNIGHT, 28));
	}

	@Test
	public void testGetPlayer()
	{
		bb.addPiece(Players.WHITE, Pieces.QUEEN, 40);
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		assertEquals(Players.WHITE, bb.getPlayer(40));
		assertEquals(Players.BLACK, bb.getPlayer(27));
		assertEquals(-1, bb.getPlayer(26));
	}

	@Test
	public void testClone()
	{
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb.addPiece(Players.BLACK, Pieces.BISHOP, 63);
		bb.addPiece(Players.WHITE, Pieces.PAWN, 25);
		BitBoard bb2 = bb.clone();
		assertEquals(1L << 27, bb2.getPieces(Players.BLACK, Pieces.KNIGHT));
		assertEquals(1L << 63, bb2.getPieces(Players.BLACK, Pieces.BISHOP));
		assertEquals(1L << 27 | 1L << 63, bb2.getPieces(Players.BLACK));
		assertEquals(1L << 25, bb.getPieces(Players.WHITE, Pieces.PAWN));
		assertEquals(1L << 25, bb.getPieces(Players.WHITE));
	}

	@Test
	public void testEqualsTrue()
	{
		BitBoard bb2 = new BitBoard();
		assertTrue(bb.equals(bb2));

		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb.addPiece(Players.BLACK, Pieces.BISHOP, 63);
		bb.addPiece(Players.WHITE, Pieces.PAWN, 25);
		bb2.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb2.addPiece(Players.BLACK, Pieces.BISHOP, 63);
		bb2.addPiece(Players.WHITE, Pieces.PAWN, 25);
		assertTrue(bb2.equals(bb));
	}

	@Test
	public void testEqualsFalse()
	{
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb.addPiece(Players.BLACK, Pieces.BISHOP, 63);
		bb.addPiece(Players.WHITE, Pieces.PAWN, 25);
		BitBoard bb2 = new BitBoard();
		bb2.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb2.addPiece(Players.BLACK, Pieces.BISHOP, 63);
		bb2.addPiece(Players.BLACK, Pieces.PAWN, 25);
		assertFalse(bb.equals(bb2));

		bb2.removePiece(Players.BLACK, Pieces.PAWN, 25);
		bb2.addPiece(Players.WHITE, Pieces.KING, 25);
		assertFalse(bb2.equals(bb));

		bb2.removePiece(Players.WHITE, Pieces.KING, 25);
		bb2.addPiece(Players.BLACK, Pieces.KING, 25);
		bb.removePiece(Players.WHITE, Pieces.PAWN, 25);
		bb.addPiece(Players.BLACK, Pieces.PAWN, 25);
		assertFalse(bb2.equals(bb));
	}

	@Test
	public void testEqualsFalse2()
	{
		bb.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb.addPiece(Players.BLACK, Pieces.BISHOP, 63);
		bb.addPiece(Players.WHITE, Pieces.PAWN, 25);
		BitBoard bb2 = new BitBoard();
		bb2.addPiece(Players.BLACK, Pieces.KNIGHT, 27);
		bb2.addPiece(Players.WHITE, Pieces.PAWN, 25);
		assertFalse(bb.equals(bb2));
	}
}
