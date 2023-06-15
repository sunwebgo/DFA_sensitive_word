package com.xha.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 敏感词过滤工具类
 *
 * @author Xu huaiang
 * @date 2023/06/02
 */
@Component
public class SensitiveReplaceUtil {
    /**
     * 敏感词过滤器：利用DFA算法  进行敏感词过滤
     */
    private static HashMap<String, Object> sensitiveWordHashMap = null;
    /**
     * 最小匹配规则，如：敏感词库["中国","中国人"]，语句："我是中国人"，匹配结果：我是[中国]人
     */
    public static int minMatchType = 1;
    /**
     * 最大匹配规则，如：敏感词库["中国","中国人"]，语句："我是中国人"，匹配结果：我是[中国人]
     */
    public static int maxMatchType = 2;
    /**
     * 敏感词替换词
     */
    public static String replaceChar = "*";

    @PostConstruct
    public void init() throws IOException {
        sensitiveWordHashMap = new SensitiveWordInitialize().getSensitiveWordHashMap();
    }
    /**
     * 在构造函数中初始化敏感词库
     */
    private SensitiveReplaceUtil() {
    }

    /**
     * 替换字符串当中的敏感词
     *
     * @param string    字符串
     * @param matchType 匹配类型
     * @return {@link String}
     */
    public String replaceSensitiveWord(String string, int matchType) {
        String resultString = string;
//        1.获取到当前字符串中的敏感词集合
        Set<String> sensitiveWord = getSensitiveWord(string, matchType);
//        2.迭代遍历敏感词集合
        for (String word : sensitiveWord) {
//            2.1获取到敏感词
            //            2.2根据敏感词长度创建替代字符串
            String replaceString = getReplaceString(word.length());
//            2.3替换字符串
            resultString = resultString.replaceAll(word, replaceString);
        }
        return resultString;
    }

    /**
     * 根据敏感词长度创建替代字符串
     *
     * @param length 长度
     * @return {@link String}
     */
    private String getReplaceString(int length) {
        StringBuilder replaceString = new StringBuilder();
//        根据敏感词长度创建替代字符串
        for (int i = 0; i < length; i++) {
            replaceString.append(replaceChar);
        }
        return replaceString.toString();
    }

    /**
     * 获取到字符串中的敏感词集合
     *
     * @param string    字符串
     * @param matchType 匹配类型
     * @return {@link Set}<{@link String}>
     */
    public Set<String> getSensitiveWord(String string, int matchType) {
        Set<String> set = new HashSet<>();
//        1.遍历字符串中的每一个字符
        for (int i = 0; i < string.length(); i++) {
            int length = getStringLength(string, i, matchType);
//        2.如果length大于0表示存在敏感词，将敏感词添加到集合中
            if (length > 0) {
                set.add(string.substring(i, i + length));
            }
        }
        return set;
    }


    /**
     * 检查文字中是否包含敏感字符，检查规则如下：
     * 如果存在，则返回敏感词字符的长度，不存在返回0
     *
     * @param string     字符串
     * @param beginIndex 开始下标
     * @param matchType  匹配类型
     * @return int
     */
    public int getStringLength(String string, int beginIndex, int matchType) {
//        1.当前敏感词长度，用作累加
        int nowLength = 0;
//        2.最终敏感词长度
        int resultLength = 0;
//        3.获取到敏感词库
        HashMap<String, Object> temporaryHashMap = sensitiveWordHashMap;
//        4.遍历字符串
        for (int i = beginIndex; i < string.length(); i++) {
//        5.判断当前字符是否为敏感词库中的首个字母
            String word = String.valueOf(string.charAt(i));
            temporaryHashMap = (HashMap<String, Object>) temporaryHashMap.get(word);
//            5.1如果为空表示当前字符并不为敏感词
            if (temporaryHashMap == null) {
                break;
            } else {
                nowLength++;
//            5.2判断是否为最后一个敏感词字符
                if (temporaryHashMap.get("isEnd").equals(1)) {
                    resultLength = nowLength;
//            5.3判断匹配原则，如果为2表示最大匹配原则，继续匹配，为1结束匹配
                    if (matchType == minMatchType) {
                        break;
                    }
                }
            }
        }
        return resultLength;
    }
}
