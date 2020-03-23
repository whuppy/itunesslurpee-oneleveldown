package fontana;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
  * A simple representation of a song in the iTunes library,
  * now in dictionary form.
  */
public class SongDict {
	private int trackID;
	private HashMap<String, String> sdict;

	public SongDict() {
		sdict = new HashMap<String, String>();
	}
	public SongDict(int uid){
		trackID = uid;
		sdict = new HashMap<String, String>();
	}
	public void addDict(String dictKey, String dictVal){
		sdict.put(dictKey, dictVal);
	}
	public String getDict(String dictKey){
		return sdict.get(dictKey);
	}
	public void dump() {
		String key;
		String val;

		System.out.println("Song Record No. " + trackID);
		Set<String> s =  sdict.keySet();
		Iterator<String> it = s.iterator();
		while (it.hasNext()){
			key = it.next();
			val = (String) sdict.get(key);
			System.out.println(key + " : " + val);
		}
	}
	public static void main(String[] args) {
		SongDict mysong = new SongDict(666);
		mysong.addDict("Title", "Song About Otters");
		mysong.addDict("Artist", "Otto Otterbard");
		mysong.addDict("Album", "Happy Otter Brook");
		mysong.addDict("Genre", "Otter Rock");
		
		String t = mysong.getDict("Title");
		System.out.println(t);
		System.out.println();
		
		mysong.dump();
	}
}
