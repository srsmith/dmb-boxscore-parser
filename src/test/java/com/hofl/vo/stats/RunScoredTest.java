package com.hofl.vo.stats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The null-name guard in RunScored's constructor was two independent if statements,
 * not if/else if. With a null playerName: the first if set the "FIXME!" placeholder,
 * then the second if's condition (playerName != null && ...) was false, so its else
 * ran and reassigned this.playerName back to the original (null) argument, undoing the
 * fix. Downstream SQL/CSV building calls .trim()/.indexOf() on it unconditionally,
 * which would NPE.
 */
public class RunScoredTest {

    @Test
    public void nullPlayerNameBecomesPlaceholderInsteadOfStayingNull() {
        RunScored run = new RunScored("Team", null, 1, 1, 0, 1, 0, "CF", "Ballpark", "A");
        assertEquals("FIXME!", run.getPlayerName());
    }

    @Test
    public void blankPlayerNameBecomesPlaceholder() {
        RunScored run = new RunScored("Team", "   ", 1, 1, 0, 1, 0, "CF", "Ballpark", "A");
        assertEquals("FIXME!", run.getPlayerName());
    }

    @Test
    public void realPlayerNameIsPreserved() {
        RunScored run = new RunScored("Team", "Snodgrass", 1, 1, 0, 1, 0, "CF", "Ballpark", "A");
        assertEquals("Snodgrass", run.getPlayerName());
    }
}
