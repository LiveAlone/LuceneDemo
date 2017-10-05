package org.yqj.lucene.demo.basic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by yaoqijun on 2017-09-28.
 */
public class IndexFiles {

    private IndexFiles(){}

    public static void main(String[] args) {
        String docsPath = "docs";
        String luceneDataPath = "luceneData";
        boolean created = true;
        try {
            Directory directory = FSDirectory.open(Paths.get(luceneDataPath));

            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

            if (created){
                indexWriterConfig.setOpenMode(OpenMode.CREATE);
            }else {
                indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

            indexDocs(indexWriter, Paths.get(docsPath));

            indexWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static void indexDocs(IndexWriter indexWriter, Path path) throws IOException{
        if (Files.isDirectory(path)){
            Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexDoc(indexWriter, file, attrs.lastModifiedTime().toMillis());
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("index docs error");
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    static void indexDoc(IndexWriter indexWriter, Path file, long lastModified) throws IOException{
        try (InputStream stream = Files.newInputStream(file)){

            Document document = new Document();

            Field pathField = new StringField("path", file.toString(), Store.YES);
            document.add(pathField);

            document.add(new LongPoint("midified", lastModified));

            document.add(new TextField("content", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE){
                System.out.println("adding " + file);
                indexWriter.addDocument(document);
            }else {
                System.out.println("updating " + file);
                indexWriter.updateDocument(new Term("path", file.toString()), document);
            }
        }
    }

}
