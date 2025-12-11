package com.distributed.ecommerce.hybrid;

import java.util.concurrent.atomic.AtomicReference;

public class SingeltonWithAR {
    private static  final AtomicReference<SingeltonWithAR> INSTANCE= new AtomicReference();

    private SingeltonWithAR(){

    }

    public  static SingeltonWithAR getInstance(){
        SingeltonWithAR current = INSTANCE.get();
        if(current == null){
            SingeltonWithAR singleton = new SingeltonWithAR();
            if(INSTANCE.compareAndSet(null, singleton)){
                return singleton;

            }else{
                return INSTANCE.get();
            }

        }
        return current;
    }

}
