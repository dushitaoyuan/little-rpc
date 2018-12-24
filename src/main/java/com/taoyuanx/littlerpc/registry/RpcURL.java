package com.taoyuanx.littlerpc.registry;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.taoyuanx.littlerpc.server.ServerConstant;
import com.taoyuanx.littlerpc.util.IpUtil;

public class RpcURL {

	private String host;
	private int port;
	private String service;
	private String version;
	private Integer weight;
	private Map<String, String> parameters;

	public RpcURL(String host, int port, String service, Map<String, String> parameters) {
		super();
		this.host = host;
		this.port = port;
		this.service = service;
		this.parameters = parameters;
		
		if(parameters!=null) {
			if(parameters.containsKey(ServerConstant.VERSION_KEY)) {
				this.version=parameters.get(ServerConstant.VERSION_KEY);	
			}
			if(parameters.containsKey(ServerConstant.WEIGHT_KEY)) {
				this.weight=Integer.parseInt(parameters.get(ServerConstant.WEIGHT_KEY));	
			}
		}
		
	}

	
	


	public static RpcURL valueOf(String url) {
		if (url == null || (url = url.trim()).length() == 0) {
			throw new IllegalArgumentException("url can't be  null");
		}
		url=decode(url);
		String host = null;
		String service = null;
		int port = 0;
		Map<String, String> parameters = null;
		int i = url.indexOf("?");
		if (i >= 0) {
			String[] parts = url.substring(i + 1).split("\\&");
			parameters = new HashMap<String, String>();
			for (String part : parts) {
				part = part.trim();
				if (part.length() > 0) {
					int j = part.indexOf('=');
					if (j >= 0) {
						parameters.put(part.substring(0, j), part.substring(j + 1));
					} else {
						parameters.put(part, part);
					}
				}
			}
			url = url.substring(0, i);
		}
		i = url.indexOf("/");
		if (i >= 0) {
			service = url.substring(i + 1);
			url = url.substring(0, i);
		}
		i = url.lastIndexOf(":");
		if (i >= 0 && i < url.length() - 1) {
			port = Integer.parseInt(url.substring(i + 1));
			url = url.substring(0, i);
		}
		if (url.length() > 0)
			host = url;
		return new RpcURL(host, port, service, parameters);
	}

	public String buildString(String... parameters) {
		StringBuilder buf = new StringBuilder();
		if (host != null && host.length() > 0) {
			buf.append(host);
			if (port > 0) {
				buf.append(":");
				buf.append(port);
			}
		}
		if (service != null && service.length() > 0) {
			buf.append("/");
			buf.append(service);
		}
		buildParameters(buf, true, parameters);
		return encode(buf.toString());
	}
	
	public String toUrlString(String... parameters) {
		StringBuilder buf = new StringBuilder();
		if (host != null && host.length() > 0) {
			buf.append(host);
			if (port > 0) {
				buf.append(":");
				buf.append(port);
			}
		}
		if (service != null && service.length() > 0) {
			buf.append("/");
			buf.append(service);
		}
		buildParameters(buf, true, parameters);
		
		return encode(buf.toString());
	}

	private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
		if (this.parameters != null && this.parameters.size() > 0) {
			List<String> includes = (parameters == null || parameters.length == 0 ? null : Arrays.asList(parameters));
			boolean first = true;
			for (Map.Entry<String, String> entry : new TreeMap<String, String>(this.parameters).entrySet()) {
				if (entry.getKey() != null && entry.getKey().length() > 0
						&& (includes == null || includes.contains(entry.getKey()))) {
					if (first) {
						if (concat) {
							buf.append("?");
						}
						first = false;
					} else {
						buf.append("&");
					}
					buf.append(entry.getKey());
					buf.append("=");
					buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
				}
			}
		}
	}



	public Map<String, String> getParameters() {
		return parameters;
	}
	public String getParameter(String paramKey) {
		return parameters.get(paramKey);
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getService() {
		return service;
	}
	
	public String getHostPort() {
		return IpUtil.getIpPort(host, port);
	}


	public String getParameters(String key) {
		if (parameters != null) {
			return parameters.get(key);
		}
		return "";
	}
	public Integer getWeight() {
		return weight;
	}

/*	public static String makeURLKey(String url) {
		if(null==url||url.trim().length()==0){
			throw new IllegalArgumentException("url == null");
		}
		RpcURL valueOf = RpcURL.valueOf(url);
		return makeURLKey(valueOf);
	}
	
	public static String makeURLKey(RpcURL url){
		if(null==url){
			throw new IllegalArgumentException("url == null");
		}
		String service = url.getService();
		if()
		String version=;
		return service+"#"+version;
	}
*/
	@Override
	public String toString() {
		return "RpcURL [host=" + host + ", port=" + port + ", service=" + service + ", parameters=" + parameters + "]";
	}
	

	








	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpcURL other = (RpcURL) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	public static String encode(String encode) {
		try {
			return URLEncoder.encode(encode, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String decode(String encode) {
		try {
			return URLDecoder.decode(encode, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}


	public static void main(String[] args) {
		String url = "10.120.21.13:20880/com.ncs.rpc.api.ContractService?anyhost=true&application=online-ncrd-beian-privider&default.group=online&default.timeout=10000&dubbo=2.5.3&executes=50&interface=com.ncs.rpc.api.ContractService&loadbalance=leastactive&logger=slf4j&methods=recordContract,makeFiling&owner=taoyuan&pid=28299&revision=0.0.1-SNAPSHOT&serialization=dubbo&side=provider&timeout=10000×tamp=1539598478136&version=1.0";

		RpcURL valueOf = RpcURL.valueOf(url);
		System.out.println(valueOf);
		String a[] = new String[valueOf.getParameters().size()];
		valueOf.getParameters().keySet().toArray(a);
		System.out.println(url);
		System.out.println(valueOf.getParameters("version"));
		System.out.println(URLEncoder.encode(valueOf.toUrlString()));
	}
}
