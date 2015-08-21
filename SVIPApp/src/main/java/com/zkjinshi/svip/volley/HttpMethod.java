package com.zkjinshi.svip.volley;

/**
 * http请求方法
 */
public interface HttpMethod {
	
	/**
	 * 表示GET请求
	 */
    public static final int GET = 0x00;
    
    /**
	 * 表示POST请求
	 */
    public static final int POST = 0x01;
    
    /**
	 * 表示PUT请求
	 */
    public static final int PUT = 0x02;
    
    /**
     * 表示DELETE请求
     */
    public static final int DELETE = 0x03;
    
    /**
     * 表示HEAD请求
     */
    public static final int HEAD = 0x04;
}
