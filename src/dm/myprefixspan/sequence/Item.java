package dm.myprefixspan.sequence;

import java.util.HashSet;
import java.util.Set;

/**
 * Item
 * @author zhhailon
 *
 */
public class Item implements Comparable<Item> {

	/**
	 * Label of an item, should be a Hash number
	 */
	private int label;

	public class Occurrence {
		public int seqId;
		public int setId;
		public int itemId;
		public Occurrence(int seqId, int setId, int itemId) {
			this.itemId = itemId;
			this.seqId = seqId;
			this.setId = setId;
		}
	}
	
	private Set<Occurrence> occurrences;
	/**
	 * Ids of sequences in which a item appears
	 */
	private Set<Integer> seqIds;

	static private Set<Item> items;

	public Set<Occurrence> getOccurrences() {
		if (null == occurrences) {
			occurrences = new HashSet<Occurrence>();
		}
		return occurrences;
	}
	
	public void addOccurrence(Occurrence occ) {
		this.getOccurrences().add(occ);
	}

	public void setOccurrences(Set<Occurrence> occurrences) {
		this.occurrences = occurrences;
	}

	public Set<Integer> getSeqIds() {
		if (null == seqIds) {
			seqIds = new HashSet<Integer>();
		}
		return seqIds;
	}

	public void addSeqId(int seqId) {
		this.getSeqIds().add(seqId);
	}

	public void setSeqIds(Set<Integer> occurrence) {
		this.seqIds = occurrence;
	}

	static public Set<Item> allItems() {
		if (null == items) {
			items = new HashSet<Item>();
		}
		return items;
	}

	static public Item getItem(int label) {
		for (Item i : allItems()) {
			if (i.label == label) return i;
		}
		return null;
	}

	static public Item fromInt(int l) {
		for (Item i : allItems()) {
			if (i.getLabel() == l)
				return i;
		}
		Item item = new Item();
		item.setLabel(l);
		allItems().add(item);
		return item;
	}

	static public Item fromString(String str) {
		return fromInt(Integer.valueOf(str.trim()));
	}

	@Override
	public String toString() {
		return String.valueOf(getLabel());
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public boolean equals(Item i) {
		return this.equals(i.getLabel());
	}

	public boolean equals(int i) {
		if (this.getLabel() == i) {
			return true;
		}
		return false;
	}

	public int compareTo(Item o) {
		return this.getLabel() - o.getLabel();
	}

}
