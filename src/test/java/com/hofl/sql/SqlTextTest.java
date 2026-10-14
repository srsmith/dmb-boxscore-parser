package com.hofl.sql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The SQL-building classes in com.hofl.vo.stats (PlateAppearance, RunScored,
 * StealAttempt, OtherBaseRunning, PitchingLine) previously escaped only single quotes
 * (' -> \') before embedding a value in a hand-built SQL string literal. Under MySQL's
 * default backslash-escape mode, a value ending in a raw backslash is untouched by
 * that regex (no quote to escape) but still consumes the literal's closing quote when
 * the statement is executed, corrupting everything after it.
 */
public class SqlTextTest {

    @Test
    public void escapesSingleQuotes() {
        assertEquals("O\\'Brien", SqlText.escape("O'Brien"));
    }

    @Test
    public void escapesTrailingBackslashSoItCannotConsumeTheClosingQuote() {
        // Naive quote-only escaping would leave this as "Foo\" -- when embedded as
        // '...Foo\', the backslash escapes the quote instead of closing the literal.
        assertEquals("Foo\\\\", SqlText.escape("Foo\\"));
    }

    @Test
    public void escapesBackslashesBeforeQuotesSoTheQuoteEscapeIsNotItselfEscapedAway() {
        assertEquals("a\\\\b\\'c", SqlText.escape("a\\b'c"));
    }

    @Test
    public void nullPassesThroughUnchanged() {
        assertNull(SqlText.escape(null));
    }

    @Test
    public void ordinaryTextIsUnchanged() {
        assertEquals("Snodgrass", SqlText.escape("Snodgrass"));
    }
}
