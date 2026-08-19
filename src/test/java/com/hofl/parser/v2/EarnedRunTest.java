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
 * Regression fixture for two earned-run rules Diamond Mind Baseball applies that the
 * v2 JSON parser previously never computed at all (Baserunner.earned was hardcoded
 * true everywhere):
 *
 * 1. "Reconstructed inning": an error that costs the defense what should have been its
 *    3rd out makes every run scored for the rest of that half-inning unearned, even
 *    runs that score on later, error-free plays -- including the very play with the
 *    error on it.
 * 2. A run that scores on a passed ball is unearned in Diamond Mind's scoring, unlike
 *    official MLB rule 9.16, which treats passed balls the same as wild pitches.
 *
 * Fixture: RID26-EWA26, 8/19/2026.
 *   - Top of the 1st, Wahoos batting: with 2 outs already recorded, Alonso,Y (1B)
 *     errors on what would have been the 3rd out. Diamond Mind charges Knepper with
 *     all 7 runs that inning as unearned (IP=0.2 H=6 R=7 ER=0).
 *   - Top of the 8th: Nieman scores on a passed ball by LaValliere. Diamond Mind
 *     charges that run to Tamulis as unearned, while his other two runs (a clean
 *     single in the 7th, a double play that scores a run in the same half-inning as
 *     the passed ball) are earned (IP=3.0 R=3 ER=2).
 */
public class EarnedRunTest {

    private Game loadGame() throws Exception {
        URL fixture = getClass().getClassLoader().getResource("fixtures/2026081900180.box");
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
    public void allRunsChargedToKnepperAreUnearned() throws Exception {
        Game game = loadGame();
        List<BaserunnerScored> knepperRuns = runsChargedTo(game, "Knepper");

        assertEquals(7, knepperRuns.size(), "all 7 runs Knepper allowed should be charged to him");
        for (BaserunnerScored run : knepperRuns) {
            assertFalse(run.getEarnedRun(),
                run.getPlayerName() + "'s run should be unearned -- it scored after Alonso,Y's error "
                    + "cost the defense what should have been its 3rd out");
        }
    }

    @Test
    public void knepperPitchingLineStillMatchesDiamondMindsOwnSummary() throws Exception {
        Game game = loadGame();
        PitchingLine knepper = findPitchingLine(game, "Knepper", true);

        assertTrue(knepper != null, "Knepper should appear in the Mud Dogs' pitching lines");
        assertEquals(7, knepper.getRuns());
        assertEquals(0, knepper.getEarnedRuns());
    }

    @Test
    public void onlyThePassedBallRunChargedToTamulisIsUnearned() throws Exception {
        Game game = loadGame();
        List<BaserunnerScored> tamulisRuns = runsChargedTo(game, "Tamulis");

        assertEquals(3, tamulisRuns.size(), "all 3 runs Tamulis allowed should be charged to him");

        int unearned = 0;
        for (BaserunnerScored run : tamulisRuns) {
            if ("Nieman".equals(run.getPlayerName())) {
                assertFalse(run.getEarnedRun(), "Nieman's run should be unearned -- it scored on a passed ball");
                unearned++;
            } else {
                assertTrue(run.getEarnedRun(), run.getPlayerName() + "'s run should be earned -- it scored cleanly");
            }
        }
        assertEquals(1, unearned, "exactly one of Tamulis's runs should be unearned");
    }

    @Test
    public void tamulisPitchingLineStillMatchesDiamondMindsOwnSummary() throws Exception {
        Game game = loadGame();
        PitchingLine tamulis = findPitchingLine(game, "Tamulis", true);

        assertTrue(tamulis != null, "Tamulis should appear in the Mud Dogs' pitching lines");
        assertEquals(3, tamulis.getRuns());
        assertEquals(2, tamulis.getEarnedRuns());
    }
}
