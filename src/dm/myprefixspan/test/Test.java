package dm.myprefixspan.test;

import java.util.List;

import dm.myprefixspan.PrefixSpan;
import dm.myprefixspan.sequence.Sequence;




public class Test {
	/**
	 * 
	 * @param args
	 */
	static public void main(String[] args) {
		if (4 != args.length) {
			usage();
			return;
		}

		PrefixSpan p = new PrefixSpan(args[0], Double.valueOf(args[1]));
		p.setMaxGap(Integer.valueOf(args[2]));
		p.setMaxPattern(Integer.valueOf(args[3]));
		System.out.println(p);
//		Sequence seq = Sequence.fromString("1 6", "#", " ");
//		System.out.println(seq);
		//		seq.iExtension(Item.fromString("3"));
		//		System.out.println(seq);
		//		seq.sExtension(Item.fromString("3"));
		//		System.out.println(seq);
//				for (Sequence s : p.getDatabase().getSequences()) {
////					System.out.println("== " + s.getSuffix(Sequence.fromString("6#4", "#", " ")));
//					System.out.println("== " + s.getPositions(Sequence.fromString("6#4", "#", " ")));
//				}

//				System.out.println("===\n" + prefixspan.project(seq, prefixspan.getDatabase())+"===");

		System.out.println("\n===============================");
		p.prefixSpan();
		List<Sequence> fs = p.getFreqPattern();
		System.out.println("\nFrequent Patterns:");
		for (Sequence s : fs) {
			System.out.println(s);
		}
	}

	private static void usage() {
		System.out.println("Usage:");
		System.out.println("\tfile_name min_support max_gap max_pattern_length");
		System.out.println("\tNOTE:");
		System.out.println("\t\tmin_support: percentage");
		System.out.println("\t\tmax_gap: max gap between two items, -1 for unlimited (INT_MAX)");
		System.out.println("\t\tmax_pattern_length: max pattern length, -1 for unlimited (INT_MAX)");
	}
}
