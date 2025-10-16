package com.dandbazaar.back.common;

import org.springframework.stereotype.Service;

@Service
public class SwordPriceService {
    public static Double SWORD_PRICE = 20.0;

    public static Double getRatio(Double ingameprice) {
        return SWORD_PRICE / ingameprice;
    }

    public static Double getOriginalSwordPrice(Double ratio) {
        return SWORD_PRICE * ratio;
    }
    
}
