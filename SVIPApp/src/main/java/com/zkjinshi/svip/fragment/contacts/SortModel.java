package com.zkjinshi.svip.fragment.contacts;

public class SortModel extends Contact {

	public SortModel(String name, String number, String sortKey) {
		super(name, number, sortKey);
	}

	public String sortLetters; //显示数据拼音的首字母

	public SortToken sortToken = new SortToken();

	public String shopName;

	public String fuid;

	public String shopid;
}
