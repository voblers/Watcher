/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import Data.Const;
import Notifications.TrayNotification;
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
        coldInit();
        connection.createStatement().execute("SET DATABASE SQL AVG SCALE 3");
    }

    private static void coldInit() {
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS sites(\n"
                    + "	id INTEGER IDENTITY PRIMARY KEY,\n"
                    + "	site varchar(1024)\n"
                    + "	);");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS statistics(\n"
                    + "  id INTEGER IDENTITY PRIMARY KEY,\n"
                    + "  url VARCHAR(1024),\n"
                    + "  check_date timestamp default sysdate,\n"
                    + "  status INTEGER,\n"
                    + "  response_code integer) ;");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS responsecodes(\n"
                    + "response_code integer primary key,\n"
                    + "shortDesc varchar(1024),\n"
                    + "longDesc LONGVARCHAR );");

            if (getRowCount("responsecodes") == 0) {
                connection.createStatement().execute("insert into responsecodes values(100, 'Continue', 'The server has received the request headers and the client should proceed to send the request body (in the case of a request for which a body needs to be sent; for example, a POST request). Sending a large request body to a server after a request has been rejected for inappropriate headers would be inefficient. To have a server check the request''s headers, a client must send Expect: 100-continue as a header in its initial request and receive a 100 Continue status code in response before sending the body. The response 417 Expectation Failed indicates the request should not be continued.');\n"
                        + "insert into responsecodes values(101, 'Switching Protocols', 'The requester has asked the server to switch protocols and the server has agreed to do so.');\n"
                        + "insert into responsecodes values(102, 'Processing', 'A WebDAV request may contain many sub-requests involving file operations, requiring a long time to complete the request. This code indicates that the server has received and is processing the request, but no response is available yet.[6] This prevents the client from timing out and assuming the request was lost.');\n"
                        + "insert into responsecodes values(200, 'OK', 'Standard response for successful HTTP requests. The actual response will depend on the request method used. In a GET request, the response will contain an entity corresponding to the requested resource. In a POST request, the response will contain an entity describing or containing the result of the action.');\n"
                        + "insert into responsecodes values(201, 'Created', 'The request has been fulfilled, resulting in the creation of a new resource.');\n"
                        + "insert into responsecodes values(202, 'Accepted', 'The request has been accepted for processing, but the processing has not been completed. The request might or might not be eventually acted upon, and may be disallowed when processing occurs.');\n"
                        + "insert into responsecodes values(203, 'Non-Authoritative Information', 'The server is a transforming proxy (e.g. a Web accelerator) that received a 200 OK from its origin, but is returning a modified version of the origin''s response.');\n"
                        + "insert into responsecodes values(204, 'No Content', 'The server successfully processed the request and is not returning any content.');\n"
                        + "insert into responsecodes values(205, 'Reset Content', 'The server successfully processed the request and is not returning any content.');\n"
                        + "insert into responsecodes values(206, 'Partial Content', 'The server is delivering only part of the resource (byte serving) due to a range header sent by the client. The range header is used by HTTP clients to enable resuming of interrupted downloads, or split a download into multiple simultaneous streams.');\n"
                        + "insert into responsecodes values(207, 'Multi-Status', 'The message body that follows is an XML message and can contain a number of separate response codes, depending on how many sub-requests were made.');\n"
                        + "insert into responsecodes values(208, 'Already Reported', 'The members of a DAV binding have already been enumerated in a previous reply to this request, and are not being included again.');\n"
                        + "insert into responsecodes values(226, 'IM Used', 'The server has fulfilled a request for the resource, and the response is a representation of the result of one or more instance-manipulations applied to the current instance.');\n"
                        + "insert into responsecodes values(300, 'Multiple Choices', 'Indicates multiple options for the resource from which the client may choose (via agent-driven content negotiation). For example, this code could be used to present multiple video format options, to list files with different extensions, or to suggest word sense disambiguation.');\n"
                        + "insert into responsecodes values(301, 'Moved Permanently', 'This and all future requests should be directed to the given URI.');\n"
                        + "insert into responsecodes values(302, 'Found', 'This is an example of industry practice contradicting the standard. The HTTP/1.0 specification (RFC 1945) required the client to perform a temporary redirect (the original describing phrase was \"Moved Temporarily\"),[21] but popular browsers implemented 302 with the functionality of a 303 See Other. Therefore, HTTP/1.1 added status codes 303 and 307 to distinguish between the two behaviours.[22] However, some Web applications and frameworks use the 302 status code as if it were the 303.');\n"
                        + "insert into responsecodes values(303, 'See Other', 'The response to the request can be found under another URI using a GET method. When received in response to a POST (or PUT/DELETE), the client should presume that the server has received the data and should issue a redirect with a separate GET message.');\n"
                        + "insert into responsecodes values(304, 'Not Modified', 'Indicates that the resource has not been modified since the version specified by the request headers If-Modified-Since or If-None-Match. In such case, there is no need to retransmit the resource since the client still has a previously-downloaded copy.');\n"
                        + "insert into responsecodes values(305, 'Use Proxy', 'The requested resource is available only through a proxy, the address for which is provided in the response. Many HTTP clients (such as Mozilla[26] and Internet Explorer) do not correctly handle responses with this status code, primarily for security reasons.');\n"
                        + "insert into responsecodes values(307, 'Temporary Redirect', 'In this case, the request should be repeated with another URI; however, future requests should still use the original URI. In contrast to how 302 was historically implemented, the request method is not allowed to be changed when reissuing the original request. For example, a POST request should be repeated using another POST request.');\n"
                        + "insert into responsecodes values(308, 'Permanent Redirect', 'The request and all future requests should be repeated using another URI. 307 and 308 parallel the behaviors of 302 and 301, but do not allow the HTTP method to change. So, for example, submitting a form to a permanently redirected resource may continue smoothly.');\n"
                        + "insert into responsecodes values(400, 'Bad Request', 'The server cannot or will not process the request due to an apparent client error (e.g., malformed request syntax, too large size, invalid request message framing, or deceptive request routing).');\n"
                        + "insert into responsecodes values(401, 'Unauthorized', 'Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been provided. The response must include a WWW-Authenticate header field containing a challenge applicable to the requested resource. See Basic access authentication and Digest access authentication.[33] 401 semantically means \"unauthenticated\",[34] i.e. the user does not have the necessary credentials. ');\n"
                        + "insert into responsecodes values(403, 'Forbidden', 'The request was a valid request, but the server is refusing to respond to it. The user might be logged in but does not have the necessary permissions for the resource.');\n"
                        + "insert into responsecodes values(404, 'Not Found', 'The requested resource could not be found but may be available in the future. Subsequent requests by the client are permissible.');\n"
                        + "insert into responsecodes values(405, 'Method Not Allowed', 'A request method is not supported for the requested resource; for example, a GET request on a form which requires data to be presented via POST, or a PUT request on a read-only resource.');\n"
                        + "insert into responsecodes values(406, 'Not Acceptable', 'The requested resource is capable of generating only content not acceptable according to the Accept headers sent in the request.[37]See Content negotiation.');\n"
                        + "insert into responsecodes values(407, 'Proxy Authentication Required', 'The client must first authenticate itself with the proxy.');\n"
                        + "insert into responsecodes values(408, 'Request Timeout', 'The server timed out waiting for the request. According to HTTP specifications: \"The client did not produce a request within the time that the server was prepared to wait. The client MAY repeat the request without modifications at any later time.\"');\n"
                        + "insert into responsecodes values(409, 'Conflict', 'Indicates that the request could not be processed because of conflict in the request, such as an edit conflict between multiple simultaneous updates.');\n"
                        + "insert into responsecodes values(410, 'Gone', 'Indicates that the resource requested is no longer available and will not be available again. This should be used when a resource has been intentionally removed and the resource should be purged. Upon receiving a 410 status code, the client should not request the resource in the future. Clients such as search engines should remove the resource from their indices.');\n"
                        + "insert into responsecodes values(411, 'Length Required', 'The request did not specify the length of its content, which is required by the requested resource.');\n"
                        + "insert into responsecodes values(412, 'Precondition Failed', 'The server does not meet one of the preconditions that the requester put on the request.');\n"
                        + "insert into responsecodes values(413, 'Payload Too Large', 'The request is larger than the server is willing or able to process. Previously called \"Request Entity Too Large\".');\n"
                        + "insert into responsecodes values(414, 'URI Too Long', 'The URI provided was too long for the server to process. Often the result of too much data being encoded as a query-string of a GET request, in which case it should be converted to a POST request.');\n"
                        + "insert into responsecodes values(415, 'Unsupported Media Type', 'The request entity has a media type which the server or resource does not support. For example, the client uploads an image as image/svg+xml, but the server requires that images use a different format.');\n"
                        + "insert into responsecodes values(416, 'Range Not Satisfiable', 'The client has asked for a portion of the file (byte serving), but the server cannot supply that portion. For example, if the client asked for a part of the file that lies beyond the end of the file.');\n"
                        + "insert into responsecodes values(417, 'Expectation Failed', 'The server cannot meet the requirements of the Expect request-header field.');\n"
                        + "insert into responsecodes values(421, 'Misdirected Request', 'The request was directed at a server that is not able to produce a response (for example because a connection reuse).');\n"
                        + "insert into responsecodes values(422, 'Unprocessable Entity', 'The request was well-formed but was unable to be followed due to semantic errors.');\n"
                        + "insert into responsecodes values(423, 'Locked', 'The resource that is being accessed is locked.');\n"
                        + "insert into responsecodes values(424, 'Failed Dependency', 'The request failed due to failure of a previous request (e.g., a PROPPATCH).');\n"
                        + "insert into responsecodes values(426, 'Upgrade Required', 'The client should switch to a different protocol such as TLS/1.0, given in the Upgrade header field.');\n"
                        + "insert into responsecodes values(428, 'Precondition Required', 'The origin server requires the request to be conditional. Intended to prevent \"the ''lost update'' problem, where a client GETs a resource''s state, modifies it, and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict.\"');\n"
                        + "insert into responsecodes values(429, 'Too Many Requests', 'The user has sent too many requests in a given amount of time. Intended for use with rate-limiting schemes.');\n"
                        + "insert into responsecodes values(431, 'Request Header Fields Too Large', 'The server is unwilling to process the request because either an individual header field, or all the header fields collectively, are too large.');\n"
                        + "insert into responsecodes values(451, 'Unavailable For Legal Reasons', 'A server operator has received a legal demand to deny access to a resource or to a set of resources that includes the requested resource.');\n"
                        + "insert into responsecodes values(500, 'Internal Server Error', 'A generic error message, given when an unexpected condition was encountered and no more specific message is suitable.');\n"
                        + "insert into responsecodes values(501, 'Not Implemented', 'The server either does not recognize the request method, or it lacks the ability to fulfill the request. Usually this implies future availability (e.g., a new feature of a web-service API).');\n"
                        + "insert into responsecodes values(502, 'Bad Gateway', 'The server was acting as a gateway or proxy and received an invalid response from the upstream server.');\n"
                        + "insert into responsecodes values(503, 'Service Unavailable', 'The server is currently unavailable (because it is overloaded or down for maintenance). Generally, this is a temporary state.');\n"
                        + "insert into responsecodes values(504, 'Gateway Timeout', 'The server was acting as a gateway or proxy and did not receive a timely response from the upstream server.');\n"
                        + "insert into responsecodes values(505, 'HTTP Version Not Supported', 'The server does not support the HTTP protocol version used in the request.');\n"
                        + "insert into responsecodes values(506, 'Variant Also Negotiates', 'Transparent content negotiation for the request results in a circular reference.');\n"
                        + "insert into responsecodes values(507, 'Insufficient Storage', 'The server is unable to store the representation needed to complete the request.');\n"
                        + "insert into responsecodes values(508, 'Loop Detected', 'The server detected an infinite loop while processing the request');\n"
                        + "insert into responsecodes values(510, 'Not Extended', 'Further extensions to the request are required for the server to fulfill it.');\n"
                        + "insert into responsecodes values(511, 'Network Authentication Required', 'The client needs to authenticate to gain network access. Intended for use by intercepting proxies used to control access to the network (e.g., \"captive portals\" used to require agreement to Terms of Service before granting full Internet access via a Wi-Fi hotspot).');");
            }

        } catch (SQLException ex) {
            Logger.getLogger(HSQL_Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int getRowCount(String tableName) {
        try {
            ResultSet resultSet;
            Statement statement;

            statement = connection.createStatement();
            resultSet = statement.executeQuery("select count(*) AS total from " + tableName);
            resultSet.next();
            
            return resultSet.getInt("total");
        } catch (SQLException ex) {
            Logger.getLogger(HSQL_Manager.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
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
            new TrayNotification(ex.getCause().toString(), Const.notificationError, 5).run();
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
            new TrayNotification(ex.getCause().toString(), Const.notificationError, 5).run();
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
            new TrayNotification(ex.getCause().toString(), Const.notificationError, 5).run();
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
            new TrayNotification(ex.getCause().toString(), Const.notificationError, 5).run();
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
            new TrayNotification(ex.getCause().toString(), Const.notificationError, 5).run();
            return returnList;
        }
    }

    public static ArrayList<detailsObject> getDetails(String site) {
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
            new TrayNotification(ex.getCause().toString(), Const.notificationError, 5).run();
            return returnList;
        }
    }

    public static boolean clearStat(String site) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM PUBLIC.STATISTICS WHERE URL = ?");
            statement.setString(1, site);

            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            new TrayNotification(ex.getCause().toString(), Const.notificationError, 5).run();
            return false;
        }
    }
}
