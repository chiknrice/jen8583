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
package org.chiknrice.iso.codec;

import org.chiknrice.iso.CodecException;
import org.chiknrice.iso.ConfigException;
import org.chiknrice.iso.config.ComponentDef.Encoding;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class AlphaCodecTest {

    @Test
    public void testSimpleEncode() {
        AlphaCodec codec = new AlphaCodec(false);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, "abc");
        byte[] bytes = buf.array();
        String encoded = new String(bytes, 0, 3, StandardCharsets.ISO_8859_1);
        assertThat(encoded, is("abc"));
    }

    @Test
    public void testEncodeSpecialChar() {
        AlphaCodec codec = new AlphaCodec(false);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, "ü");
        byte[] bytes = buf.array();
        String encoded = new String(bytes, 0, 1, StandardCharsets.ISO_8859_1);
        assertThat(encoded, is("ü"));
    }

    @Test
    public void testEncodeSpecialCharPadded() {
        AlphaCodec codec = new AlphaCodec(false, true, 3);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, "ü");
        byte[] bytes = buf.array();
        String encoded = new String(bytes, 0, 3, StandardCharsets.ISO_8859_1);
        assertThat(encoded, is("ü  "));
    }

    @Test
    public void testInsufficientConstructorArgs() {
        try {
            new AlphaCodec(true, null, 9);
            fail("Constructor should fail due to missing required param");
        } catch (ConfigException e) {
            assertThat(e.getMessage(), is("Fixed length config requires justified flag"));
        }
    }

    @Test
    public void testPadding() {
        AlphaCodec codec = new AlphaCodec(true, false, 9);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, "abc");
        byte[] bytes = buf.array();
        String encoded = new String(bytes, 0, 9, StandardCharsets.ISO_8859_1);
        assertThat(encoded, is("      abc"));
    }

    @Test
    public void testExceedFixedLength() {
        AlphaCodec codec = new AlphaCodec(true, false, 4);
        ByteBuffer buf = ByteBuffer.allocate(20);
        try {
            codec.encode(buf, "abcdef");
            fail("Encoding should fail due to exceeding fixed length");
        } catch (CodecException e) {
            assertThat(e.getMessage(), is("Length of value (abcdef) exceeds allowed length (4)"));
        }
    }

    @Test
    public void testJustified() {
        AlphaCodec codec = new AlphaCodec(true, true, 9);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, "abc");
        byte[] bytes = buf.array();
        String encoded = new String(bytes, 0, 9, StandardCharsets.ISO_8859_1);
        assertThat(encoded, is("abc      "));
    }

    @Test
    public void testDecode() {
        AlphaCodec codec = new AlphaCodec(false);
        byte[] bytes = new byte[]{0x31, 0x32, 0x33};
        String decoded = codec.decode(ByteBuffer.wrap(bytes));
        assertThat(decoded, is("123"));
    }

    @Test
    public void testDecodeSpecialChar() {
        AlphaCodec codec = new AlphaCodec(false);
        byte[] bytes = new byte[]{(byte) 0xfc};
        String decoded = codec.decode(ByteBuffer.wrap(bytes));
        assertThat(decoded, is("ü"));
    }

    @Test
    public void testDecodeSpecialCharNoTrim() {
        AlphaCodec codec = new AlphaCodec(false);
        byte[] bytes = new byte[]{0x20, (byte) 0xfc, 0x20, 0x20, 0x20};
        String decoded = codec.decode(ByteBuffer.wrap(bytes));
        assertThat(decoded, is(" ü   "));
    }

    @Test
    public void testDecodeSpecialCharTrim() {
        AlphaCodec codec = new AlphaCodec(true);
        byte[] bytes = new byte[]{0x20, (byte) 0xfc, 0x20, 0x20, 0x20};
        String decoded = codec.decode(ByteBuffer.wrap(bytes));
        assertThat(decoded, is("ü"));
    }

    @Test
    public void testDecodeFixedLengthSpecialChar() {
        AlphaCodec codec = new AlphaCodec(false, false, 3);
        byte[] bytes = new byte[]{0x20, (byte) 0xfc, 0x20, 0x20, 0x20};
        String decoded = codec.decode(ByteBuffer.wrap(bytes));
        assertThat(decoded, is(" ü "));
    }

    @Test
    public void testGetEncoding() {
        AlphaCodec codec = new AlphaCodec(false);
        assertThat(codec.getEncoding(), is(Encoding.CHAR));
    }

    @Test
    @SuppressWarnings({"EqualsBetweenInconvertibleTypes", "EqualsWithItself", "ObjectEqualsNull"})
    public void testEqualsAndHashCode() {
        AlphaCodec codec1 = new AlphaCodec(true, false, 5);
        AlphaCodec codec2 = new AlphaCodec(true, false, 5);
        AlphaCodec codec3 = new AlphaCodec(true, true, 5);
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
