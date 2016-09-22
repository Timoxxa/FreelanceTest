package test;

import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Timofey Boldyrev on 22.09.2016.
 */
@Component
public class SeatInfoParser {

    public static final String REGEXP = "(?:(?<sector>.*?) " +
            "(?<sectorName>(?:\\p{Lu}|\\d).*?)|(?<sectorWithoutName>.*)) " +
            "(?<row>Ряд) (?<rowName>.*) (?<seat>Место) (?<seatName>.*)";
    private static final String EMPTY = "";
    private static final String SECTOR = "sector";
    private static final String SECTOR_NAME = "sectorName";
    private static final String SECTOR_WITHOUT_NAME = "sectorWithoutName";
    private static final String ROW = "row";
    private static final String ROW_NAME = "rowName";
    private static final String SEAT = "seat";
    private static final String SEAT_NAME = "seatName";
    private static final Map<Character,Character> replacedChars = new HashMap<Character,Character>() {{
        put('С', 'C');
        put('Е', 'E');
        put('Т', 'T');
        put('Н', 'H');
        put('У', 'Y');
        put('О', 'O');
        put('Р', 'P');
        put('Х', 'X');
        put('А', 'A');
        put('В', 'B');
        put('К', 'K');
        put('М', 'M');
    }};

    private SeatInfo parse(String seatInfoExpression, long id) {
        try {
            Matcher matcher = Pattern.compile(REGEXP).matcher(seatInfoExpression);
            if(!matcher.find()) return null;
            Map.Entry<String,String> sectorAndSectorName = getSectorAndSectorName(matcher);
            if (sectorAndSectorName==null) return null;
            String row = matcher.group(ROW);
            if (row==null) return null;
            String rowName = matcher.group(ROW_NAME);
            if (rowName==null) return null;
            String seat = matcher.group(SEAT);
            if (seat==null) return null;
            String seatName = matcher.group(SEAT_NAME);
            if (seatName==null) return null;
            SeatInfo seatInfo = new SeatInfo(id);
            seatInfo.setSector(sectorAndSectorName.getKey());
            seatInfo.setSectorName(replaceCyrillicChars(sectorAndSectorName.getValue()));
            seatInfo.setRow(row);
            seatInfo.setRowName(rowName);
            seatInfo.setSeat(seat);
            seatInfo.setSeatName(seatName);
            return seatInfo;
        } catch (Exception e) {
            throw new RuntimeException("Error when parsing seat info expression. " +
                    "Seat info expression: [" + seatInfoExpression + "], id=[" + id + "]. ", e);
        }
    }

    private Map.Entry<String,String> getSectorAndSectorName(Matcher matcher) {
        String sector;
        String sectorName;
        String sectorWithoutName = matcher.group(SECTOR_WITHOUT_NAME);
        if (sectorWithoutName!=null) {
            sector = SECTOR_WITHOUT_NAME;
            sectorName = EMPTY;
        } else {
            sector = matcher.group(SECTOR);
            if(sector==null) return null;
            sectorName = matcher.group(SECTOR_NAME);
        }
        return new AbstractMap.SimpleEntry<>(sector, sectorName);
    }

    private String replaceCyrillicChars(String replacedString) {
        char[] charArray = replacedString.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = replacedChars.containsKey(charArray[i])? replacedChars.get(charArray[i]) : charArray[i];
        }
        return String.valueOf(charArray);
    }
}
