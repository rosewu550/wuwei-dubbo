package com.wuwei.demoutil.document.apiFilter;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class apiList {

    /**
     * 对swagger扫描后的接口清单去重
     * @param sourcePathStr
     * @param targetPathStr
     */
    public void getApiList(String sourcePathStr, String targetPathStr) {
        Path path = Paths.get(sourcePathStr);
        Path targetPath = Paths.get(targetPathStr);
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(bufferedReader, Map.class);
            LinkedTreeMap<String, Object> pathsMap = (LinkedTreeMap) map.get("paths");
            Set<String> set = new LinkedHashSet<>();
            pathsMap.forEach((key, value) -> set.add(key));

            try {
                Files.createFile(targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bufferedWriter = Files.newBufferedWriter(targetPath);
            set.forEach(api -> {
                try {
                    System.out.println(">>>>>>" + api);
                    bufferedWriter.write(api);
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
