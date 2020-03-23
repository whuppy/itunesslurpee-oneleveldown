package fontana;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A sample iTunes Library file SAX parser.
 */
public class TunesReader extends DefaultHandler {
    // private static final String LIBRARY_FILE_PATH = "C:\\Users\\F210869\\Downloads\\file.xml";
    private static final String LIBRARY_FILE_PATH = "file:///C:/Users/F210869/Downloads/file.xml";
    // private static final String LIBRARY_FILE_PATH = "http://ogden.frdmrt.org/file.xml";

    List<SongDict> myTracks;
    private String tempVal;
    private SongDict tempTrack;
    boolean foundTracks = false; // are we inside the Tracks dict?
    private String previousTag;
    private String previousTagVal;
    public TunesReader() {
        myTracks = new ArrayList<SongDict>();
		// Here would be the place to open up a connection
		// to (or create) a database. 
    }
    public void runExample() {
        parseDocument();
        printData();
    }
    private void parseDocument() {
        //get a factory
    	// System.out.println("Getting factory . . .");
        SAXParserFactory spf = SAXParserFactory.newInstance();
        
        try {
            //get a new instance of parser
        	// System.out.println("Getting parser instance . . .");
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            // System.out.println(". . . done. Parsing " + LIBRARY_FILE_PATH + " . . .");
            sp.parse(LIBRARY_FILE_PATH, this);
            // System.out.println(". . . done.");
        } catch(SAXException se) {
        	System.out.println("SAX Exception");
            se.printStackTrace();
        } catch(ParserConfigurationException pce) {
        	System.out.println("Parser Configuration Exception");
            pce.printStackTrace();
        } catch (IOException ie) {
        	System.out.println("IO Exception");
            ie.printStackTrace();
        }
    }
    private void printData(){
        System.out.println("Number of Tracks '" + myTracks.size() + "'.");
        Iterator<SongDict> it = myTracks.iterator();
        while(it.hasNext()) {
            SongDict song = it.next();
            song.dump();
            System.out.println();
        }
    }
    //Event Handlers
    /**
     * Determine if we've entered either the Tracks dict or a new Song's dict.
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (foundTracks) { // we're dealing with a Track's key or dict
            if ("key".equals(previousTag) && "dict".equalsIgnoreCase(qName)) {
                // We've started a Track's dict, 
            	// so wrap things up with the current tempTrack . . .
            	if (tempTrack != null) {
            		// tempTrack.dump();
            		// System.out.println();
            	}
            	// . . . and create and start working on a new SongDict object.
            	tempTrack = new SongDict(Integer.parseInt(previousTagVal));
                // In the example code, all of the XML file gets read 
                // into the myTracks array . . . 
                myTracks.add(tempTrack);
                // . . . but I envision that the move at this
                // point is to write all of the tempTrack SongDict's info 
                // out to a record in a database and then have the
                // TunesReader destroy the current tempTrack SongDict
                // object and create a new one. 
            }
        } else {
            if ("key".equals(previousTag) && "Tracks".equalsIgnoreCase(previousTagVal) && "dict".equalsIgnoreCase(qName)) {
                foundTracks = true; // We are now inside the Tracks dict.
            }
        }
    }
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch,start,length);
    }
    /**
     * This is where the logic peculiar to the iTunes XML file mostly resides.
     * The elements in the iTunes XML file are mostly dicts, which consist of
     * alternating <key> and value elements. 
     * Value elements can be integer, string, date, true, or false. 
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (foundTracks) {
        	// At the end of an <integer>, <string>, or <date> element,
        	// make an entry into the current SongDict's dictionary:
        	if (qName.equals("string") || qName.equals("integer") || qName.equals("date")){
        		tempTrack.addDict(previousTagVal, tempVal);
        	}
        	// <true/> and <false/> value elements:
        	if (qName.equals("true") || qName.equals("false")){
        		tempTrack.addDict(previousTagVal, qName);
        	}
        	// Mark when we come to the end of the "Tracks" dict;
            // the Playlists key comes after the Tracks dict in the iTunes XML file.
            if (qName.equals("key") && tempVal.equalsIgnoreCase("Playlists")) {
                foundTracks = false;
            }
        }
        // Keep track of the previous tag so we can track the context when 
        // we're at the second (value) tag in a key-value pair.
        previousTagVal = tempVal;
        previousTag = qName;
    }
    public static void main(String[] args) {
        TunesReader spe = new TunesReader();
        spe.runExample();
    }
}
