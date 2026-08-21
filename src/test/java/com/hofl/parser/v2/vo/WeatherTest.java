package com.hofl.parser.v2.vo;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Weather(String) assumed a fixed token position/count: temperature always 1st, sky
 * always 2nd, wind always 3rd. But "Field:" is only present for a notable field
 * condition, so on any game where it appears, every token after temperature shifts by
 * one -- the "Sky:" token gets read as the sky value (setSky("wet") instead of the
 * real sky), the actual "Sky: ..." token gets read as the wind token (fails its "MPH"
 * check, silently sets windSpeed to "None"), and the real wind info is dropped
 * entirely since only 3 tokens were ever read. fieldCondition itself was never set by
 * any code path despite having a full getter/setter, and rainDelays was always null --
 * on top of the parsing bug, the source line ("Rain Delays: 61 minutes.") is a
 * continuation line in the raw box score text, dropped entirely before it ever reached
 * this constructor.
 */
public class WeatherTest {

    @Test
    public void fieldConditionDoesNotCorruptSkyAndWind() {
        // Real weather line from a box score with a notable field condition, wrapped
        // onto a continuation line for the rain delay -- BoxScoreAssembler joins the
        // two physical lines with a space before constructing Weather.
        Weather w = new Weather("Temperature: 69, Field: wet, Sky: threatening, "
            + "Wind: in from right at 18 MPH, Rain Delays: 61 minutes.");

        assertEquals(69, w.getTemperature());
        assertEquals("wet", w.getFieldCondition());
        assertEquals("threatening", w.getSky());
        assertEquals("in from right", w.getWindDirection());
        assertEquals("18 MPH", w.getWindSpeed());
        assertEquals(Arrays.asList("61 minutes"), w.getRainDelays());
    }

    @Test
    public void gameWithoutFieldConditionOrRainDelayStillParsesCorrectly() {
        Weather w = new Weather("Temperature: 72, Sky: clear, Wind: in from center at 14 MPH.");

        assertEquals(72, w.getTemperature());
        assertNull(w.getFieldCondition());
        assertEquals("clear", w.getSky());
        assertEquals("in from center", w.getWindDirection());
        assertEquals("14 MPH", w.getWindSpeed());
        assertNull(w.getRainDelays());
    }
}
