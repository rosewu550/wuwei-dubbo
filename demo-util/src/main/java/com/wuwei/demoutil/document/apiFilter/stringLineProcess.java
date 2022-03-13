package com.wuwei.demoutil.document.apiFilter;

import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class stringLineProcess {
    public static void main(String[] args) {
        getModuleClass();
    }


    public static void getModuleClass() {
        Path filePath = Paths.get("/Volumes/storage/wuwei/macos/doc/使用om.weaver.teams.domain.user.Avatar的项目或者jar.csv");
        Set<String> moduleNameSet = new HashSet<>();
        Map<String, List<String>> resultMap = new HashMap<>();
        try (Stream<String> stringStream = Files.lines(filePath)) {
            stringStream.forEach(stringLine -> {
                String[] lineSplit = stringLine.split(",");
                String moduleName = lineSplit[0];
                String moduleClass = lineSplit[1];
                if (moduleNameSet.add(moduleName)) {
                    List<String> currentModuleClassList = new ArrayList<>();
                    currentModuleClassList.add(moduleClass);
                    resultMap.put(moduleName, currentModuleClassList);
                } else {
                    List<String> currentModuleClassList = resultMap.get(moduleName);
                    currentModuleClassList.add(moduleClass);
                }
            });

            Gson gson = new Gson();
            System.out.println(gson.toJson(resultMap));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
