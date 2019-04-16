package com.mulesoft.java;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class APExchangeClean {
	public static String HTTPS_ANYPOINT_MULESOFT_COM = "https://anypoint.mulesoft.com";
	public static boolean makeApiNameBusinessGroupSensitive = false;
	public static String RESOURCES_DIR = "src/main/resources";
	public static String API_VERSION_HEADER_MSG = "APExchangeClean version 1.0.0";

	public static void main(String[] args) {

		try {
			if (args.length <= 5) {
				System.err.println(API_VERSION_HEADER_MSG);
				System.err.println("\n");
				printHelp();
			} else if (args[0].equals("clean")) {
				System.err.println(API_VERSION_HEADER_MSG + " Starting " + args[0]);
				cleanProject((args.length > 1) ? args[1] : "userName",
						(args.length > 2) ? args[2] : "userPass",
						(args.length > 3) ? args[3] : "my-organization-anypoint-orgid",
						(args.length > 4) ? args[4] : "artifactId", 
						(args.length > 5) ? args[5] : "version");
				System.err.println(API_VERSION_HEADER_MSG + " Done " + args[0]);
				System.err.println("\n");
			} else {
				printHelp();
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(500);
		}
	}

	private static void printHelp() {
		System.err.println("Usage: java -jar APExchangeClean {operation} [parameters]\n");
		System.err.println("  operations:");
		System.err.println("    clean  -Remove the specified artifactId and version from the org's Exchange");
		System.err.println("       Parameters:");
		System.err.println("          userName      -Anypoint user name required");
		System.err.println("          userPassword  -Anypoint user's password required");
		System.err.println("          orgid         -my-organization-anypoint-orgid");
		System.err.println("          artifactId    -Project's Maven artifactId");
		System.err.println("          version       -Project's version");
	}

	private static void cleanProject(String userName, String userPass,
			String orgId, String artifactId, String artifactVersion) throws Exception {

		Client client = null;
		client = ClientBuilder.newClient();
		client.register(JacksonJsonProvider.class).register(MultiPartFeature.class);

		// clean steps

		/*
		 * Authenticate with Anypoint Platform
		 */
		String apToken = getAPToken(client, userName, userPass);
		String authorizationHdr = "Bearer " + apToken;

		/*
		 * Remove artifact/version from Exchange
		 */
		int resultcode = hardDelete(client, authorizationHdr, orgId, artifactId, artifactVersion);
		if (resultcode == 204) {
			System.err.println("  " + artifactId + " " + artifactVersion + " removed from Exchange.");
		} else if (resultcode == 404) {
			System.err.println("  " + artifactId + " " + artifactVersion + " is not in Exchange.");
		} else {
			System.err.println("Unexpected error " + resultcode);
		}
	}

	@SuppressWarnings("unchecked")
	private static String getAPToken(Client restClient, String user, String password) throws JsonProcessingException {
		
		if (user.equalsIgnoreCase("~~~Token~~~")) {
			return password;
		}
		
		String token = null;
		LinkedHashMap<String, Object> loginValues = new LinkedHashMap<String, Object>();
		loginValues.put("username", user);
		loginValues.put("password", password);
		String payload = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(loginValues);
		WebTarget target = restClient.target(HTTPS_ANYPOINT_MULESOFT_COM).path("accounts/login");

		Response response = target.request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(payload, MediaType.APPLICATION_JSON));

		int statuscode = 500;
		Map<String, Object> result = null;
		if (response != null) {
			statuscode = response.getStatus();
		}
		if (response != null && response.getStatus() == 200) {
			result = response.readEntity(Map.class);
			token = (String) result.get("access_token");
		} else {
			System.err.println("Failed to login...check credentials");
			System.exit(statuscode);
		}

		return token;
	}

	private static int hardDelete(Client restClient, String authorizationHdr,
			String my_ap_org_id, String artifactId, String artifactVersion) {
		
		WebTarget target = restClient.target(HTTPS_ANYPOINT_MULESOFT_COM).path("exchange/api/v1/organizations/").path(my_ap_org_id).path("assets").path(my_ap_org_id).path(artifactId).path(artifactVersion);
		Response response = target.request().header("Authorization", authorizationHdr).header("X-Delete-Type", "hard-delete").delete();

		int statuscode = 500;
		ArrayList<LinkedHashMap<String, Object>> result = null;
		if (response != null) {
			statuscode = response.getStatus();
		} else {
			statuscode = 500;
		}
		
		return statuscode;
	}
}
