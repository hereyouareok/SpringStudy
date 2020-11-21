package aop;

import org.springframework.stereotype.Component;

/**
 * @author YanQin
 * @version v1.0.0
 * @Description : TODO
 * @Create on : 2020/11/21 17:23
 **/
@Component("target")
public class Target implements TargetInterface{
    public void save() {
        System.out.println("saving running............");
    }
}
