package com.rayxxzhang.simplespring.test.core;

import com.rayxxzhang.simplespring.core.annotations.Autowired;
import com.rayxxzhang.simplespring.core.annotations.Component;

/**
 * Created by Ray on 3/23.
 */
@Component
public class TestBean2 {
    private TestBean3 tb3;

    public TestBean3 getTb3() {
        return tb3;
    }
    @Autowired
    public void setTb3(TestBean3 tb3) {
        this.tb3 = tb3;
    }
}
