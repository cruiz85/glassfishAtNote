package com.glassfish.test.server;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;


import com.glassfish.test.client.GreetingService;
import com.glassfish.test.client.Template;
import com.glassfish.test.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {


	private EntityManager em;
	private String PERSISTENCE_UNIT_NAME = "System";
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	
	
	public void saveTemplate(Template template) {
		EntityManager entityManager = emf.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();

		entityTransaction.begin();
		if (template.getId() == null) {
			entityManager.persist(template);
			entityManager.flush();
		} else {

			entityManager.merge(template);
			entityManager.flush();
		}
		
		entityTransaction.commit();
		entityManager.close();


	}
//	
//	public void saveTemplate(Template template) {
//		emf =  Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
//		em = emf.createEntityManager();
//
//		em.getTransaction().begin();
//
//		
//		em.persist(template);
//
//		em.getTransaction().commit();
//		em.close();
//	    emf.close();
//
//
//	}
	public String greetServer(String input) throws IllegalArgumentException {
		
		
		Template template = new Template("cesan");
		saveTemplate(template);
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
		
		
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
