import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ParseException {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        String xmlFileName = "data.xml";

//        List<Employee> csvList = parseCSV(columnMapping, csvFileName);
//        csvList.forEach(System.out::println);
//        String csvJson = listToJson(csvList);
//        writeString(csvJson);

//        List<Employee> xmlList = parseXML(xmlFileName);
//        xmlList.forEach(System.out::println);
//        String xmlJson = listToJson(xmlList);
//        writeString(xmlJson);

        String json = readString("data.json");
        List<Employee> list = jsonToList(json);
        list.forEach(System.out::println);


    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json) {
        try (FileWriter writer = new FileWriter("data.json")) {
            writer.write(json);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> staff = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (Node.ELEMENT_NODE == node.getNodeType()) {

                Element element = (Element) node;
                String s = element.getTextContent();
                s = s.replaceAll("\\s+", " ");
                String[] v = s.split(" ");

                staff.add(new Employee(
                        Long.valueOf(v[1]),
                        v[2],
                        v[3],
                        v[4],
                        Integer.parseInt(v[5])));
            }
        }
        return staff;
    }

    public static String readString(String fileName) {
        String text = "";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int c;
            while ((c = br.read()) != -1) {
                //System.out.print((char)c);
                text += (char) c;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return text;
    }

    public static List<Employee> jsonToList(String json) throws ParseException {
        List<Employee> staff = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(json);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        for (Object employee : jsonArray) {
            String jsonString = gson.toJson(employee);

            Employee emp = gson.fromJson(jsonString, Employee.class);
            staff.add(emp);
        }
        return staff;
    }
}






