package net.sandbox.segment;

import java.util.Map;

import com.google.common.base.Optional;

public class PathSegment {

	private PathSegment next;
	private PathSegment prev;
	private String name;
	private SegmentType type;
	private Optional<Map<String, String>> keyMap = Optional.absent();

	public PathSegment getNext() {
		return next;
	}

	public void setNext(PathSegment next) {
		this.next = next;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SegmentType getType() {
		return type;
	}

	public void setType(SegmentType type) {
		this.type = type;
	}
	
	public PathSegment getPrev() {
		return prev;
	}

	public void setPrev(PathSegment prev) {
		this.prev = prev;
	}

	public boolean hasNext() {
		return next != null;
	}
	
	public boolean hasPrev() {
		return prev != null;
	}
	
	@Override
	public String toString() {
		return String.format("Name: '%s', Type '%s', \nnext '%s'", name, type.toString(), (next == null) ? "EOL" : next.toString());
	}

	public void setKeyMap(Map<String, String> keyMap) {
		this.keyMap = Optional.fromNullable(keyMap);
	}
	
	public Map<String, String> getKeyMap() {
		return keyMap.get();
	}

	public boolean hasKey() {
		return keyMap.isPresent();
	}
	
	public void linkSegment(final PathSegment prevSegment) {
		if (prevSegment != null) {
			prevSegment.setNext(prevSegment);
			this.setPrev(prevSegment);
		}
	}
}
