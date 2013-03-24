package net.parser.behaviour;

import java.util.Iterator;

import net.parser.GenericParser;
import net.parser.Parser;
import net.parser.ResetableIterator;
import net.parser.predicate.CharPredicate;

import com.google.common.base.Predicate;

public class SingleParser extends GenericParser implements Parser  {
	
	public final Predicate<Character> predicate;
	
	public SingleParser(Predicate<Character> p) {
		predicate = p;
	}

	public SingleParser(char c) {
		predicate = new CharPredicate(c);
	}

	@Override
	public boolean parse(Iterator<Character> i) {
		sanitizeIterator(i);
		if (predicate.apply(((ResetableIterator)i).peek())) {
			i.next(); 
			return super.parse(i);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[SingleParser for " + predicate.toString() + "]";
	}
}
