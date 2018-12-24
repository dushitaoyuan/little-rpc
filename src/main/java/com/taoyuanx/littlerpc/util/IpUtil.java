package com.taoyuanx.littlerpc.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class IpUtil {
	private static final String LOCAL="127.0.0.1",LOOP_BACK="0.0.0.0";
	/**
	 * 
	 * @param NetPrefix 
	 * @return
	 * @throws Exception
	 */
	public static String getNetAddress(String netPrefix) throws Exception {
		try {
			if(netPrefix==null){
				return getNetAddress();
			}
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface network = networkInterfaces.nextElement();
				Enumeration<InetAddress> net = network.getInetAddresses();
				while (net.hasMoreElements()) {
					InetAddress addr = net.nextElement();
					if (addr.isSiteLocalAddress()&&addr.getHostAddress().startsWith(netPrefix)) {
						return addr.getHostAddress();
					}
				}
			}
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	

	/**
	 * @return
	 * @throws Exception
	 */
	public static String getNetAddress(){
	try {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
				.getNetworkInterfaces();
		
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface network = networkInterfaces.nextElement();
			Enumeration<InetAddress> net = network.getInetAddresses();
			while (net.hasMoreElements()) {
				InetAddress addr = net.nextElement();
				if (addr.isSiteLocalAddress()) {
					return addr.getHostAddress();
				}
			}
		}
		return InetAddress.getLocalHost().getHostAddress();

	} catch (Exception e) {
		throw new RuntimeException(e);
	}
	}
    private static Pattern pattern = Pattern.compile("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
	
    public static boolean isValid(String ip){
    	if(ip==null||"".equals(ip)) {
    		return false;
    	}
		return (pattern.matcher(ip).matches()&&!LOCAL.equals(ip)&&!LOOP_BACK.equals(ip));
	}
    public static String getIpPort(String ip,Integer port){
    	return ip+":"+port;
    }
    public static String getIp(String address){
    	return address.substring(0, address.indexOf(":"));
    }
    public static Integer getPort(String address){
    	return Integer.parseInt(address.substring(address.indexOf(":")+1));
    }
   
	


}
