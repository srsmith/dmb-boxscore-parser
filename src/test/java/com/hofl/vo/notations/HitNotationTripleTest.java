package com.hofl.vo.notations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Same typo as the v2 pipeline's HitNotation (see the test of the same name in
 * com.hofl.parser.v2.notations): isTriple() checked for "T//" instead of "T/".
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
