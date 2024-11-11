package com.wuwei.demoutil.document.apiFilter;

import com.wuwei.demoutil.commonutil.SnowFlakeGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SqlScript {

    public void getScriptList(String sourcePathStr, String targetPathStr) {
        Path path = Paths.get(sourcePathStr);
        Path targetPath = Paths.get(targetPathStr);
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            try {
                Files.createFile(targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bufferedWriter = Files.newBufferedWriter(targetPath);
            SnowFlakeGenerator.Factory factory = new SnowFlakeGenerator.Factory();
            SnowFlakeGenerator snowFlakeGenerator = factory.create(5, 5);
            Set<String> duplicateSet = new HashSet<>();
            bufferedReader.lines().filter(line -> {
                        String s = Optional.ofNullable(line).orElse("");
                        return s.contains("##Exeute.sql##");
                    }).map(line -> {
                        int startIndex = line.indexOf("File:") + 5;
                        int endIndex = line.indexOf(".sql ") + 4;
                        return line.substring(startIndex, endIndex);
                    }).filter(duplicateSet::add)
                    .map(line ->
                            "insert into doc.sqlupgradeloginfo(id, create_time, tenant_key, scriptname, delete_type) values (" + snowFlakeGenerator.nextId() + ", '2022-07-07 20:57:21', 'all_teams', '" + line + "', 0);")
                    .forEach(line -> {
                        System.out.println(">>>>>>" + line);
                        try {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
