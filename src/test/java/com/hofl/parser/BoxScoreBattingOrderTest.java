package com.hofl.parser;

import java.io.File;
import java.net.URL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Regression test for a batting-order bug in the v1 (SQL-insert) pipeline:
 * BoxScore.setBattingOrder() was checking playerLine.getPlayerName().indexOf(" ") == 0
 * to detect a substitute (whose name is printed with a leading space in the raw box
 * score line, so subs reuse the batting-order slot of the player they replaced), but
 * getPlayerName() always trims that leading space away first -- so the check could
 * never be true, and every substitute incremented the batting order past where it
 * should stop. Once a real batter's order was pushed past 9,
 * PlateAppearance.setBattingOrder() threw RuntimeException, failing the whole game.
 *
 * Fixture: RID26-STA26, 7/26/2026 (src/test/resources/fixtures/2026072600350.box).
 * The Wahoos lineup has two pinch hitters (Kendall for Chapman,R; Gurriel for Rasmus)
 * batting ahead of two more regulars who bat again (Sims, Green,Di). Under the bug,
 * those regulars are pushed to batting order 11 and 12.
 */
public class BoxScoreBattingOrderTest {

    @Test
    public void substitutesDoNotPushRemainingRegularsPastBattingOrder9() throws Exception {
        URL fixture = getClass().getClassLoader().getResource("fixtures/2026072600350.box");
        BoxScore box = new BoxScore(new File(fixture.toURI()));

        assertDoesNotThrow(() -> box.getStatFactory(),
            "pinch hitters batting ahead of remaining regulars should not push anyone's "
                + "batting order past 9 -- each substitute should reuse the order of the "
                + "player they replaced, not claim a new slot");
    }
}
