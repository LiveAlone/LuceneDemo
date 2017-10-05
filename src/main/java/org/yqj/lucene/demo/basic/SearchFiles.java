package org.yqj.lucene.demo.basic;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by yaoqijun on 2017-09-29.
 * 通过读取创建搜索的文件 读取搜索内容
 */
public class SearchFiles {

    public static void doPagingSearch(BufferedReader in, IndexSearcher indexSearcher, Query query, int hitsPerPage,
                                      boolean raw, boolean interactive) throws IOException{
        TopDocs result = indexSearcher.search(query, 5 * hitsPerPage);

        ScoreDoc[] hits = result.scoreDocs;

        int numTotalHits = Math.toIntExact(result.totalHits);
        System.out.println("num total hits content is " + numTotalHits);

        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);
        while (true){
            if (end > hits.length){
                System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
                hits = indexSearcher.search(query, numTotalHits).scoreDocs;
            }
        }
    }

}
