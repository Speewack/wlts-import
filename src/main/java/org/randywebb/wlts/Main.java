/**
 * 
 */
package org.randywebb.wlts;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.randywebb.wlts.beans.DetailedMember;
import org.randywebb.wlts.ldstools.json.DetailedMemberConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author randyw
 *
 */
public class Main {

	private static Logger log = LoggerFactory.getLogger(Main.class);
	/**
	 * @param args
	 * @throws Exception 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws Exception {
		
		
		// Parse JSON Membership file into beans
		
		List<DetailedMember> members = new LinkedList<DetailedMember>();
		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("detailedmembership.json")));
		
		JSONArray membersJSON = (JSONArray) obj;
		
		DetailedMemberConsumer action = new DetailedMemberConsumer(members);
		
		membersJSON.forEach(action);
		
		for (DetailedMember m : members)
		{
			System.out.println(m.toString());
		}

	}
}
