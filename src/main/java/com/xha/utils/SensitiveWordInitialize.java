package com.xha.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 敏感词库初始化
 *
 * @author Xu huaiang
 * @date 2023/06/02
 */
@Slf4j
public class SensitiveWordInitialize {

    // 字符编码
    private final static String ENCODING = "UTF-8";

    /**
     * 敏感词库变量
     */
    private HashMap<String, Object> sensitiveWordHashMap = null;

    /**
     * 获取到敏感词库
     *
     * @return {@link HashMap}<{@link String}, {@link Object}>
     * @throws IOException ioexception
     */
    protected HashMap<String, Object> getSensitiveWordHashMap() throws IOException {
        log.info("敏感词库初始化");
        Set<String> strings = readSensitiveWordFile();
        sensitiveWordHashMap = (HashMap<String, Object>) initSensitiveHashMap(strings);
        return sensitiveWordHashMap;
    }

    /**
     * 敏感词文件读取
     *
     * @return {@link Set}<{@link String}>
     */
    private Set<String> readSensitiveWordFile() {
        Set<String> wordSet = null;
            ClassPathResource classPathResource = new ClassPathResource("sensitive_words.txt");
//        1.获取文件字节流
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = classPathResource.getInputStream();
//        2.实现字节流和字符流之间的转换
            inputStreamReader = new InputStreamReader(inputStream, ENCODING);
            wordSet = new HashSet<>();
//        3.使用缓冲流进行读取
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String txt = null;
            while ((txt = bufferedReader.readLine()) != null) {
                wordSet.add(txt);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wordSet;
    }


    /**
     * 初始化敏感词库
     *
     * @param strings 字符串
     */
    private Map initSensitiveHashMap(Set<String> strings) {
        sensitiveWordHashMap = new HashMap<>(strings.size());
//        1.创建temporaryHashMap用于存储临时数据
        HashMap<String, Object> temporaryHashMap = new HashMap<>();
//        2.遍历敏感词列表
        for (String string : strings) {
//        3.对于temporaryHashMap，其初始值就是为根节点sensitiveWordHashMap
            temporaryHashMap = sensitiveWordHashMap;
//        4.遍历每个字符
            for (int i = 0; i < string.length(); i++) {
                String word = String.valueOf(string.charAt(i));
//              4.1判断根据点是否存在该字符
                HashMap<String, Object> resultHashMap = (HashMap<String, Object>) temporaryHashMap.get(word);
//              4.2如果为空就创建节点
                if (resultHashMap == null) {
                    resultHashMap = new HashMap<String, Object>();
//              4.3以当前字符为key，new HashMap为value创建节点
                    temporaryHashMap.put(word, resultHashMap);
                }
//        5.temporaryHashMap指向下一个HashMap
                temporaryHashMap = resultHashMap;
//        6.判断是否跳过本次循环
//          如果temporaryHashMap里面已经有isEnd，并且为1，说明时树形结构中已经存在的敏感词，就不再设置isEnd
//          如日本和日本鬼子，先设置日本
//          在日本鬼子设置的时候，本对应的map有isEnd=1，如果这时对它覆盖，就会isEnd=0，导致日本这个关键字失效
                if (temporaryHashMap.containsKey("isEnd") && temporaryHashMap.get("isEnd").equals(1)) {
                    continue;
                }
//        7.封装temporaryHashMap
//          7.1判断当前字符是否为字符串的最后一个字符
                if (i == string.length() - 1) {
                    temporaryHashMap.put("isEnd", 1);
                } else {
                    temporaryHashMap.put("isEnd", 0);
                }
            }
        }
        return sensitiveWordHashMap;
    }
}
