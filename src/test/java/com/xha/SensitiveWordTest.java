package com.xha;


import com.xha.utils.SensitiveReplaceUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
public class SensitiveWordTest {

    @Resource
    private SensitiveReplaceUtil sensitiveReplaceUtil;

    @Test
    public void SensitiveWordFilter() {
//        String string = "我是成人视频，打跑日本鬼子";
//        String replaceSensitiveWord = SensitiveReplaceUtil.getInstance().
//                replaceSensitiveWord(string, 2);
//        System.out.println(string + "->" + replaceSensitiveWord);
        String string = "我是成人视频，打跑日本鬼子";
        String replaceSensitiveWord = sensitiveReplaceUtil
                .replaceSensitiveWord(string, 2);
        System.out.println(string + "->" + replaceSensitiveWord);
    }
}
