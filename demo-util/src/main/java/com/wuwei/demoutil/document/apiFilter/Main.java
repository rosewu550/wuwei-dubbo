package com.wuwei.demoutil.document.apiFilter;

public class Main {
    public static void main(String[] args) {
//        ApiList apiList = new ApiList();
        String sourceStr = "/Users/alphaxx/Documents/doc/log/log.txt";
        String targetStr = "/Users/alphaxx/Documents/doc/log/sqlScript.txt";
//        apiList.getApiList(sourceStr,targetStr);

//        apiList.getNewApi(sourceStr,targetStr);
//        apiList.deleteAbandonApi(sourceStr,targetStr);

        SqlScript sqlScript = new SqlScript();
        sqlScript.getScriptList(sourceStr, targetStr);
    }
}
