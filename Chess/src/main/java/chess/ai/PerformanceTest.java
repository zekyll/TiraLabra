package chess.ai;

import chess.domain.GameState;
import chess.domain.Randomizer;
import chess.util.Logger;
import java.util.Random;

/**
 * Suorituskykytesti minmax-tekoälylle.
 */
public class PerformanceTest implements Runnable
{
	/**
	 * Loggeri tulostetta varten.
	 */
	private Logger logger;

	/**
	 * Aloitussyvyys.
	 */
	private int startDepth;

	/**
	 * Kunkin iteraation pituus sekunteina.
	 */
	private double length;

	/**
	 * Iteraation aikana analysoitujen solmujen kokonäismäärä.
	 */
	private long totalNodes = 0;

	/**
	 * Konstruktori.
	 *
	 * @param logger loki
	 */
	public PerformanceTest(Logger logger, int startDepth, double length)
	{
		this.logger = logger;
		this.startDepth = startDepth;
		this.length = length;
	}

	/**
	 * Ajaa sarjan suorituskykytestejä tekoälylle eri hakusyvyyden arvoille. Kullakin hakusyvyydellä
	 * arvotaan satunnaisia pelitilanteita ja lasketaan niihin paras siirto, kunnes aikaa on
	 * käytetty n. 5 sekuntia. Hakusyvyyden kasvatus lopetetaan, jos annetussa ajassa ehdittiin
	 * analysoida vähemmän kuin 10 tilannetta.
	 *
	 * Jokaisella testikerralla käytetään samaa random-seediä, jotta testit olisivat paremmin
	 * vertailukelpoisia.
	 */
	@Override
	public void run()
	{
		logger.logMessage("Running test...");

		int depth = startDepth;
		int n;
		do {
			Random rnd = new Random(123456);

			double totalTime = 0;

			MinMaxAI ai = new MinMaxAI(logger, depth, 30, 0.0, 0);
			n = 0;
			while (totalTime < length) {
				totalTime += runSingleTest(ai, rnd);
				++n;
			}

			double avgTime = totalTime * 1e3 / n;
			logger.logMessage(String.format("d%d: %d %.3fms %.3g", depth, n, avgTime,
					Math.pow(totalNodes / n, 1.0 / depth)));

			++depth;
		} while (n > 10);

		logger.logMessage("Test done.");
	}

	/**
	 * Arpoo satunnaisen pelitlanteen ja laskee siihen parhaan siirron MinMaxAI:n avulla.
	 *
	 * @param ai käytettävä tekoälyobjekti
	 * @param rnd Random-objekti satunnaisen pelitilanteen generoimiseksi
	 * @return palauttaa käytetyn ajan, poislukien pelitilanteen arpomiseen kulunut aika
	 */
	private double runSingleTest(MinMaxAI ai, Random rnd)
	{
		GameState state = Randomizer.createGame(rnd);

		long start = System.nanoTime();
		try {
			int move = ai.getMove(state);
			if (move == 0) // Käytetään tulosta johonkin, ettei operaatiota optimoida pois.
				throw new RuntimeException("Never thrown");
		} catch (InterruptedException e) {
		}
		totalNodes += ai.getNodeCount();
		return (System.nanoTime() - start) * 1e-9;
	}
}
