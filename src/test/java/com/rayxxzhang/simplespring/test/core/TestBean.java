package com.rayxxzhang.simplespring.test.core;

import com.rayxxzhang.simplespring.core.annotations.Autowired;
import com.rayxxzhang.simplespring.core.annotations.Component;

/**
 * Created by Ray on 3/23.
 */
@Component
public class TestBean {
    private TestBean2 tb2;
    private TestBean3 tb3;
    @Autowired
    public TestBean(TestBean2 tb2) {
        this.tb2 = tb2;
    }
    @Autowired
    public void setTb3(TestBean3 tb3) {
        this.tb3 = tb3;
    }
    public TestBean2 getTb2() {
        return tb2;
    }
}
