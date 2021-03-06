import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class thumbtack {
	static HashMap<String, String> updateMap = new HashMap<>();
	static HashMap<String, Integer> countMap = new HashMap<>();
	static StringBuilder reverseCommand = new StringBuilder();
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		while(scan.hasNextLine() ) {
			String cur = scan.nextLine();
			if (cur.equals("END")) {
				System.out.println("END");
				scan.close();
				System.exit(0);
			}
			parseStr(cur);
		}
	}
	
	private static void parseStr(String str) {
		String[] strs = str.split(" ");
		System.out.println(str);
		switch(strs[0]) {
		case "SET":
			if (strs.length < 3) System.out.println("wrong SET info");
			else {
				set(strs[1], strs[2]);
				reverseCommand.append(str + ",");
			}
			break;
		case "GET":
			if (strs.length < 2) System.out.println("wrong GET info");
			else get(strs[1]);
			break;
		case "UNSET":
			if (strs.length < 2) System.out.println("wrong UNSET info");
			else {
				set(strs[1], "NULL");
				reverseCommand.append(str + ",");
			}
			break;
		case "COMMIT":
			if (reverseCommand.length() == 0) System.out.println("> NO TRANSACTION");
			else reverseCommand.setLength(0);
			break;
		case "NUMEQUALTO":
			if (strs.length < 2) System.out.println("wrong NUMEQUALTO info");
			else numEqualTo(strs[1]);
			break;
		case "BEGIN":
			reverseCommand.append("|");
			break;
		case "ROLLBACK":
			if (reverseCommand.length() == 0) System.out.println("> NO TRANSACTION");
			else {
				rollBack();
			}
			break;
		default:
			break;
		}
	}
	
	private static void set(String key, String val) {
		if (!updateMap.containsKey(key)) {
			updateMap.put(key, val);
			countMap.put(val, countMap.getOrDefault(val, 0)+1);
		}
		else {
			if (updateMap.containsKey(key)) {
				String oldVal = updateMap.get(key);
				countMap.put(oldVal, countMap.get(oldVal)-1);
			}
			updateMap.put(key, val);
			countMap.put(val, countMap.getOrDefault(val, 0)+1);
		}
	}
	
	private static void get(String key) {
		System.out.println("> " + updateMap.get(key));
	}
	
	private static void numEqualTo(String key) {
		System.out.println("> " + countMap.getOrDefault(key, 0));
	}
	
	private static void rollBack() {
		int index = reverseCommand.toString().lastIndexOf("|");
		reverseCommand.delete(index, reverseCommand.length());
		String[] commands = reverseCommand.toString().split("\\|");
		for (String command: commands) {
			if ((command.length() == 0 && commands.length == 1) || commands == null) {
				for (String key: updateMap.keySet()) {
					countMap.put(updateMap.get(key), countMap.get(updateMap.get(key))-1);
					set(key, "NULL");	
				}
				break; // corner case: BEGIN is the first command
			}
			String[] actions = command.split(",");
			for (String act: actions) {
				String[] each = act.split(" ");
				switch (each[0]) {
				case "SET":
					set(each[1], each[2]);
					break;
				case "UNSET":
					set(each[1], "NULL");
				default:
					break;
				}
			}
		}
	}
}
