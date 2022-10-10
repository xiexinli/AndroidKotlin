package com.example.design_pattern.pattern_action.chain;

import com.example.design_pattern.pattern_action.template.OtherPayShopping;
import com.example.design_pattern.pattern_action.template.ShoppingCart;
import com.example.design_pattern.pattern_create.fatory.simple.StaticFactory;
import com.example.design_pattern.entity.Fruit;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板方法模式
 * 订单费用结算过程
 */
public class ChainCartClient {

    //初始化满减优惠券
    private static MultyDiscount multyDiscount = new FullMultyDiscount(null);
    static {
        multyDiscount = new NewerMultyDiscount(multyDiscount);
        multyDiscount = new SecondMultyDiscount(multyDiscount);
        multyDiscount = new HolidayMultyDiscount(multyDiscount);


    }

    public static void main(String[] args) {
        List<Fruit> products = new ArrayList();

        products.add(StaticFactory.getFruitApple());
        products.add(StaticFactory.getFruitBanana());
        products.add(StaticFactory.getFruitOrange());

        ShoppingCart cart = new OtherPayShopping(products);

        //注入优惠方案
        cart.setDiscount(multyDiscount);

        cart.submitOrder();
    }


}
