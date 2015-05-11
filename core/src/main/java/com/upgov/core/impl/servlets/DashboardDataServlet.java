package com.upgov.core.impl.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

@SlingServlet(paths = "/bin/servlets/dashboard")
public class DashboardDataServlet extends SlingSafeMethodsServlet {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    QueryBuilder queryBuilder;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {

        JSONArray jsonCircleArray;
        JSONArray jsonLineArray;
        JSONArray jsonArray = new JSONArray();

        String dept = req.getParameter("dept");
        String location = req.getParameter("location");
        String assigned = req.getParameter("assigned");

        try {
            Session session = req.getResourceResolver().adaptTo(Session.class);
            SearchResult result = getAllComplaintAssignedTOEngineer(dept, location, assigned, session);

            log.info("json string---------->>" + getLineJsonArray(result).toString());
            jsonLineArray = getLineJsonArray(result);

            SearchResult totalTodayResult = getAllComplaintAssignedTOEngineer("-1d",location,assigned,dept, session);
            SearchResult totalWeekResult = getAllComplaintAssignedTOEngineer("-1w",location,assigned,dept, session);
            SearchResult totalMonthResult = getAllComplaintAssignedTOEngineer("-1M",location,assigned,dept, session);
            SearchResult totalYearResult = getAllComplaintAssignedTOEngineer("-1y",location,assigned,dept, session);

            log.info("circle json---->>" + getCircles(totalTodayResult, totalWeekResult, totalMonthResult, totalYearResult).toString());
            jsonCircleArray = getCircles(totalTodayResult, totalWeekResult, totalMonthResult, totalYearResult);
            JSONObject circles = new JSONObject();
            circles.put("circles", jsonCircleArray);
            JSONObject lines = new JSONObject();
            lines.put("lines", jsonLineArray);
            jsonArray.put(lines);
            jsonArray.put(circles);

            resp.getWriter().println(jsonArray);
        } catch (JSONException e) {
            log.error("JSONException::   " + e.getMessage());
        } catch (RepositoryException e) {
            log.error("RepositoryException:: " + e.getMessage());
        }

    }


    SearchResult getAllComplaintAssignedTOEngineer(String dept, String location, String currentAssignee, Session session) {
        Map queryMap = new HashMap();
        queryMap.put("path", "/content/usergenerated/content/UPGovForm");
        queryMap.put("1_property", "sling:resourceType");
        queryMap.put("1_property.value", "UPGov-POC/components/content/formData");
        queryMap.put("2_property", "location");
        queryMap.put("2_property.value", location);
        queryMap.put("3_property", "currentAssignee");
        queryMap.put("3_property.value", currentAssignee);
        queryMap.put("4_property", "department");
        queryMap.put("4_property.value", dept);
        queryMap.put("p.limit", "-1");
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);

        SearchResult result = query.getResult();
        return result;
    }

    SearchResult getAllComplaintAssignedTOEngineer(String range,String location, String currentAssignee, String dept, Session session) {
        Map queryMap = new HashMap();
        queryMap.put("path", "/content/usergenerated/content/UPGovForm");
        queryMap.put("1_property", "sling:resourceType");
        queryMap.put("1_property.value", "UPGov-POC/components/content/formData");
        queryMap.put("2_property", "location");
        queryMap.put("2_property.value", location);
        queryMap.put("3_property", "currentAssignee");
        queryMap.put("3_property.value", currentAssignee);
        queryMap.put("4_property", "department");
        queryMap.put("4_property.value", dept);
        queryMap.put("5_relativedaterange.property", "jcr:created");
        queryMap.put("5_relativedaterange.lowerBound", range);
        queryMap.put("5_relativedaterange.upperBound", "0");
        queryMap.put("p.limit", "-1");
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);

        SearchResult result = query.getResult();
        return result;
    }

    JSONArray getLineJsonArray(SearchResult result) throws RepositoryException, JSONException {
        List<Hit> hits = result.getHits();
        if (hits.size() == 0) {
            return new JSONArray();
        }
        JSONArray jsonLineArray = new JSONArray();
        int[] crLineValues = new int[]{0, 0, 0, 0, 0, 0, 0};
        int[] coLineValues = new int[]{0, 0, 0, 0, 0, 0, 0};
        int[] cfLineValues = new int[]{0, 0, 0, 0, 0, 0, 0};
        for (Hit temp : hits) {
            Resource res = temp.getResource();
            ValueMap properties = res.adaptTo(ValueMap.class);
            Date dt = properties.get("jcr:created", Date.class);
            String status = properties.get("complaintStatus", String.class);
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 2;
            dayOfWeek = (dayOfWeek < 0) ? 6 : dayOfWeek;
            if (status.equals("completed")) {
                crLineValues[dayOfWeek]++;
            } else if (status.equals("inProgress")) {
                coLineValues[dayOfWeek]++;
            } else {
                cfLineValues[dayOfWeek]++;
            }
        }
        jsonLineArray.put(getLineObject(coLineValues, "complainOpened"));
        jsonLineArray.put(getLineObject(crLineValues, "complainResolve"));
        jsonLineArray.put(getLineObject(cfLineValues, "complainFiled"));

        return jsonLineArray;
    }

    JSONObject getLineObject(int[] line, String type) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("type", type);
        object.put("mon", line[0]);
        object.put("twe", line[1]);
        object.put("wed", line[2]);
        object.put("thr", line[3]);
        object.put("fri", line[4]);
        object.put("sat", line[5]);
        object.put("sun", line[6]);

        return object;
    }

    JSONObject getCircleObject(int[] circle, String size, String type) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("type", type);
        object.put("cf", circle[0]);
        object.put("co", circle[1]);
        object.put("cr", circle[2]);
        object.put("size", size);
        return object;
    }

    JSONObject getCircleJsonObject(SearchResult result, String type) throws RepositoryException, JSONException {
        List<Hit> hits = result.getHits();
        if (hits.size() == 0) {
            return new JSONObject();
        }
        int[] totalResults = new int[]{0, 0, 0};

        for (Hit temp : hits) {
            Resource res = temp.getResource();
            ValueMap properties = res.adaptTo(ValueMap.class);
            String status = properties.get("complaintStatus", String.class);

            if (status.equals("completed")) {
                totalResults[1]++;
            } else if (status.equals("inProgress")) {
                totalResults[2]++;
            } else {
                totalResults[0]++;
            }
        }

        return getCircleObject(totalResults, String.valueOf(hits.size()), type);
    }

    JSONArray getCircles(SearchResult today, SearchResult week, SearchResult month, SearchResult year) throws RepositoryException, JSONException {
        JSONObject totalToday = getCircleJsonObject(today, "today");
        JSONObject totalWeek = getCircleJsonObject(week, "week");
        JSONObject totalMonth = getCircleJsonObject(month, "month");
        JSONObject totalYear = getCircleJsonObject(year, "year");

        return getCircleJsonArray(totalToday, totalWeek, totalMonth, totalYear);

    }

    JSONArray getCircleJsonArray(JSONObject today, JSONObject week, JSONObject month, JSONObject year) {
        JSONArray jsonCircleArray = new JSONArray();
        jsonCircleArray.put(today);
        jsonCircleArray.put(week);
        jsonCircleArray.put(month);
        jsonCircleArray.put(year);
        return jsonCircleArray;
    }

}
