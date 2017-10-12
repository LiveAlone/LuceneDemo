package org.yqj.lucene.demo.basic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by yaoqijun on 2017-09-29.
 * 通过读取创建搜索的文件 读取搜索内容
 */
public class SearchFiles {

    public static void main(String[] args) throws Exception{
        String index = "luceneData";
        String field = "content";
        String queryString = "Amazon";
        int hitsPerPage = 10;

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();

        QueryBuilder builder = new QueryBuilder(analyzer);
        Query query = builder.createPhraseQuery(field, queryString);
        System.out.println("Searching for: " + query.toString(field));
        System.out.println("Searching for: " + query.toString(field));

        doPagingSearch(searcher, query, hitsPerPage);

        reader.close();
    }

    public static void doPagingSearch(IndexSearcher searcher, Query query, int hitsPerPage) throws IOException{

        // Collect enough docs to show 5 pages
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = Math.toIntExact(results.totalHits);
        System.out.println(numTotalHits + " total matching documents");

        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);

        if (end > hits.length) {
            System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
            System.out.println("Collect more (y/n) ?");

            hits = searcher.search(query, numTotalHits).scoreDocs;
        }

        end = Math.min(hits.length, start + hitsPerPage);

        for (int i = start; i < end; i++) {
            System.out.println("doc="+hits[i].doc+" score="+hits[i].score);

            Document doc = searcher.doc(hits[i].doc);
            String path = doc.get("path");
            if (path != null) {
                System.out.println((i+1) + ". " + path);
                String title = doc.get("title");
                if (title != null) {
                    System.out.println("   Title: " + doc.get("title"));
                }
            } else {
                System.out.println((i+1) + ". " + "No path for this document");
            }
        }
    }

}
