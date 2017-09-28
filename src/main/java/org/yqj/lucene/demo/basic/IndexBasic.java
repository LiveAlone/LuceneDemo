package org.yqj.lucene.demo.basic;

/**
 * Created by yaoqijun on 2017-09-28.
 */
public class IndexBasic {
    public static void main(String[] args) throws Exception {
        Indexer indexer = new Indexer("luceneData");
        indexer.indexDirContent("docs");
    }
}
