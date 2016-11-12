/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import dao.Site;
import dao.StatisticsObj;
import dao.WatchObj;
import dao.detailsObject;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BB3605
 */
public class HSQL_Manager {

    private static Connection connection = null;

    public static void init(String url, String user, String pass) throws ClassNotFoundException, SQLException {
        Class.forName("org.hsqldb.jdbcDriver");
        connection = DriverManager.getConnection(
                url, user, pass);
        connection.setAutoCommit(true);
        connection.createStatement().execute("SET DATABASE SQL AVG SCALE 3");
    }

    public static void exit() {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(HSQL_Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean putNewStat(WatchObj inpWatchObj) {
        try {
            PreparedStatement statement = connection.prepareStatement("insert into public.statistics(\"URL\", \"STATUS\",\"RESPONSE_CODE\") values (?,?,?)");
            statement.setString(1, inpWatchObj.getSite().getAddress());
            statement.setInt(2, inpWatchObj.getStatus());
            statement.setInt(3, inpWatchObj.getResponseCode());

            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(HSQL_Manager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static ArrayList<Site> getSites() {
        ArrayList<Site> returnList = new ArrayList<>();
        try {
            ResultSet resultSet;
            Statement statement;

            statement = connection.createStatement();
            resultSet = statement.executeQuery("select site from public.sites");
            while (resultSet.next()) {
                returnList.add(new Site(resultSet.getString("site")));
            }

            return returnList;
        } catch (SQLException ex) {
            Logger.getLogger(HSQL_Manager.class.getName()).log(Level.SEVERE, null, ex);
            return returnList;
        }
    }

    public static boolean addSite(Site inpSite) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO PUBLIC.SITES(\"SITE\") VALUES (?)");
            statement.setString(1, inpSite.getAddress());

            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean removeSite(Site inpSite) {
        //System.out.println("Removing: " + inpSite.getAddress());
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM PUBLIC.SITES WHERE SITE = ?");
            statement.setString(1, inpSite.getAddress());

            statement.executeUpdate();

            statement = connection.prepareStatement("DELETE FROM PUBLIC.STATISTICS WHERE URL = ?");
            statement.setString(1, inpSite.getAddress());

            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public static ArrayList<StatisticsObj> getStatistics(Site inpSite) {
        ArrayList<StatisticsObj> returnList = new ArrayList<>();
        try {
            ResultSet resultSet;
            PreparedStatement statement;

            statement = connection.prepareStatement("with overall as (SELECT count(url) total FROM PUBLIC.STATISTICS\n"
                    + "where url = ?\n"
                    + "group by url)\n"
                    + "select a.url, a.status, round(a.res / b.total, 2) res\n"
                    + "from\n"
                    + "(SELECT url, status, count(url) res FROM PUBLIC.STATISTICS\n"
                    + "where url = ?\n"
                    + "group by url, status)  a, overall b;");
            statement.setString(1, inpSite.getAddress());
            statement.setString(2, inpSite.getAddress());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                returnList.add(new StatisticsObj(new WatchObj(resultSet.getInt("status"), new Site(resultSet.getString("url"))), resultSet.getDouble("res")));
            }

            return returnList;
        } catch (SQLException ex) {
            Logger.getLogger(HSQL_Manager.class.getName()).log(Level.SEVERE, null, ex);
            return returnList;
        }
    }
    
    public static ArrayList<detailsObject> getDetails(String site){
        ArrayList<detailsObject> returnList = new ArrayList<>();
        try {
            ResultSet resultSet;
            PreparedStatement statement;

            statement = connection.prepareStatement("SELECT a.URL, a.CHECK_DATE, a.RESPONSE_CODE, case b.shortDesc when is null then 'Unknown' else b.shortDesc end shortDesc, case b.longDesc when is null then 'Unknown' else b.longDesc end longDesc FROM STATISTICS a left join responsecodes b on a.response_code = b.response_code where url = ?");
            statement.setString(1, site);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                returnList.add(new detailsObject(resultSet.getString("URL"), resultSet.getString("shortDesc"), resultSet.getString("longDesc"), resultSet.getTimestamp("CHECK_DATE").toLocalDateTime(), resultSet.getInt("RESPONSE_CODE")));
            }

            return returnList;
        } catch (SQLException ex) {
            Logger.getLogger(HSQL_Manager.class.getName()).log(Level.SEVERE, null, ex);
            return returnList;
        }
    }
    
    public static boolean clearStat(String site){
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM PUBLIC.STATISTICS WHERE URL = ?");
            statement.setString(1, site);

            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
}
