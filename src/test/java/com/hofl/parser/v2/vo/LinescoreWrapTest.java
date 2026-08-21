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
 * first linescore row. Past that, the box score wraps onto a second linescore header
 * ("                      13     R  H  E   LOB DP") that doesn't match
 * BoxScoreAssembler.linescoreHeaderPattern's fixed-column layout for the normal case,
 * so it (and the team totals row that comes with it, since DMB only prints R/H/E/LOB/DP
 * on whichever linescore row is last) was silently skipped entirely: the wrapped
 * inning(s) were missing from the linescore, and every team total (runs, hits, errors,
 * LOB, double plays) defaulted to 0 instead of the real values.
 *
 * Fixture: JER26-TEX26, 3/14/2026 (src/test/resources/fixtures/2026031400190.box), a
 * 13-inning game. Real totals per the box score: Fugitives R=3 H=12 E=2 LOB=13 DP=4,
 * Chain Saw Scalpers R=2 H=6 E=0 LOB=7 DP=1; Fugitives scored 1 run in the top 13th.
 */
public class LinescoreWrapTest {

    private Game loadGame() throws Exception {
        URL fixture = getClass().getClassLoader().getResource("fixtures/2026031400190.box");
        BoxScoreAssembler box = new BoxScoreAssembler(new File(fixture.toURI()));
        return box.getGame();
    }

    @Test
    public void thirteenthInningIsCapturedInTheLinescore() throws Exception {
        Game game = loadGame();
        Map<Integer, InningScore> linescores = game.getLinescores();

        assertTrue(linescores.containsKey(13), "the wrapped 13th inning should appear in the linescore");
        InningScore thirteenth = linescores.get(13);
        assertEquals(1, thirteenth.getAwayTeamRuns(), "Fugitives scored 1 run in the top of the 13th");
        assertEquals(0, thirteenth.getHomeTeamRuns(), "Chain Saw Scalpers were shut down in the bottom of the 13th");
    }

    @Test
    public void teamTotalsAreNotSilentlyZeroedOutByTheWrap() throws Exception {
        Game game = loadGame();

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
}
