package dm.myprefixspan.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Item set
 * @author zhhailon
 *
 */
public class ItemSet {

	private List<Item> items;

	/**
	 * 
	 * @param str the item set string
	 * @param itemSplitStr the delimiting regex for item set splitting in an item set
	 * @return
	 */
	static public ItemSet fromString(String str, String itemSplitStr) {
		ItemSet iset = new ItemSet();
		String[] itemStrs = str.split(itemSplitStr);
		for (String itemStr : itemStrs) {
			Item item = Item.fromString(itemStr);
			iset.addItem(item);
		}
		return iset;
	}

	@Override
	public String toString() {
		String ret = "( ";
		for (Item i : this.getItems()) {
			ret += i.toString() + " ";
		}
		return ret + ") ";
	}

	public boolean equals(ItemSet s) {
		if (size() != s.size()) return false;
		for (int i = 0; i < size(); i ++) {
			Item i1 = this.getItems().get(i);
			Item i2 = s.getItems().get(i);
			if (!i1.equals(i2)) return false;
		}
		return true;
	}

	public List<Item> getItems() {
		if (null == items) {
			items = new ArrayList<Item>();
		}
		return items;
	}

	public void addItem(Item item) {
		this.getItems().add(item);
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Item getItem(int idx) {
		return this.getItems().get(idx);
	}

	/**
	 * Number of items
	 * @return
	 */
	public int size() {
		return this.getItems().size();
	}
}
