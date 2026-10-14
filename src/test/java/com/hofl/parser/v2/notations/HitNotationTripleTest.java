package com.hofl.parser.v2.notations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * HitNotation.isTriple() checked for "T//" (double slash) instead of "T/" (single
 * slash) -- every sibling (isSingle/isDouble/isHomerun) uses a single slash for the
 * fielder-modifier form, so this was a typo. A triple notation like "T/9" (fielded by
 * right field) failed isOfType() entirely, so AbstractNotation.getNotation() fell
 * through every real notation type and landed on its PitchingChangeNotation catch-all
 * -- no hit recorded, no RBI, no baserunner placement for that play.
 */
public class HitNotationTripleTest {

    @Test
    public void tripleWithFielderModifierIsRecognizedAsATriple() {
        assertTrue(HitNotation.isTriple("T/9"), "T/9 should be recognized as a triple");
    }

    @Test
    public void tripleWithFielderModifierDispatchesToHitNotationNotPitchingChange() throws Exception {
        AbstractNotation notation = AbstractNotation.getNotation("T/9");
        assertInstanceOf(HitNotation.class, notation,
            "a triple fielded by a specific position should resolve to HitNotation, "
                + "not fall through to the PitchingChangeNotation catch-all");
    }
}
