/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package XML;

import ErrorHandling.errDialog;
import dao.Site;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author BB3605
 */
public class XMLUtils {
    private static DocumentBuilderFactory dbFactory;
    private static DocumentBuilder dBuilder;
    private static Document doc;

    public XMLUtils() {
    }
    private void initXML(String file){
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            InputStream istream = getClass().getResourceAsStream(file);
            if (istream == null)
                throw new IOException();
            doc = dBuilder.parse(istream); 
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            new errDialog().showError(sw.toString());
        }
    }
    
    private String getValue(Element inp, String tag){
        return  inp.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
    }
    
    public ArrayList<Site> getSites(){
        initXML(Data.Const.siteXMLName);
        
        ArrayList<Site> sites = new ArrayList<>();
        
        NodeList nList = doc.getElementsByTagName("site");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Element eElement = (Element)nList.item(temp);
            sites.add(new Site(getValue(eElement, "adress")));
        }
        
        return sites;
    }
}