package com.francelabs.datafari.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.francelabs.datafari.servlets.admin.ConfigDeduplication;
import com.francelabs.datafari.user.CodesUser;
import com.francelabs.datafari.user.Like;
import com.francelabs.datafari.utils.UpdateNbLikes;

/**
 * Servlet implementation class addLikes
 */
@WebServlet("/addLikes")
public class AddLikes extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AddLikes.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddLikes() {
        super();
        BasicConfigurator.configure();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject jsonResponse = new JSONObject();
		request.setCharacterEncoding("utf8");
		response.setContentType("application/json");
		String documentId = request.getParameter("idDocument");
		if (documentId!=null ){
			try {
				Principal userPrincipal = request.getUserPrincipal(); 
				//checking if the user is connected
				if (userPrincipal == null){
					jsonResponse.put("code", CodesUser.NOTCONNECTED)
					.put("statut", "Please reload the page, you'r not connected");
				}else{
					String username = request.getUserPrincipal().getName(); //get the username  
					int returnResult = Like.addLike(username, documentId); 
					if (returnResult == CodesUser.ALLOK){
						UpdateNbLikes.increment(documentId);
						jsonResponse.put("code", 0);
					}else if (returnResult == CodesUser.ALREADYPERFORMED){
						// if the like was already done (attempt to increase illegaly the likes)
						jsonResponse.put("code", CodesUser.ALREADYPERFORMED);
					}else{
						jsonResponse.put("code", CodesUser.PROBLEMCONNECTIONMONGODB)
						.put("statut", "Problem while connecting to database");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}else{
			try {
				jsonResponse.put("code", -1)
				.put("statut", "Query malformed");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
		}
		PrintWriter out = response.getWriter();
		out.print(jsonResponse);
	}

}