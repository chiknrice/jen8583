/* 
 * Copyright (c) 2014 Ian Bondoc
 * 
 * This file is part of Jen8583
 * 
 * Jen8583 is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or(at your option) any later version.
 * 
 * Jen8583 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package org.chiknrice.iso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.chiknrice.iso.codec.DateTimeCodec;
import org.chiknrice.iso.config.ComponentDef.Encoding;
import org.junit.Test;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 *
 */
public class DateTimeCodecTest {

    @Test
    public void testEncodeBCD() throws ParseException {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        DateTimeCodec codec = new DateTimeCodec("hhmmss", utc, Encoding.BCD);
        ByteBuffer buf = ByteBuffer.allocate(3);
        SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
        sdf.setTimeZone(utc);
        Date toEncode = sdf.parse("123456");
        codec.encode(buf, toEncode);
        byte[] bytes = buf.array();
        assertTrue(Arrays.equals(new byte[] { 0x12, 0x34, 0x56 }, bytes));
    }

    @Test
    public void testEncodeCHAR() throws ParseException {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        DateTimeCodec codec = new DateTimeCodec("hhmmss", utc, Encoding.CHAR);
        ByteBuffer buf = ByteBuffer.allocate(6);
        SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
        sdf.setTimeZone(utc);
        Date toEncode = sdf.parse("123456");
        codec.encode(buf, toEncode);
        byte[] bytes = buf.array();
        assertEquals("123456", new String(bytes, StandardCharsets.ISO_8859_1));
    }

    @Test
    public void testGetEncoding() {
        DateTimeCodec codec = new DateTimeCodec("hhmmss", TimeZone.getDefault(), Encoding.BCD);
        assertEquals(Encoding.BCD, codec.getEncoding());
    }

    @Test(expected = ConfigException.class)
    public void testInvalidEncoding() {
        new DateTimeCodec("hhmmss", TimeZone.getDefault(), Encoding.BINARY);
    }

    @Test
    public void testEqualsAndHashCode() {
        DateTimeCodec codec1 = new DateTimeCodec("hhmmss", TimeZone.getDefault(), Encoding.CHAR);
        DateTimeCodec codec2 = new DateTimeCodec("hhmmss", TimeZone.getDefault(), Encoding.CHAR);
        DateTimeCodec codec3 = new DateTimeCodec("hhmmss", TimeZone.getDefault(), Encoding.BCD);
        assertTrue(!codec1.equals(null));
        assertTrue(!codec1.equals("a"));
        assertTrue(codec1.equals(codec1));
        assertTrue(codec1.equals(codec2));
        assertEquals(codec1.hashCode(), codec2.hashCode());
        assertTrue(!codec1.equals(codec3));
        assertNotEquals(codec1.hashCode(), codec3.hashCode());
        assertTrue(!codec2.equals(codec3));
        assertNotEquals(codec2.hashCode(), codec3.hashCode());
    }

}
