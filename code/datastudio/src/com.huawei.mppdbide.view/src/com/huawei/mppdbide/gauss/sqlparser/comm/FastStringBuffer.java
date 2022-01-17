/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.gauss.sqlparser.comm;

import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * 
 * Title: FastStringBuffer
 *
 * @since 3.0.0
 */
public final class FastStringBuffer {
    /**
     * Holds the actual chars
     */
    private char[] value;

    /**
     * Count for which chars are actually used
     */
    private int count;

    /**
     * Initializes with a default initial size (128 chars).
     */
    public FastStringBuffer() {
        this(128);
    }

    /**
     * An initial size can be specified (if available and given for no
     * allocations it can be more efficient).
     *
     * @param initialSize the initial size
     */
    public FastStringBuffer(int initialSize) {
        this.value = new char[initialSize];
        this.count = 0;
    }

    /**
     * Instantiates a new fast string buffer.
     *
     * @param internalBuffer the internal buffer
     */
    public FastStringBuffer(char[] internalBuffer) {
        this.value = internalBuffer;
        this.count = internalBuffer.length;
    }

    /**
     * initializes from a string and the additional size for the buffer.
     *
     * @param bufferString string with the initial contents
     * @param additionalSize the additional size for the buffer
     */
    public FastStringBuffer(String bufferString, int additionalSize) {
        this.count = bufferString.length();
        if (additionalSize < 0) {
            additionalSize = 0;
        }
        value = new char[this.count + additionalSize];
        bufferString.getChars(0, this.count, value, 0);
    }

    /**
     * Appends a string to the buffer. Passing a null string will throw an
     * exception.
     *
     * @param string the string
     * @return the fast string buffer
     */
    public FastStringBuffer append(String string) {
        return appendObject(string);
    }

    /**
     * Appends a string to the buffer. The buffer must have enough pre-allocated
     * space for it to succeed.
     * 
     * Passing a null string will throw an exception. Not having a pre-allocated
     * internal array big enough will throw an exception.
     *
     * @param string the string
     * @return the fast string buffer
     */
    public FastStringBuffer appendNoResize(String string) {
        int strLen = string.length();
        string.getChars(0, strLen, value, this.count);
        this.count = count + strLen;
        return this;
    }

    /**
     * Appends an int to the buffer.
     *
     * @param n the n
     * @return the fast string buffer
     */
    public FastStringBuffer append(int n) {
        String string = String.valueOf(n);
        return appendObject(string);
    }

    /**
     * Appends a char to the buffer.
     *
     * @param n the n
     * @return the fast string buffer
     */
    public FastStringBuffer append(char chr) {
        if (count + 1 > value.length) {
            int minimumCapacity = count + 1;
            int newCapacity = (value.length + 1) * 2;
            if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
        value[count] = chr;
        count++;
        return this;
    }

    /**
     * Appends a char to the buffer. Use when the size allocated is usually
     * already ok (will only resize on exception instead of doing a size check
     * all the time).
     *
     * @param n the n
     */
    public void appendResizeOnExc(char chr) {
        try {
            value[count] = chr;
        } catch (Exception e) {
            int minimumCapacity = count + 1;
            int newCapacity = (value.length + 1) * 2;
            if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
            value[count] = chr;
        }
        count++;
    }

    /**
     * Appends a long to the buffer.
     *
     * @param n the n
     * @return the fast string buffer
     */
    public FastStringBuffer append(long n) {
        String string = String.valueOf(n);
        return appendObject(string);
    }

    /**
     * Appends a boolean to the buffer.
     *
     * @param booleanValue the b
     * @return the fast string buffer
     */
    public FastStringBuffer append(boolean booleanValue) {
        String string = String.valueOf(booleanValue);
        return appendObject(string);
    }

    private FastStringBuffer appendObject(String objStrVal) {
        int strLen = objStrVal.length();
        int newCount = count + strLen;

        if (newCount > this.value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (newCount > newCapacity) {
                newCapacity = newCount;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
        objStrVal.getChars(0, strLen, value, this.count);
        this.count = newCount;
        return this;
    }

    /**
     * Appends a double to the buffer.
     *
     * @param doubleValue the b
     * @return the fast string buffer
     */
    public FastStringBuffer append(double doubleValue) {
        String string = String.valueOf(doubleValue);
        return appendObject(string);
    }

    /**
     * Appends an array of chars to the buffer.
     *
     * @param chars the chars
     * @return the fast string buffer
     */
    public FastStringBuffer append(char[] chars) {
        int newCount = count + chars.length;
        if (newCount > value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (newCount > newCapacity) {
                newCapacity = newCount;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
        System.arraycopy(chars, 0, value, count, chars.length);
        count = newCount;
        return this;
    }

    /**
     * Appends another buffer to this buffer.
     *
     * @param other the other
     * @return the fast string buffer
     */
    public FastStringBuffer append(FastStringBuffer other) {
        int len = other.count;
        int newCount = count + len;
        if (newCount > value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (newCount > newCapacity) {
                newCapacity = newCount;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
        System.arraycopy(other.value, 0, value, count, len);
        count = newCount;
        return this;
    }

    /**
     * Appends an array of chars to this buffer, starting at the offset passed
     * with the length determined.
     *
     * @param chars the chars
     * @param offset the offset
     * @param len the len
     * @return the fast string buffer
     */
    public FastStringBuffer append(char[] chars, int offset, int len) {
        int newCount = count + len;
        if (newCount > value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (newCount > newCapacity) {
                newCapacity = newCount;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
        System.arraycopy(chars, offset, value, count, len);
        count = newCount;
        return this;
    }

    /**
     * Reverses the contents on this buffer.
     *
     * @return the fast string buffer
     */
    public FastStringBuffer reverse() {
        final int limit = count / 2;
        for (int i = 0; i < limit; ++i) {
            char chr = value[i];
            value[i] = value[count - i - 1];
            value[count - i - 1] = chr;
        }
        return this;
    }

    /**
     * Clears this buffer.
     *
     * @return the fast string buffer
     */
    public FastStringBuffer clear() {
        this.count = 0;
        return this;
    }

    /**
     * Length.
     *
     * @return the length of this buffer
     */
    public int length() {
        return this.count;
    }

    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return this.count == 0;
    }

    /**
     * To string.
     *
     * @return a new string with the contents of this buffer.
     */
    @Override
    public String toString() {
        return new String(value, 0, count);
    }

    /**
     * To char array.
     *
     * @return a new char array with the contents of this buffer.
     */
    public char[] toCharArray() {
        char[] chr = new char[count];
        System.arraycopy(value, 0, chr, 0, count);
        return chr;
    }

    /**
     * Erases the last char in this buffer.
     */
    public void deleteLast() {
        if (this.count > 0) {
            this.count--;
        }
    }

    /**
     * Delete last chars.
     *
     * @param charsToDelete the chars to delete
     */
    public void deleteLastChars(int charsToDelete) {
        this.count -= charsToDelete;
        if (this.count < 0) {
            this.count = 0;
        }
    }

    /**
     * Char at.
     *
     * @param i the i
     * @return the char given at a specific position of the buffer (no bounds
     * check)
     */
    public char charAt(int i) {
        return this.value[i];
    }

    /**
     * Inserts a string at a given position in the buffer.
     *
     * @param offset the offset
     * @param str the str
     * @return the fast string buffer
     */
    public FastStringBuffer insert(int offset, String str) {
        int length = str.length();
        int newCountLength = count + length;
        if (newCountLength > value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (newCountLength > newCapacity) {
                newCapacity = newCountLength;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
        System.arraycopy(value, offset, value, offset + length, count - offset);
        str.getChars(0, length, value, offset);
        count = newCountLength;
        return this;
    }

    /**
     * Inserts a string at a given position in the buffer.
     *
     * @param offset the offset
     * @param str the str
     * @return the fast string buffer
     */
    public FastStringBuffer insert(int offset, FastStringBuffer str) {
        int len = str.length();
        int newCount = count + len;
        if (newCount > value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (newCount > newCapacity) {
                newCapacity = newCount;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
        System.arraycopy(value, offset, value, offset + len, count - offset);
        System.arraycopy(str.value, 0, value, offset, str.count);
        count = newCount;
        return this;
    }

    /**
     * Inserts a char at a given position in the buffer.
     *
     * @param offset the offset
     * @param c the c
     * @return the fast string buffer
     */
    public FastStringBuffer insert(int offset, char chr) {
        int newCount = count + 1;
        if (newCount > value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (newCount > newCapacity) {
                newCapacity = newCount;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
        System.arraycopy(value, offset, value, offset + 1, count - offset);
        value[offset] = chr;
        count = newCount;
        return this;
    }

    /**
     * Appends object.toString(). If null, "null" is appended.
     *
     * @param object the object
     * @return the fast string buffer
     */
    public FastStringBuffer appendObject(Object object) {
        String string = object != null ? object.toString() : "null";
        return appendObject(string);
    }

    /**
     * Sets the new size of this buffer (warning: use with care: no validation
     * is done of the len passed).
     *
     * @param newLen the new count
     */
    public void setCount(int newLen) {
        this.count = newLen;
    }

    /**
     * Delete.
     *
     * @param start the start
     * @param end the end
     * @return the fast string buffer
     */
    public FastStringBuffer delete(int start, int end) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > count) {
            end = count;
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        int len = end - start;
        if (len > 0) {
            System.arraycopy(value, start + len, value, start, count - end);
            count -= len;
        }
        return this;
    }

    /**
     * Replace.
     *
     * @param start the start
     * @param end the end
     * @param str the str
     * @return the fast string buffer
     */
    public FastStringBuffer replace(int start, int end, String str) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (start > count) {
            throw new StringIndexOutOfBoundsException("start > length()");
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException("start > end");
        }
        if (end > count) {
            end = count;
        }

        int len = str.length();
        int newCount = count + len - (end - start);
        if (newCount > value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (newCount > newCapacity) {
                newCapacity = newCount;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }

        System.arraycopy(value, end, value, start + len, count - end);
        str.getChars(0, len, value, start);
        count = newCount;
        return this;
    }

    /**
     * Delete char at.
     *
     * @param index the index
     * @return the fast string buffer
     */
    public FastStringBuffer deleteCharAt(int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        System.arraycopy(value, index + 1, value, index, count - index - 1);
        count--;
        return this;
    }

    /**
     * Index of.
     *
     * @param c the c
     * @return the int
     */
    public int indexOf(char c) {
        for (int i = 0; i < this.count; i++) {
            if (c == this.value[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Index of.
     *
     * @param c the c
     * @param fromOffset the from offset
     * @return the int
     */
    public int indexOf(char c, int fromOffset) {
        for (int i = fromOffset; i < this.count; i++) {
            if (c == this.value[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * First char.
     *
     * @return the char
     */
    public char firstChar() {
        return this.value[0];
    }

    /**
     * Last char.
     *
     * @return the char
     */
    public char lastChar() {
        return this.value[this.count - 1];
    }

    /**
     * Right trim.
     */
    public void rightTrim() {
        char chr;
        while (this.count > 0 && ((chr = this.value[this.count - 1]) == ' ' || chr == '\t')) {
            this.count--;
        }
    }

    /**
     * Delete first.
     *
     * @return the char
     */
    public char deleteFirst() {
        char ret = this.value[0];
        this.deleteCharAt(0);
        return ret;
    }

    /**
     * Append N.
     *
     * @param val the val
     * @param n the n
     * @return the fast string buffer
     */
    public FastStringBuffer appendN(final String val, int n) {
        final int strLen = val.length();
        int min = count + (n * strLen);
        if (min > value.length) {
            int newCapacity = (value.length + 1) * 2;
            if (min > newCapacity) {
                newCapacity = min;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }

        while (n-- > 0) {
            val.getChars(0, strLen, value, this.count);
            this.count += strLen;
        }
        return this;
    }

    /**
     * Append N.
     *
     * @param val the val
     * @param n the n
     * @return the fast string buffer
     */
    public FastStringBuffer appendN(char val, int n) {
        if (count + n > value.length) {
            int minimumCapacity = count + n;
            int newCapacity = (value.length + 1) * 2;
            if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }

        while (n-- > 0) {
            value[count] = val;
            count++;
        }
        return this;
    }

    /**
     * Ends with.
     *
     * @param string the string
     * @return true, if successful
     */
    public boolean endsWith(String string) {
        return startsWith(string, count - string.length());
    }

    /**
     * Starts with.
     *
     * @param prefix the prefix
     * @return true, if successful
     */
    public boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }

    /**
     * Starts with.
     *
     * @param c the c
     * @return true, if successful
     */
    public boolean startsWith(char c) {
        if (this.count < 1) {
            return false;
        }
        return this.value[0] == c;
    }

    /**
     * Ends with.
     *
     * @param c the c
     * @return true, if successful
     */
    public boolean endsWith(char c) {
        if (this.count < 1) {
            return false;
        }
        return this.value[this.count - 1] == c;
    }

    /**
     * Starts with.
     *
     * @param prefix the prefix
     * @param offset the offset
     * @return true, if successful
     */
    public boolean startsWith(String prefix, int offset) {
        char[] ta = value;
        int to = offset;
        char[] pa = prefix.toCharArray();
        int po = 0;
        int pc = pa.length;
        // Note: toffset might be near -1>>>1.
        if ((offset < 0) || (offset > count - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the char at.
     *
     * @param pos the pos
     * @param chr the chr
     */
    public void setCharAt(int pos, char chr) {
        this.value[pos] = chr;
    }

    /**
     * Careful: it doesn't check anything. Just sets the internal length.
     *
     * @param i the new length
     */
    public void setLength(int len) {
        this.count = len;
    }

    /**
     * Gets the bytes.
     *
     * @return the bytes
     */
    public byte[] getBytes() {
        return this.toString().getBytes(Charset.forName("UTF-8"));
    }

    /**
     * Count new lines.
     *
     * @return the int
     */
    public int countNewLines() {
        int lines = 0;

        for (int i = 0; i < count; i++) {
            char chr = value[i];
            switch (chr) {
                case '\n': {
                    lines += 1;
                    break;
                }

                case '\r': {
                    lines += 1;
                    if (i < count - 1) {
                        if (value[i + 1] == '\n') {
                            i++; // skip the \n after the \r
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return lines;
    }

    /**
     * Insert N.
     *
     * @param pos the pos
     * @param charValue the c
     * @param repetitions the repetitions
     * @return the fast string buffer
     */
    public FastStringBuffer insertN(int pos, char charValue, int repetitions) {
        FastStringBuffer other = new FastStringBuffer(repetitions);
        other.appendN(charValue, repetitions);
        insert(pos, other);
        return this;
    }

    /**
     * Gets the last word.
     *
     * @return the last word
     */
    public String getLastWord() {
        FastStringBuffer lastWordBuf = new FastStringBuffer(this.count);
        int index;
        // skip whitespaces in the end
        for (index = this.count - 1; index >= 0; index--) {
            if (!Character.isWhitespace(this.value[index])) {
                break;
            }
        }
        // actual word
        for (; index >= 0; index--) {
            if (Character.isWhitespace(this.value[index])) {
                break;
            }
            lastWordBuf.append(this.value[index]);
        }
        lastWordBuf.reverse();
        return lastWordBuf.toString();
    }

    /**
     * Removes the whitespaces.
     */
    public void removeWhitespaces() {
        int length = this.count;
        char[] newVal = new char[length];

        int index = 0;
        for (int i = 0; i < length; i++) {
            char ch = this.value[i];
            if (!Character.isWhitespace(ch)) {
                newVal[index] = ch;
                index++;
            }
        }
        this.count = index;
        this.value = newVal;
    }

    /**
     * Gets the internal chars array.
     *
     * @return the internal chars array
     */
    public char[] getInternalCharsArray() {
        return this.value;
    }
}
