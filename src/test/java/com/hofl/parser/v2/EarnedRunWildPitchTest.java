package com.hofl.parser.v2;

import com.hofl.parser.v2.notations.AbstractNotation.BaserunnerScored;
import com.hofl.parser.v2.pbp.AbstractEvent;
import com.hofl.parser.v2.pbp.GameEvent;
import com.hofl.parser.v2.vo.Game;
import com.hofl.parser.v2.vo.Pitching.PitchingLine;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression fixture confirming (a) Diamond Mind Baseball treats a run scored on a
 * wild pitch as earned -- unlike passed balls, which it charges as unearned (see
 * EarnedRunTest) -- and (b) the reconstructed-inning rule from EarnedRunTest also
 * covers outfield fielding errors, not just infield ones.
 *
 * Fixture: RID26-STA26, 7/26/2026.
 *   - Top of the 4th: Chapman,R scores on a wild pitch by Allen,J. Diamond Mind
 *     charges Allen,J with that run as earned (IP=7.0 R=1 ER=1).
 *   - Bottom of the 3rd, 2 outs already recorded: Heyward scores when Belt reaches on
 *     an outfield error by the left fielder, Nieman (notation e7.2-H;B-2) -- the error
 *     itself costs what should have been the 3rd out, so Heyward's run is unearned.
 *     Diamond Mind charges Wynn,E with exactly one unearned run that game
 *     (IP=3.2 R=5 ER=4); this is the only error-adjacent run he allows.
 */
public class EarnedRunWildPitchTest {

    private Game loadGame() throws Exception {
        URL fixture = getClass().getClassLoader().getResource("fixtures/2026072600350.box");
        BoxScoreAssembler box = new BoxScoreAssembler(new File(fixture.toURI()));
        return box.getGame();
    }

    private List<BaserunnerScored> runsChargedTo(Game game, String pitcherName) throws Exception {
        List<BaserunnerScored> runs = new ArrayList<BaserunnerScored>();
        for (AbstractEvent event : game.getPlayByPlay()) {
            if (event instanceof GameEvent) {
                GameEvent ge = (GameEvent) event;
                for (GameEvent.Pitch p : ge.getPitches()) {
                    if (p.getNotation() != null) {
                        Map<String, BaserunnerScored> scored = p.getNotation().getRunnersScored();
                        if (scored != null) {
                            for (BaserunnerScored run : scored.values()) {
                                if (pitcherName.equals(run.getPitcherResponsible())) {
                                    runs.add(run);
                                }
                            }
                        }
                    }
                }
            }
        }
        return runs;
    }

    private PitchingLine findPitchingLine(Game game, String pitcherName, boolean home) {
        List<PitchingLine> lines = home ? game.getPitching().getHomePitching() : game.getPitching().getAwayPitching();
        for (PitchingLine line : lines) {
            if (pitcherName.equals(line.getPitcherName())) {
                return line;
            }
        }
        return null;
    }

    @Test
    public void wildPitchRunChargedToAllenIsEarned() throws Exception {
        Game game = loadGame();
        List<BaserunnerScored> allenRuns = runsChargedTo(game, "Allen,J");

        assertEquals(1, allenRuns.size(), "the only run Allen,J allowed should be charged to him");
        assertTrue(allenRuns.get(0).getEarnedRun(),
            "Chapman,R's run should be earned -- it scored on a wild pitch, which Diamond Mind "
                + "does not treat as an unearned-run event (unlike a passed ball)");
    }

    @Test
    public void allenPitchingLineStillMatchesDiamondMindsOwnSummary() throws Exception {
        Game game = loadGame();
        PitchingLine allen = findPitchingLine(game, "Allen,J", true);

        assertTrue(allen != null, "Allen,J should appear in the Blues' pitching lines");
        assertEquals(1, allen.getRuns());
        assertEquals(1, allen.getEarnedRuns());
    }

    @Test
    public void onlyTheOutfieldErrorRunChargedToWynnIsUnearned() throws Exception {
        Game game = loadGame();
        List<BaserunnerScored> wynnRuns = runsChargedTo(game, "Wynn,E");

        assertEquals(5, wynnRuns.size(), "all 5 runs Wynn,E allowed should be charged to him");

        int unearned = 0;
        for (BaserunnerScored run : wynnRuns) {
            if ("Heyward".equals(run.getPlayerName())) {
                assertFalse(run.getEarnedRun(),
                    "Heyward's run should be unearned -- Nieman's outfield error on the same play "
                        + "cost what should have been the inning's 3rd out");
                unearned++;
            } else {
                assertTrue(run.getEarnedRun(), run.getPlayerName() + "'s run should be earned -- it scored cleanly");
            }
        }
        assertEquals(1, unearned, "exactly one of Wynn,E's runs should be unearned");
    }

    @Test
    public void wynnPitchingLineStillMatchesDiamondMindsOwnSummary() throws Exception {
        Game game = loadGame();
        PitchingLine wynn = findPitchingLine(game, "Wynn,E", false);

        assertTrue(wynn != null, "Wynn,E should appear in the Wahoos' pitching lines");
        assertEquals(5, wynn.getRuns());
        assertEquals(4, wynn.getEarnedRuns());
    }
}
