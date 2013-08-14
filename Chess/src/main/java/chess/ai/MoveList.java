package chess.ai;

import chess.domain.GameState;
import chess.domain.Move;
import chess.domain.Pieces;
import java.util.Arrays;

/**
 * Ylläpitää sallituista siirroista listaa, joka on jaettu useampaan eri prioriteettiluokkaan.
 * Tarkoituksena on siirtojen tehokas järjestäminen alfa-beta-karsintaa varten.
 */
public final class MoveList
{
	/**
	 * Eri prioriteettiluokkien kokonaismäärä.
	 */
	public static final int PRIORITIES = 13;

	/**
	 * Siirrot jaettuna useaan listaan siten, että jokaiselle prioriteetille on oma listansa.
	 * Maksimi siirtojen määrä missään positiossa on 218, joten 256 on riittävä taulukon koko.
	 */
	private final int[][] moves = new int[PRIORITIES][256];

	/**
	 * Siirtojen lukumäärä kussakin listassa.
	 */
	private final int[] moveCounts = new int[PRIORITIES];

	/**
	 * Bittimaskin korotettaville sotilaille.
	 */
	private static final long[] PROMOTABLE = {0x000000000000FF00L, 0x00FF000000000000L};

	/**
	 * Täyttää siirtolistan sisällön annetusta pelitilanteesta.
	 *
	 * @param state pelitilanne
	 */
	public void populate(GameState state)
	{
		int player = state.getNextMovingPlayer();
		clear();

		// Muut kuin sotilaat.
		for (int pieceType = 0; pieceType < Pieces.COUNT; ++pieceType) {
			long pieces = state.getPieces(player, pieceType);
			addMoves(state, pieceType, pieces, -1);
		}

		// Korotettavat sotilaat.
		long pieces = state.getPieces(player, Pieces.PAWN) & PROMOTABLE[player];
		if (pieces != 0) {
			for (int promotedType = Pieces.QUEEN; promotedType <= Pieces.KNIGHT; ++promotedType)
				addMoves(state, Pieces.PAWN, pieces, promotedType);
		}

		// Ei-korotettavat sotilaat.
		pieces = state.getPieces(player, Pieces.PAWN) & ~PROMOTABLE[player];
		addMoves(state, Pieces.PAWN, pieces, -1);
	}

	/**
	 * Lisää siirrot kaikille annetun tyyppisille nappuloille.
	 *
	 * @param state pelitilanne
	 * @param pieceType nappulatyyppi
	 * @param pieces nappuloiden sijainnit bittimaskina
	 * @param promotedType korotuksen tyyppi
	 */
	private void addMoves(GameState state, int pieceType, long pieces, int promotedType)
	{
		int player = state.getNextMovingPlayer();

		for (; pieces != 0; pieces -= Long.lowestOneBit(pieces)) {
			int fromSqr = Long.numberOfTrailingZeros(pieces);
			long moves = state.getPseudoLegalMoves(player, pieceType, fromSqr);
			for (int capturedType = 0; capturedType < Pieces.COUNT; ++capturedType) {
				long captureMoves = moves & state.getPieces(1 - player, capturedType);
				for (; captureMoves != 0; captureMoves -= Long.lowestOneBit(captureMoves)) {
					int toSqr = Long.numberOfTrailingZeros(captureMoves);
					add(pieceType, fromSqr, toSqr, capturedType, promotedType);
				}
			}

			long quietMoves = moves & ~state.getPieces(1 - player);
			for (; quietMoves != 0; quietMoves -= Long.lowestOneBit(quietMoves)) {
				int toSqr = Long.numberOfTrailingZeros(quietMoves);
				add(pieceType, fromSqr, toSqr, -1, promotedType);
			}
		}
	}

	/**
	 * Palauttaa siirtojen lukumäärän annetussa prioriteettiluokassa.
	 *
	 * @param priority prioriteetti
	 * @return siirtojen lukumäärä
	 */
	public int getCount(int priority)
	{
		return moveCounts[priority];
	}

	/**
	 * Palauttaa siirtojen lukumäärän annetussa prioriteettiluokassa.
	 *
	 * @param priority prioriteetti
	 * @return siirtojen lukumäärä
	 */
	public int getMove(int priority, int idx)
	{
		return moves[priority][idx];
	}

	/**
	 * Tyhjentää siirtolistan.
	 */
	private void clear()
	{
		Arrays.fill(moveCounts, 0);
	}

	/**
	 * Lisää uuden siirron listaan. Prioriteetit asetetaan seruvaavasti:
	 * korotukset: 5
	 * lyönnit: 0-10 (0 on PxK ja 10 on KxP)
	 * muut siirrot: 11
	 */
	private void add(int pieceType, int fromSqr, int toSqr, int capturedType, int promotedType)
	{
		int priority = 12;
		if (promotedType != -1)
			priority = 5;
		else if (capturedType != -1)
			priority = capturedType - pieceType + 5;
		int idx = moveCounts[priority]++;
		moves[priority][idx] = Move.pack(fromSqr, toSqr, pieceType, capturedType, promotedType);
	}
}