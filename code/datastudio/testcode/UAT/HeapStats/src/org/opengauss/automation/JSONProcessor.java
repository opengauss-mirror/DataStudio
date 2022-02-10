package org.opengauss.automation;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONProcessor
{

	public static boolean isJSONValid(String jsonData)
	{
		try
		{
			new JSONObject(jsonData);
		}
		catch (JSONException e)
		{
			try
			{
				new JSONArray(jsonData);
			}
			catch (JSONException e1)
			{
				return false;
			}
		}
		if (!SYNTAX.matcher(jsonData).matches()) { return false; }
		return true;
	}

	// The regular expression that every JSON number string must match.
	static Pattern SYNTAX = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

	public JSONProcessor(String jsonData) throws JSONException
	{

		if (jsonData == null) throw new NullPointerException();
		try
		{
			@SuppressWarnings("unused")
			JSONObject jsonObject = new JSONObject(jsonData);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Malformed JSON");
		}
		try
		{
			@SuppressWarnings("unused")
			JSONObject jsonObject = new JSONObject(jsonData);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Malformed JSON");
		}

	}

	public static void iterateJSON(JSONObject jsonObject)
	{
		try
		{
			for (@SuppressWarnings("rawtypes")
			Iterator iterator = jsonObject.keys(); iterator.hasNext();)
			{

				String jsonKey = (String) iterator.next();
				String JSONType = getType(jsonObject, jsonKey);

				if (JSONType.equals("ARRAY"))
				{
					System.out.println("ARRAY : " + jsonKey);
					JSONArray jsonArray = jsonObject.getJSONArray(jsonKey);
					for (int i = 0; i < jsonArray.length(); i++)
					{
						System.out.println(jsonArray.get(i));
						// JSONObject innerObject = (JSONObject)
						// jsonArray.get(i);
						// iterateJSON(innerObject);
					}
				}
				else if (JSONType.equals("OBJECT"))
				{
					System.out.println("OBJECT : " + jsonKey);
					JSONObject innerObject = jsonObject.getJSONObject(jsonKey);
					iterateJSON(innerObject);
				}
				else
				{
					System.out.println("Key:  " + jsonKey + "\t\t\t\t\t\t\t : Value : " + jsonObject.get(jsonKey));
				}
			}

		}
		catch (Exception e)
		{
			// System.out.println(""+e);
			throw new IllegalArgumentException("Malformed JSON");
		}
	}

	public static String getType(JSONObject jsonObject, String key) throws JSONException
	{
		try
		{
			if (jsonObject.get(key) instanceof JSONObject) return "OBJECT";
			else if (jsonObject.get(key) instanceof JSONArray) return "ARRAY";
		}
		catch (Exception e)
		{
			System.out.println("" + e);
		}
		return "DATA";
	}

	public static String getJSonType(JSONObject jsonObject, String key)
	{
		try
		{
			if (jsonObject != null)
			{
				// System.out.println(key + " " +jsonObject );
				if (jsonObject.get(key) instanceof JSONObject) return "OBJECT";
				else if (jsonObject.get(key) instanceof JSONArray) return "ARRAY";
			}

		}
		catch (Exception e)
		{
			System.out.println("" + e);
		}
		return "DATA";
	}

	public static String getValueOld(String jsonData, String jsonPath)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(jsonData);
			JSONObject procObject = jsonObject;
			JSONArray procArray = null;
			String JSONType = "";
			String jsonContext = "";
			String indexValue = "";
			String keys[] = jsonPath.split("\\.");

			for (int i = 0; i < keys.length; i++)
			{
				String keyName = "";
				int index = 0;

				// Identify Index Value
				keyName = keys[i];
				if (keyName.indexOf("[") != -1 && keyName.indexOf("]") != -1)
				{
					indexValue = keyName.substring(keyName.indexOf("[") + 1, keyName.indexOf("]")).trim();
					index = Integer.parseInt(indexValue);
					keyName = keyName.substring(0, keyName.indexOf("[")).trim();
				}

				// Identify JSONType Need to modify this logic
				JSONType = getJSonType(procObject, keyName);

				if (JSONType.equals("ARRAY"))
				{
					procArray = procObject.getJSONArray(keyName);
					procObject = null;
					jsonContext = "ARRAY";

				}

				else if (JSONType.equals("OBJECT"))
				{
					procObject = procObject.getJSONObject(keyName);
					jsonContext = "OBJECT";

				}

				else if (JSONType.equals("DATA"))
				{
					if (jsonContext.equals("OBJECT"))
					{
						jsonContext = "DATA";

					}
					else if (jsonContext.equals("ARRAY"))
					{
						jsonContext = "DATA";
						procObject = (JSONObject) procArray.get(index);
					}

					String JSONInnerType = getJSonType(procObject, keyName);

					if (JSONInnerType.equals("DATA"))
					{
						String dataType = getDataType(procObject, keyName);

						// System.out.println(dataType);

						if (dataType.equals("java.lang.String")) { return (String) procObject.get(keyName); }
						if (dataType.equals("java.lang.Double"))
						{
							Double doubleValue = (Double) procObject.get(keyName);
							return doubleValue.toString();
						}

						if (dataType.equals("java.lang.Integer"))
						{
							Integer intValue = (Integer) procObject.get(keyName);
							return intValue.toString();
						}
					}
					else if (JSONInnerType.equals("OBJECT"))
					{
						procObject = procObject.getJSONObject(keyName);
						jsonContext = "OBJECT";

					}
					else if (JSONInnerType.equals("ARRAY"))
					{
						procArray = procObject.getJSONArray(keyName);
						jsonContext = "ARRAY";
					}
					JSONInnerType = "";

				}
				JSONType = "";
			}

		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		return "";

	}

	public static String getValue(java.io.File jsonFile, String jsonPath)
	{
		try
		{
			String jsonData = readFile(jsonFile);
			return getValue(jsonData, jsonPath);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		return "";

	}

	public static String readFile(File jsonFile) throws IOException
	{
		StringBuilder fileContents = new StringBuilder((int) jsonFile.length());
		Scanner scanner = new Scanner(jsonFile);
		String lineSeparator = System.getProperty("line.separator");
		try
		{
			while (scanner.hasNextLine())
			{
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		}
		finally
		{
			scanner.close();
		}
	}

	public static String readFile(String jsonPath)
	{
		try
		{
			java.io.File jsonFile = new java.io.File(jsonPath);
			return readFile(jsonFile);
		}
		catch (Exception e)
		{
			System.out.println(e);
			//new APIException("Unable to read File");
			return "";
		}
	}

	private static String getDataType(JSONObject object, String key) throws JSONException
	{
		return object.get(key).getClass().getName();
	}

	public static boolean getBoolean(String jsonData, String toFind)
	{
		return ((Boolean) getValueasObject(jsonData, toFind)).booleanValue();
	}

	public static int getInt(String jsonData, String toFind)
	{
		return ((Number) getValueasObject(jsonData, toFind)).intValue();
	}

	public static long getLong(String jsonData, String toFind)
	{
		return ((Number) getValueasObject(jsonData, toFind)).longValue();
	}

	public static float getFloat(String jsonData, String toFind)
	{
		return ((Number) getValueasObject(jsonData, toFind)).floatValue();
	}

	public static double getDouble(String jsonData, String toFind)
	{
		return ((Number) getValueasObject(jsonData, toFind)).doubleValue();
	}

	public static String getString(String jsonData, String toFind)
	{
		String result = (String) getValueasObject(jsonData, toFind);
		if (result == null) throw new NullPointerException();
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<Object> getList(String jsonData, String toFind)
	{
		List<Object> result = (List<Object>) getValueasObject(jsonData, toFind);
		if (result == null) throw new NullPointerException();
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMap(String jsonData, String toFind)
	{
		Map<String, Object> result = (Map<String, Object>) getValueasObject(jsonData, toFind);
		if (result == null) throw new NullPointerException();
		return result;
	}

	public static int arrayCount(String jsonData)
	{
		try
		{
			JSONArray jsonArray = new JSONArray(jsonData);
			return jsonArray.length();
		}
		catch(Exception e)
		{
			System.out.println("Malformed JSON");
			return 0;
		}
	}
	// --------------------------
	// New Code
	// --------------------------

	public static Map<String, Object> jsonToMap(String json) throws JSONException
	{
		Map<String, Object> retMap = new HashMap<String, Object>();

		if (json != null && !json.isEmpty())
		{
			JSONObject jo = new JSONObject(json);
			retMap = JSONProcessor.jsonToMap(jo);
		}
		System.out.println("Map is " + retMap.toString());
		return retMap;
	}

	public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException
	{
		Map<String, Object> retMap = new HashMap<String, Object>();

		if (json != JSONObject.NULL)
		{
			retMap = toMap(json);
		}
		// System.out.println("Map is " + retMap.toString());
		return retMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listToMap(List thisList, Map toMap, String prefixKey)
	{

		for (int i = 0; i < thisList.size(); i++)
		{
			Object listvalue = ((ArrayList<Object>) thisList).get(i);
			String key = prefixKey + "[" + i + "]";
			toMap.put(key, listvalue);
		}
		return toMap;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> mapToMap(Map<String, Object> thisMap, Map toMap, String prefixKey)
	{

		for (Map.Entry<String, Object> entry : thisMap.entrySet())
		{
			String key = prefixKey + "." + entry.getKey();
			Object val = entry.getValue();
			toMap.put(key, val);
		}
		return toMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> toMap(JSONObject object) throws JSONException
	{
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext())
		{
			String key = keysItr.next();
			Object value = object.get(key);
			String prefixKey = key;
			map.put(key, value);
			if (key != null && !key.isEmpty())
			{

				if (value instanceof JSONArray)
				{
					value = toList((JSONArray) value);
					for (int i = 0; i < ((List) value).size(); i++)
					{
						Object listvalue = ((ArrayList<Object>) value).get(i);
						prefixKey = key + "[" + i + "]";
						if (listvalue instanceof List)
						{
							map = listToMap((List) listvalue, map, prefixKey);
						}
						else if (listvalue instanceof Map)
						{
							map = mapToMap((Map) listvalue, map, prefixKey);
						}
						else
						{
							map.put(prefixKey, listvalue);
						}
					}
				}
				else if (value instanceof JSONObject)
				{
					value = toMap((JSONObject) value);
					if (value instanceof List)
					{
						map = listToMap((List) value, map, prefixKey);
					}
					else if (value instanceof Map)
					{
						map = mapToMap((Map) value, map, prefixKey);
					}
					else
					{
						map.put(prefixKey, value);
					}
				}
			}
			else
			{
				throw new IllegalArgumentException("Malformed JSON");
			}
		}

		return map;

	}

	public static List<Object> toList(JSONArray array) throws JSONException
	{
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++)
		{
			Object value = array.get(i);
			if (value instanceof JSONArray)
			{
				value = toList((JSONArray) value);
			}
			else if (value instanceof JSONObject)
			{
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

	public static void displayJSONMAP(Map<String, Object> map) throws Exception
	{
		Set<String> keyset = map.keySet(); // HM$keyset
		if (!keyset.isEmpty())
		{
			Iterator<String> keys = keyset.iterator(); // HM$keysIterator
			System.out.println("--------------------------------------------------------------------------------------");
			System.out.format("%-50s%-30s%-60s", "KEY", "TYPE", "VALUE");
			System.out.println("");
			System.out.println("--------------------------------------------------------------------------------------");
			while (keys.hasNext())
			{
				String key = keys.next();
				Object value = map.get(key);
				System.out.format("%-50s%-30s%-60s", key, value);
				System.out.println("");
			}
		}

	}

	public static Object getValueasObject(String jsonData, String toFind)
	{
		try
		{
			JSONObject jo = new JSONObject(jsonData);
			Map<String, Object> map = JSONProcessor.jsonToMap(jo);
			if (map.containsKey(toFind)) { return map.get(toFind); }
		}
		catch (JSONException e)
		{
			throw new IllegalArgumentException("Malformed JSON");
		}
		return null;
	}

	public static String getValue(String jsonData, String toFind)
	{
		String value = null;

		Object obj = getValueasObject(jsonData, toFind);
		if (obj != null)
		{
			value = obj.toString();
		}
		return value;

	}
}
