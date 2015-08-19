/*******************************************************************************
 *  * Copyright 2015 France Labs
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * 
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *******************************************************************************/
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

import com.francelabs.datafari.user.CodesUser;
import com.francelabs.datafari.user.Favorite;
import com.francelabs.datafari.user.Like;
import com.francelabs.datafari.utils.UpdateNbLikes;

/**
 * Servlet implementation class Unlike
 */
@WebServlet("/unlike")
public class Unlike extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AddFavorite.class.getName());
    
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Unlike() {
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
		JSONObject jsonResponse = new JSONObject();
		request.setCharacterEncoding("utf8");
		response.setContentType("application/json");
		String documentId = request.getParameter("idDocument"); 
		System.out.println("here");
		if (documentId!=null ){
			try {
				Principal userPrincipal = request.getUserPrincipal();
				// checking if the user is connected
				if (userPrincipal == null){
					jsonResponse.put("code", CodesUser.NOTCONNECTED)
					.put("statut", "Please reload the page, you'r not connected");
				}else{
					String username = request.getUserPrincipal().getName();
					int returnResult = Like.unlike(username, documentId);
					if (returnResult == CodesUser.ALLOK){
						UpdateNbLikes.decrement(documentId);
						jsonResponse.put("code", returnResult);
					}else if(returnResult == CodesUser.ALREADYPERFORMED) {
						// the document isn't liked (attempt to decrease the likes of a document)
						jsonResponse.put("code", returnResult);
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
