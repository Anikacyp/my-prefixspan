package dm.myprefixspan;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import dm.myprefixspan.sequence.Database;
import dm.myprefixspan.sequence.Item;
import dm.myprefixspan.sequence.ItemSet;
import dm.myprefixspan.sequence.Sequence;




/**
 * PrefixSpan
 * @author zhhailon
 *
 */
public class PrefixSpan {

	/**
	 * Input data file name
	 */
	private String dataFileName;
	/**
	 * Minimum support
	 */
	private double minSupport;
	/**
	 * Sequence database
	 */
	private Database db;
	/**
	 * Max gap between two items in a sequence
	 */
	private int maxGap = Integer.MAX_VALUE;
	/**
	 * Max pattern length
	 */
	private int maxPattern = Integer.MAX_VALUE;
	/**
	 * Frequent sequence and the corresponding id
	 */
	private List<Sequence> freqPattern;
	/**
	 * Mininmum coverage
	 */
	private double minCoverage = 0.0;

	/**
	 * 
	 * @param fileName
	 * @param minSup
	 */
	public PrefixSpan(String fileName, double minSup) {
		setDataFileName(fileName);
		readData();
		setMinSupport(minSup * getDatabase().getSequences().size());
	}

	/**
	 * Read sequences from input data file
	 */
	private void readData() {
		FileReader fReader = null;
		String line = "";
		try {
			fReader = new FileReader(this.getDataFileName());
			BufferedReader reader = new BufferedReader(fReader);
			db = new Database();
			int seqCount = -1;
			while (null != (line = reader.readLine())) {
				seqCount ++;
				Sequence seq = Sequence.fromString(line, "#", " ");
				seq.addId(seqCount);
				for (int i = 0; i < seq.getItemSets().size(); i ++) {
					for (int j = 0; j < seq.getItemSet(i).size(); j ++) {
						Item.Occurrence occ = 
								seq.getItemSet(i).getItem(j).new Occurrence(seqCount, i, j);
						seq.getItemSet(i).getItem(j).addOccurrence(occ);
					}
				}
				for (ItemSet iset : seq.getItemSets()) {
					for (Item i : iset.getItems()) {
						i.addSeqId(seqCount);
					}
				}
				db.addSequence(seq);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public double getMinCoverage() {
		return minCoverage;
	}

	public void setMinCoverage(double minCoverage) {
		if (minCoverage < 0) {
			this.minCoverage = 0.0;
			return;
		}
		this.minCoverage = minCoverage;
	}

	public int getMaxGap() {
		return maxGap;
	}

	public void setMaxGap(int maxGap) {
		if (-1 == maxGap) {
			this.maxGap = Integer.MAX_VALUE;
			return;
		}
		this.maxGap = maxGap;
	}

	public int getMaxPattern() {
		return maxPattern;
	}

	public void setMaxPattern(int maxPattern) {
		if (-1 == maxPattern) {
			this.maxPattern = Integer.MAX_VALUE;
			return;
		}
		this.maxPattern = maxPattern;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public double getMinSupport() {
		return minSupport;
	}

	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}

	public Database getDatabase() {
		return db;
	}

	public List<Sequence> getFreqPattern() {
		if (null == freqPattern) {
			freqPattern = new ArrayList<Sequence>();
		}
		return freqPattern;
	}

	public void addFreqPattern(Sequence seq) {
		this.getFreqPattern().add(seq);
	}

	public String toString() {
		return "PrefixSpan [min_sup=" + getMinSupport() 
				+ "]\nDatabase: \n" + db.toString();
	}

	/**
	 * Project
	 * @param seq
	 * @param db
	 * @return an s-projected database
	 */
	public Database project(Sequence seq, Database db) {
		//		if (db.size() < this.getMinSupport())
		//			return null;
		Database projDb = new Database();

		for (Sequence s : db.getSequences()) {
			Sequence suffix = s.getSuffix(seq);
			if (null != suffix) projDb.addSequence(suffix);
		}
		Map<Integer, Integer> mapItems = new HashMap<Integer, Integer>();
		for (Item item : Item.allItems()) {
			for (Sequence s : projDb.getSequences()) {
				if (s.contains(item)) {
					if (mapItems.containsKey(item.getLabel())) {
						mapItems.put(item.getLabel(), mapItems.get(item.getLabel()) + 1);
					} else {
						mapItems.put(item.getLabel(), 1);
					}
				}
			}
		}
		for (int key : mapItems.keySet()) {
			int value = mapItems.get(key);
			if (value < this.getMinSupport()) {
				for (Sequence s : projDb.getSequences()) {
					s.removeItem(key);
				}
			}
		}
		return projDb;
	}

	/**
	 * 
	 */
	public void prefixSpan() {
		Set<Item> step1Items = new TreeSet<Item>();
		for (Item i : Item.allItems()) {
			if (i.getSeqIds().size() >= this.getMinSupport())  
				step1Items.add(i);
		}
		for (Item item : step1Items) {
			Sequence seq = new Sequence();
			seq.setIds(item.getSeqIds());
			ItemSet iset = new ItemSet();
			iset.addItem(item);
			seq.addItemSet(iset);
			prefixSpan(seq, this.project(seq, this.getDatabase()));
		}
	}
	/**
	 * 
	 * @param seq
	 * @param db
	 */
	public void prefixSpan(Sequence seq, Database db) {
		if (db.size() < this.getMinSupport()) {
			return;
		}

		// If the length is greater than the given maxPattern, return the method
		if (seq.numOfItems() != 0 
				&& seq.numOfItems() > this.getMaxPattern()) 
			return;

		this.addFreqPattern(seq);

		Set<Integer> step1Items = new HashSet<Integer>();
		for (Sequence s : db.getSequences()) {
			for (ItemSet iset : s.getItemSets()) {
				for (Item i : iset.getItems()) {
					step1Items.add(i.getLabel());
				}
			}
		}
		for (int label : step1Items) {
			Sequence iext = seq.iExtension(Item.getItem(label)); // i-extension
			int iextSup = 0;
			for (Sequence s : this.getDatabase().getSequences()) {
				Sequence iextSuffix = s.getSuffix(iext);
				if (null != iextSuffix) {
					for (int id : iextSuffix.getIds()) iext.addId(id);
					iextSup ++;
				}
			}
			if (iextSup >= this.getMinSupport()) {
				prefixSpan(iext, project(iext, this.getDatabase()));
			}
			Sequence sext = seq.sExtension(Item.getItem(label)); // s-extension
			int sextSup = 0;
			for (Sequence s : this.getDatabase().getSequences()) {
				Sequence sextSuffix = s.getSuffix(sext);
				if (null != sextSuffix) {
					for (int id : sextSuffix.getIds()) sext.addId(id);
					sextSup ++;
				}
			}
			if (sextSup >= this.getMinSupport()) {
				prefixSpan(sext, project(sext, this.getDatabase()));
			}
		}
	}

}
