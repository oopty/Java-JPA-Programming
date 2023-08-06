package me.oopty.chapter10;

import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.types.dsl.BooleanExpression;

public class ProductExpression {

    @QueryDelegate(Product.class)
    public static BooleanExpression isExpensive(QProduct product, int price) {
        return product.price.gt(price);
    }
}
