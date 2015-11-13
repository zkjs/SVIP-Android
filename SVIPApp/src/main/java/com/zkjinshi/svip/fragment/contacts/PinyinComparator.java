package com.zkjinshi.svip.fragment.contacts;

import java.util.Comparator;

public class PinyinComparator implements Comparator<SortModel> {

	public int compare(SortModel o1, SortModel o2) {

		if (o1.sortLetters.equals("@") || o2.sortLetters.equals("#")) {
			return -1;
		} else if (o1.sortLetters.equals("#") || o2.sortLetters.equals("@")) {
			return 1;
		} else {
			System.out.println("o1.sortLetters:" + o1.sortLetters);
			System.out.println("o2.sortLetters:" + o2.sortLetters);

			return o1.sortLetters.compareTo(o2.sortLetters);
		}
	}

}
