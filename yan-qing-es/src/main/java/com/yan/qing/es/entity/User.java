package com.yan.qing.es.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author YanQin
 * @version v1.0.0
 * @Description :
 * @Create on : 2020/11/22 17:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class User {
    private String name;
    private int age;
}
