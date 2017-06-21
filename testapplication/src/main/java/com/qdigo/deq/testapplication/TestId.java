package com.qdigo.deq.testapplication;

import java.text.ParseException;

/**
 * Created by jpj on 2017-06-09.
 */

public class TestId {
    public static void main(String[] args){
        String id = "34032219920315787x";
        try {
            String s = IDCard.IDCardValidate(id);
            System.out.println(s);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
        }

    }
}
