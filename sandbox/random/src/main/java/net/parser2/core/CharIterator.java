package net.parser2.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharIterator implements Iterator<Character> {

	private final CharSequence sequence;
	private int index = 0;

	public CharIterator(CharSequence seq) {
		this.sequence = seq;
	}

	@Override
	public boolean hasNext() {
		return index < sequence.length();
	}

	@Override
	public Character next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return sequence.charAt(index++);
	}

	public Character peek() {
		return sequence.charAt(index);
	}

	public void reset(int newIndex) {
		index = newIndex;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return (String) sequence.subSequence(index, sequence.length());
	}

	public String substring(int start) {
		return sequence.charAt(index) + (String) sequence.subSequence(index, sequence.length());
	}
}