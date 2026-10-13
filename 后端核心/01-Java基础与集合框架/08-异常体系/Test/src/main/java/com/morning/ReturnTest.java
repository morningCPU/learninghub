package com.morning;

public class ReturnTest {
    public int re(){
        try{
            return 1;
        }finally {
            return 2;
        }
    }

    public void diy() throws BizException{
        throw new BizException("biz");
    }
}
