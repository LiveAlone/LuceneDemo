package org.yqj.lucene.demo.basic;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by yaoqijun on 2017-09-28.
 */
public class Indexer {

    private IndexWriter indexWriter;

    public Indexer(String dir) throws IOException{
        Directory directory = FSDirectory.open(new File(dir).toPath());
        IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
        indexWriter = new IndexWriter(directory, conf);

    }

    public void close() throws IOException{
        indexWriter.close();
    }

    public int indexDirContent(String dataDir) throws Exception{
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".txt")){
                indexFile(file);
            }
        }
        return indexWriter.numDocs();
    }

    private void indexFile(File f) throws Exception{
        Document doc = generateDocument(f);
        indexWriter.addDocument(doc);
    }

    private Document generateDocument(File f) throws IOException{
        Document document = new Document();
        FieldType fieldType = new FieldType();
        fieldType.setTokenized(false);
        document.add(new Field("content", new FileReader(f), fieldType));
        document.add(new Field("filename", f.getName(), fieldType));
        document.add(new Field("fullpath", f.getCanonicalPath(), fieldType));
        return document;
    }
}
