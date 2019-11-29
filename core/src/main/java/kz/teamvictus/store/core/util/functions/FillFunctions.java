package kz.teamvictus.store.core.util.functions;


import kz.teamvictus.store.core.util.constance.CoreConstance;
import kz.teamvictus.store.core.util.models.Data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FillFunctions {

    public static List<Data> fillDataListFromTxtFile(String fileName){
        try {
            List<Data> dataList = new ArrayList<>();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(fileName);
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);

            BufferedReader br = new BufferedReader(streamReader);
            String st;
            while ((st = br.readLine()) != null){
                List<Double> line = convertStringListToIntList(Arrays.asList(st.split(" ")), Double::parseDouble);
                dataList.add(new Data(line));
            }
            return dataList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T, U> List<U> convertStringListToIntList(List<T> listOfString, Function<T, U> function) {
        return listOfString.stream().map(function).collect(Collectors.toList());
    }

    public static List<Double> fillAttributes(Integer size) {
        List<Double> attributes = new ArrayList<>();
        for(int i = 0; i < size; i++){
            attributes.add(0.0);
        }
        return attributes;
    }
}
