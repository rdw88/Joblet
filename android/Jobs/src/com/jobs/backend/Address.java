package com.jobs.backend;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Address {
	public static final String PROFILE = "http://ryguy.me/profile/";
	public static final String LISTING = "http://ryguy.me/listing/";

	public static String urlEncode(Map<String, String> map) {
		Set<String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		String encoded = "";

		try {
			while (it.hasNext()) {
				String next = it.next();
				String key = URLEncoder.encode(next, "UTF-8");
				String val = URLEncoder.encode(map.get(next), "UTF-8");
				String encode = key + "=" + val;
				encoded += it.hasNext() ? encode + "&" : encode;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return encoded;
	}
}
