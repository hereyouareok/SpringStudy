package com.yan.qing.jd.controller;

import com.yan.qing.jd.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author YanQin
 * @version v1.0.0
 * @Description :
 * @Create on : 2020/11/22 21:22
 **/
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws IOException {
        return contentService.parseContent(keyword);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> parse(
            @PathVariable("keyword") String keyword,
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize") int pageSize) throws IOException {
        return  contentService.searchPage(keyword, pageNo, pageSize);
    }
}
