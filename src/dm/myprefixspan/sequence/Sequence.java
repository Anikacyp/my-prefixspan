package dm.myprefixspan.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Sequence (customer)
 * @author zhhailon
 *
 */
public class Sequence {

	private Set<Integer> ids;
	private List<ItemSet> itemSets;

	/**
	 * 
	 * @param str the sequence string
	 * @param setSplitStr the delimiting regex for item set splitting in a sequence string
	 * @param itemSplitStr the delimiting regex for item set splitting in an item set
	 * @return
	 */
	static public Sequence fromString(
			String str, String setSplitStr, String itemSplitStr) {
		Sequence seq = new Sequence();
		String[] itemSetStrs = str.split(setSplitStr);
		for (String itemStr : itemSetStrs) {
			ItemSet iset = ItemSet.fromString(itemStr, itemSplitStr);
			seq.addItemSet(iset);
		}
		return seq;
	}

	public String toString() {
		String ret = "< ";
		for (ItemSet iset : this.getItemSets()) {
			ret += iset.toString();
		}
		ret += "> ids:[ ";
		for (int id : this.getIds()) {
			ret += id + " ";
		}
		return ret + "]";
	}

	public void addId(int id) {
		this.getIds().add(id);
	}

	public Set<Integer> getIds() {
		if (null == ids) {
			ids = new TreeSet<Integer>();
		}
		return ids;
	}

	public void setIds(Set<Integer> ids) {
		this.ids = ids;
	}

	public List<ItemSet> getItemSets() {
		if (null == itemSets) {
			itemSets = new ArrayList<ItemSet>();
		}
		return itemSets;
	}

	public int support() {
		return this.getIds().size();
	}

	public void setItemSets(List<ItemSet> itemSets) {
		this.itemSets = itemSets;
	}

	public void addItemSet(ItemSet iset) {
		this.getItemSets().add(iset);
	}

	public boolean contains(Item i) {
		for (ItemSet iset : this.getItemSets()) {
			for (Item item : iset.getItems()) {
				if (item.equals(i)) return true;
			}
		}
		return false;
	}
	
	public boolean contains(Sequence prefix) {
		int setIdx = -1, itemIdx = -1;
		boolean find = false, pSameSet = false;
		for (int pi = 0; pi < prefix.numOfItemSets(); pi ++) {
			ItemSet piset = prefix.getItemSet(pi);
			setIdx ++; // find next set
			itemIdx = -1; // start of the new set
			for (int pj = 0; pj < piset.size(); pj ++) {
				Item pitem = piset.getItem(pj);
				if (0 == pj) pSameSet = false;
				else pSameSet = true;
				nextItem:
					for (int i = setIdx; i < this.numOfItemSets(); i ++, setIdx ++) {
						ItemSet iset = this.getItemSet(i);
						itemIdx ++; // start from the next item in current set
						for (int j = itemIdx; j < iset.size(); j ++, itemIdx ++) {
							Item item = iset.getItem(j);
							if (item.equals(pitem)) {
								setIdx = i; // if find the item, record current set index
								itemIdx = j; // if find the item, record the item index in current set
								if (pi == (prefix.numOfItemSets() - 1) && pj == (piset.size() -1 )) {
									find = true;
								}
								break nextItem;
							}
						}
						if (pSameSet) return false; // cannot find the item in the same set in prefix, return false 
						itemIdx = -1; // if cannot find the item, start from the first item of next set
					}
			}
		}
		return find;
	}

	public void removeItem(int i) {
		List<ItemSet> itemSetRm = new ArrayList<ItemSet>();
		for (ItemSet iset : this.getItemSets()) {
			List<Item> itemRm = new ArrayList<Item>();
			for (Item item : iset.getItems()) {
				if (item.equals(i)) itemRm.add(item);
			}
			iset.getItems().removeAll(itemRm);
			if (0 == iset.size()) itemSetRm.add(iset);
		}
		this.getItemSets().removeAll(itemSetRm);
	}

	public int numOfItems() {
		int num = 0;
		for (ItemSet iset : this.getItemSets()) {
			num += iset.size();
		}
		return num;
	}

	/**
	 * Number of item sets
	 * @return
	 */
	public int numOfItemSets() {
		return this.getItemSets().size();
	}

	public ItemSet getItemSet(int idx) {
		return this.getItemSets().get(idx);
	}

	public boolean equals(Sequence s) {
		if (s.numOfItemSets() != this.numOfItemSets()) return false;
		for (int i = 0; i < this.numOfItemSets(); i ++) {
			ItemSet iset1 = this.getItemSet(i);
			ItemSet iset2 = this.getItemSet(i);
			if (!iset1.equals(iset2)) return false;
		}
		return true;
	}

	public Sequence subSequenceFrom(int setIdx, int itemIdx) {
		Sequence ret = new Sequence();
		ret.setIds(this.getIds());
		ItemSet sub = new ItemSet();
		for (int i = itemIdx; i < this.getItemSet(setIdx).size(); i ++) {
			sub.addItem(this.getItemSet(setIdx).getItem(i));
		}
		List<ItemSet> iset = ret.getItemSets();
		if (0 < sub.size()) iset.add(sub);
		for (int i = setIdx + 1; i < this.numOfItemSets(); i ++) {
			ItemSet newiset = new ItemSet();
			for (Item it : this.getItemSet(i).getItems()) {
				newiset.addItem(it);
			}
			iset.add(newiset);
		}
		return ret;
	}

	/**
	 * 
	 * @param prefix given sequence as prefix
	 * @return null if cannot find the suffix in given sequence
	 */
	public Sequence getSuffix(Sequence prefix) {
		int setIdx = -1, itemIdx = -1;
		boolean find = false, pSameSet = false;
		for (int pi = 0; pi < prefix.numOfItemSets(); pi ++) {
			ItemSet piset = prefix.getItemSet(pi);
			setIdx ++; // find next set
			itemIdx = -1; // start of the new set
			for (int pj = 0; pj < piset.size(); pj ++) {
				Item pitem = piset.getItem(pj);
				if (0 == pj) pSameSet = false;
				else pSameSet = true;
				nextItem:
					for (int i = setIdx; i < this.numOfItemSets(); i ++, setIdx ++) {
						ItemSet iset = this.getItemSet(i);
						itemIdx ++; // start from the next item in current set
						for (int j = itemIdx; j < iset.size(); j ++, itemIdx ++) {
							Item item = iset.getItem(j);
							if (item.equals(pitem)) {
								setIdx = i; // if find the item, record current set index
								itemIdx = j; // if find the item, record the item index in current set
								if (pi == (prefix.numOfItemSets() - 1) && pj == (piset.size() -1 )) {
									find = true;
								}
								break nextItem;
							}
						}
						if (pSameSet) return null; // cannot find the item in the same set in prefix, return null 
						itemIdx = -1; // if cannot find the item, start from the first item of next set
					}
			}
		}
		if (!find) return null;
		return this.subSequenceFrom(setIdx, itemIdx + 1);
	}

	public Sequence iExtension(Item i) {
		Sequence ret = new Sequence();
		for (ItemSet iset : this.getItemSets()) {
			ItemSet newiset = new ItemSet();
			for (Item ii : iset.getItems()) {
				newiset.addItem(ii);
			}
			ret.addItemSet(newiset);
		}
		if (0 == ret.getItemSets().size()) {
			ItemSet is = new ItemSet();
			is.addItem(i);
			ret.addItemSet(is);
			return ret;
		}
		ItemSet lastItemSet = ret.getItemSets().get(ret.getItemSets().size() - 1);
		lastItemSet.addItem(i);
		return ret;
	}

	public Sequence sExtension(Item i) {
		Sequence ret = new Sequence();
		for (ItemSet iset : this.getItemSets()) {
			ItemSet newiset = new ItemSet();
			for (Item ii : iset.getItems()) {
				newiset.addItem(ii);
			}
			ret.addItemSet(newiset);
		}
		ItemSet newItemSet = new ItemSet();
		newItemSet.addItem(i);
		ret.addItemSet(newItemSet);
		return ret;
	}
	
	/**
	 * The coverage of current sequence on another sequence
	 * <pre>
	 * e.g.
	 *     The coverage of <(1 2)(4)> on <(3 1 2)(5 4)> is 0.6
	 * </pre>
	 * @param s
	 * @return
	 */
	public double coverageOn(Sequence s) {
		if (s.contains(this)) {
			return (double)this.numOfItems() / s.numOfItems();
		}
		return 0.0;
	}
	
	/**
	 * Positions of items from a sequence in current sequence 
	 * if the given sequence is a subsequence of current sequence
	 * <pre>
	 * e.g.
	 *     The positions of <(1 2)(4)> in <(3 1 2)(5 4)> is [1, 2, 4]
	 * </pre>
	 * @param s
	 * @return null if cannot find s in current sequence
	 */
	public List<Integer> getPositions(Sequence s) {
		List<Integer> positions = new ArrayList<Integer>();
		int position = -1;
		int setIdx = -1, itemIdx = -1;
		boolean find = false, pSameSet = false;
		for (int pi = 0; pi < s.numOfItemSets(); pi ++) {
			ItemSet piset = s.getItemSet(pi);
			setIdx ++; // find next set
			itemIdx = -1; // start of the new set
			for (int pj = 0; pj < piset.size(); pj ++) {
				Item pitem = piset.getItem(pj);
				if (0 == pj) pSameSet = false;
				else pSameSet = true;
				nextItem:
					for (int i = setIdx; i < this.numOfItemSets(); i ++, setIdx ++) {
						ItemSet iset = this.getItemSet(i);
						itemIdx ++; // start from the next item in current set
						for (int j = itemIdx; j < iset.size(); j ++, itemIdx ++) {
							Item item = iset.getItem(j);
							if (item.equals(pitem)) {
								setIdx = i; // if find the item, record current set index
								itemIdx = j; // if find the item, record the item index in current set
								int accum = 0;
								for (int x = 0; x < setIdx; x ++) {
									int setSize = this.getItemSet(x).size();
									accum += setSize;
								}
								position = accum + itemIdx;
								positions.add(position);
								if (pi == (s.numOfItemSets() - 1) && pj == (piset.size() -1 )) {
									find = true;
								}
								break nextItem;
							}
						}
						if (pSameSet) return null; // cannot find the item in the same set in prefix, return null 
						itemIdx = -1; // if cannot find the item, start from the first item of next set
					}
			}
		}
		if (!find) return null;
		return positions;
	}

}
