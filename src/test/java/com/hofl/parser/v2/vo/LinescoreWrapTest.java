package com.hofl.parser.v2.vo;

import com.hofl.parser.v2.BoxScoreAssembler;
import java.io.File;
import java.net.URL;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for extra-inning games that go past the 12 columns DMB fits on the
 * first linescore row. Past that, the box score wraps onto additional linescore
 * headers ("                      13 14 15 16 17 18 19 20 21 22 23 24" and, if the
 * game runs even longer, "                      25     R  H  E   LOB DP") -- the last
 * of which doesn't match BoxScoreAssembler.linescoreHeaderPattern's fixed-column
 * layout for the normal single-line case, so it (and the team totals row that comes
 * with it, since DMB only prints R/H/E/LOB/DP once, on whichever linescore row is
 * last) was silently skipped entirely: wrapped innings missing from the linescore, and
 * every team total (runs, hits, errors, LOB, double plays) defaulting to 0 instead of
 * the real values.
 */
public class LinescoreWrapTest {

    private Game loadGame(String fixture) throws Exception {
        URL url = getClass().getClassLoader().getResource("fixtures/" + fixture);
        BoxScoreAssembler box = new BoxScoreAssembler(new File(url.toURI()));
        return box.getGame();
    }

    /**
     * JER26-TEX26, 3/14/2026: 13 innings, two linescore blocks (1-12, then 13 + totals).
     * Real totals per the box score: Fugitives R=3 H=12 E=2 LOB=13 DP=4, Chain Saw
     * Scalpers R=2 H=6 E=0 LOB=7 DP=1; Fugitives scored 1 run in the top 13th.
     */
    @Test
    public void thirteenthInningIsCapturedInTheLinescore() throws Exception {
        Game game = loadGame("2026031400190.box");
        Map<Integer, InningScore> linescores = game.getLinescores();

        assertTrue(linescores.containsKey(13), "the wrapped 13th inning should appear in the linescore");
        InningScore thirteenth = linescores.get(13);
        assertEquals(1, thirteenth.getAwayTeamRuns(), "Fugitives scored 1 run in the top of the 13th");
        assertEquals(0, thirteenth.getHomeTeamRuns(), "Chain Saw Scalpers were shut down in the bottom of the 13th");
    }

    @Test
    public void teamTotalsAreNotSilentlyZeroedOutByTheWrap() throws Exception {
        Game game = loadGame("2026031400190.box");

        assertEquals(3, game.getTeamTotals().getAwayTeamRuns());
        assertEquals(2, game.getTeamTotals().getHomeTeamRuns());
        assertEquals(12, game.getTeamTotals().getAwayTeamHits());
        assertEquals(6, game.getTeamTotals().getHomeTeamHits());
        assertEquals(2, game.getTeamTotals().getAwayTeamErrors());
        assertEquals(0, game.getTeamTotals().getHomeTeamErrors());
        assertEquals(13, game.getTeamTotals().getAwayTeamLOB());
        assertEquals(7, game.getTeamTotals().getHomeTeamLOB());
        assertEquals(4, game.getTeamTotals().getAwayTeamDP());
        assertEquals(1, game.getTeamTotals().getHomeTeamDP());
    }

    /**
     * COL23-ESC23, 9/26/2023: 25 innings, THREE linescore blocks (1-12, 13-24, then
     * 25 + totals) -- stresses the fix beyond the two-block case above. Real totals
     * per the box score: Dire Wolves R=4 H=16 E=4 LOB=16 DP=2, Pepper R=6 H=14 E=2
     * LOB=16 DP=1; Dire Wolves scored 1 run in the top 25th, Pepper scored 3 in the
     * bottom 25th (a walk-off, ending the game).
     */
    @Test
    public void twentyFifthInningAcrossThreeLinescoreBlocksIsCapturedCorrectly() throws Exception {
        Game game = loadGame("2023092600420.box");
        Map<Integer, InningScore> linescores = game.getLinescores();

        assertEquals(25, linescores.size(), "all 25 innings should appear in the linescore");
        assertTrue(linescores.containsKey(13), "innings from the middle wrapped block should appear too");

        InningScore twentyFifth = linescores.get(25);
        assertEquals(1, twentyFifth.getAwayTeamRuns(), "Dire Wolves scored 1 run in the top of the 25th");
        assertEquals(3, twentyFifth.getHomeTeamRuns(), "Pepper walked it off with 3 runs in the bottom of the 25th");
    }

    @Test
    public void teamTotalsAreCorrectAcrossThreeLinescoreBlocks() throws Exception {
        Game game = loadGame("2023092600420.box");

        assertEquals(4, game.getTeamTotals().getAwayTeamRuns());
        assertEquals(6, game.getTeamTotals().getHomeTeamRuns());
        assertEquals(16, game.getTeamTotals().getAwayTeamHits());
        assertEquals(14, game.getTeamTotals().getHomeTeamHits());
        assertEquals(4, game.getTeamTotals().getAwayTeamErrors());
        assertEquals(2, game.getTeamTotals().getHomeTeamErrors());
        assertEquals(16, game.getTeamTotals().getAwayTeamLOB());
        assertEquals(16, game.getTeamTotals().getHomeTeamLOB());
        assertEquals(2, game.getTeamTotals().getAwayTeamDP());
        assertEquals(1, game.getTeamTotals().getHomeTeamDP());
    }
}
