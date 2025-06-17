package com.shop.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    public static <T> List<T> loadFileFromJson(String file, Class<T> clazz) {
        File myFile = new File(file);
        if (!myFile.exists()) return new ArrayList<>();

        try {
            return mapper.readValue(myFile,
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static  <T> void  saveFileToJson(String file, List<T> items) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(file), items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T loadFileFromXML(String file, Class<T> clazz) {
        File myFile = new File(file);
        if (!myFile.exists()) return null;

        try {
            return xmlMapper.readValue(myFile, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void saveFileToXML(String file, T data) {
        try {
            File f = new File(file);
            f.getParentFile().mkdirs();
            xmlMapper.writerWithDefaultPrettyPrinter().writeValue(f, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
