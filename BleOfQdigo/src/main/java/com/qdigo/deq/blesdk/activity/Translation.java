package com.qdigo.deq.blesdk.activity;

import java.util.List;

/**
 * Created by jpj on 2017/1/11.
 */

public class Translation {

    /**
     * translation : ["How are you"]
     * basic : {"phonetic":"nǐ hǎo","explains":["hello；hi"]}
     * query : 你好
     * errorCode : 0
     * web : [{"value":["Hello","How do you do","hi"],"key":"你好"},{"value":["How are you","How Do You Do","Harvey, how are you Harvey"],"key":"你好吗"},{"value":["Teacher Kim Bong-du","My Teacher Mr Kim","Seonsaeng Kim Bong-du"],"key":"老师你好"}]
     */

    private BasicBean basic;
    private String query;
    private int errorCode;
    private List<String> translation;
    private List<WebBean> web;

    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public List<WebBean> getWeb() {
        return web;
    }

    public void setWeb(List<WebBean> web) {
        this.web = web;
    }

    public static class BasicBean {
        /**
         * phonetic : nǐ hǎo
         * explains : ["hello；hi"]
         */

        private String phonetic;
        private List<String> explains;

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public List<String> getExplains() {
            return explains;
        }

        public void setExplains(List<String> explains) {
            this.explains = explains;
        }
    }

    public static class WebBean {
        /**
         * value : ["Hello","How do you do","hi"]
         * key : 你好
         */

        private String key;
        private List<String> value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }


}
