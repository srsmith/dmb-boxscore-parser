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
 * Regression fixture for the earned-run "reconstructed inning" rule: an error that
 * costs the defense what should have been its 3rd out makes every run scored for the
 * rest of that half-inning unearned, even runs that score on later, error-free plays.
 *
 * Fixture: RID26-EWA26, 8/19/2026. Top of the 1st, Wahoos batting: with 2 outs already
 * recorded, Alonso,Y (1B) commits an error on what would have been the 3rd out. All 7
 * runs the Wahoos score in that half-inning are unearned per Diamond Mind Baseball's
 * own box score (Knepper's line reads IP=0.2 H=6 R=7 ER=0).
 */
public class EarnedRunTest {

    private Game loadGame() throws Exception {
        URL fixture = getClass().getClassLoader().getResource("fixtures/2026081900180.box");
        BoxScoreAssembler box = new BoxScoreAssembler(new File(fixture.toURI()));
        return box.getGame();
    }

    @Test
    public void allRunsChargedToKnepperAreUnearned() throws Exception {
        Game game = loadGame();

        List<BaserunnerScored> knepperRuns = new ArrayList<BaserunnerScored>();
        for (AbstractEvent event : game.getPlayByPlay()) {
            if (event instanceof GameEvent) {
                GameEvent ge = (GameEvent) event;
                for (GameEvent.Pitch p : ge.getPitches()) {
                    if (p.getNotation() != null) {
                        Map<String, BaserunnerScored> scored = p.getNotation().getRunnersScored();
                        if (scored != null) {
                            for (BaserunnerScored run : scored.values()) {
                                if ("Knepper".equals(run.getPitcherResponsible())) {
                                    knepperRuns.add(run);
                                }
                            }
                        }
                    }
                }
            }
        }

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

        PitchingLine knepper = null;
        for (PitchingLine line : game.getPitching().getHomePitching()) {
            if ("Knepper".equals(line.getPitcherName())) {
                knepper = line;
            }
        }

        assertTrue(knepper != null, "Knepper should appear in the Mud Dogs' pitching lines");
        assertEquals(7, knepper.getRuns());
        assertEquals(0, knepper.getEarnedRuns());
    }
}
