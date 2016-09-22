package test;

import java.util.HashMap;
import java.util.regex.*;

public class ParserSeat {
    private static HashMap<String, String> misprints = new HashMap<String, String>() {{
        put("С", "C");
        put("Е", "E");
        put("Т", "T");
        put("Н", "H");
        put("У", "Y");
        put("О", "O");
        put("Р", "P");
        put("Х", "X");
        put("А", "A");
        put("В", "B");
        put("К", "K");
        put("М", "M");
    }};

    public ParserSeat(){

    }

    public static ResultParser parser(String seatName, long id){
        ResultParser result = new ResultParser();
        result.setId(id);
        Matcher m = Pattern.compile("(?:(?<sector>.*?) (?<sectorName>(?:\\p{Lu}|\\d).*?)|(?<sectorWithoutName>.*)) (?<row>Ряд) (?<rowName>.*) (?<seat>Место) (?<seatName>.*)").matcher(seatName);
        if (m.find()) {
            if (m.group("sectorWithoutName") != null) {
                result.setSector(m.group("sectorWithoutName"));
                result.setSectorName("");
            } else {
                result.setSector(m.group("sector"));
                result.setSectorName(m.group("sectorName"));
            }
            result.setRow(m.group("row"));
            result.setRowName(m.group("rowName"));
            result.setSeat(m.group("seat"));
            result.setSeatName(m.group("seatName"));
        }
        if (result.getSector() == null || result.getRow() == null || result.getRowName() == null || result.getSeat() == null || result.getSeatName() == null) {
            return  null;
        }
        for (String cyrillicCharacter : misprints.keySet()) {
            result.setSectorName(result.getSectorName().replace(cyrillicCharacter, misprints.get(cyrillicCharacter)));
        }
        return result;
    }

}
