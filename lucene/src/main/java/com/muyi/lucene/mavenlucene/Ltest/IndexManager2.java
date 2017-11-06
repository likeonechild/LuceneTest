package com.muyi.lucene.mavenlucene.Ltest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * @author xinghl
 *
 */
public class IndexManager2{
    private static IndexManager indexManager;
    private static String content="";
    
    private static String INDEX_DIR = "D:\\luceneIndex";
    private static String DATA_DIR = "D:\\luceneData";
    private static Analyzer analyzer = null;
    private static Directory directory = null;
    private static IndexWriter indexWriter = null;
    
    /**
     * 创建索引管理器
     * @return 返回索引管理器对象
     */
    public IndexManager getManager(){
        if(indexManager == null){
            this.indexManager = new IndexManager();
        }
        return indexManager;
    }
    /**
     * 创建当前文件目录的索引
     * @param path 当前文件目录
     * @return 是否成功
     */
    public static boolean createIndex(String path){
        Date date1 = new Date();
        List<File> fileList = getFileList(path);
        for (File file : fileList) {
            content = "";
            //获取文件后缀
            String type = file.getName().substring(file.getName().lastIndexOf(".")+1);
            if("txt".equalsIgnoreCase(type)){
                
                content += txt2String(file);
                System.out.println("文件名字："+file.getPath()+"文件内容"+content);
            
            }else if("doc".equalsIgnoreCase(type)){
            
                content += doc2String(file);
                System.out.println("文件名字："+file.getPath()+"文件内容"+content);
            
            }else if("xls".equalsIgnoreCase(type)){
                
                content += xls2String(file);
                System.out.println("文件名字："+file.getPath()+"文件内容"+content);
                
            }
            try{
                analyzer = new StandardAnalyzer();
                directory = FSDirectory.open(FileSystems.getDefault().getPath(INDEX_DIR));
    
                File indexFile = new File(INDEX_DIR);
                if (!indexFile.exists()) {
                    indexFile.mkdirs();
                }
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                indexWriter = new IndexWriter(directory, config);
                indexWriter.deleteAll();// 清除以前的index
                Document document = new Document();
                document.add(new TextField("filename", file.getName(), Store.YES));
                document.add(new TextField("content", content, Store.YES));
                document.add(new TextField("path", file.getPath(), Store.YES));
                indexWriter.addDocument(document);
                indexWriter.commit();
                closeWriter();
    
                
            }catch(Exception e){
                e.printStackTrace();
            }
            content = "";
        }
        Date date2 = new Date();
        System.out.println("创建索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
        return true;
    }
    
    /**
     * 读取txt文件的内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String txt2String(File file){
        String result = "";
        try{
        	FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result = result + "\n" +s;
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 读取doc文件内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String doc2String(File file){
        String result = "";
        try{
            FileInputStream fis = new FileInputStream(file);
            HWPFDocument doc = new HWPFDocument(fis);
            Range rang = doc.getRange();
            result += rang.text();
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 读取xls文件内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String xls2String(File file){
        String result = "";
        try{
            FileInputStream fis = new FileInputStream(file);   
            StringBuilder sb = new StringBuilder();   
            jxl.Workbook rwb = Workbook.getWorkbook(fis);   
            Sheet[] sheet = rwb.getSheets();   
            for (int i = 0; i < sheet.length; i++) {   
                Sheet rs = rwb.getSheet(i);   
                for (int j = 0; j < rs.getRows(); j++) {   
                   Cell[] cells = rs.getRow(j);   
                   for(int k=0;k<cells.length;k++)   
                   sb.append(cells[k].getContents());   
                }   
            }   
            fis.close();   
            result += sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 查找索引，返回符合条件的文件
     * @param text 查找的字符串
     * @return 符合条件的文件List
     */
    public static void searchIndex(String text){
        Date date1 = new Date();
        try{
            directory = FSDirectory.open(FileSystems.getDefault().getPath("D:\\luceneIndex"));
            analyzer = new StandardAnalyzer();
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
    
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(text);
            
            TopDocs topDocs = isearcher.search(query, 1000);
            System.out.println(topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            System.out.println("--------------------查找结果-----------------------");
            for (ScoreDoc scoreDoc : scoreDocs) {  

                // 7、根据searcher和ScoreDoc对象获取具体的Document对象  
                Document document = isearcher.doc(scoreDoc.doc);  

                // 8、根据Document对象获取需要的值  
                
                System.out.println(document.get("filename") + document.get("content") + " " + document.get("path"));
            }
            System.out.println("--------------------查找结果-----------------------");
            ireader.close();
            directory.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Date date2 = new Date();
        System.out.println("查看索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
    }
    /**
     * 过滤目录下的文件
     * @param dirPath 想要获取文件的目录
     * @return 返回文件list
     */
    public static List<File> getFileList(String dirPath) {
        File[] files = new File(dirPath).listFiles();
        List<File> fileList = new ArrayList<File>();
        for (File file : files) {
            if (isTxtFile(file.getName())) {
                fileList.add(file);
            }
        }
        return fileList;
    }
    /**
     * 判断是否为目标文件，目前支持txt xls doc格式
     * @param fileName 文件名称
     * @return 如果是文件类型满足过滤条件，返回true；否则返回false
     */
    public static boolean isTxtFile(String fileName) {
        if (fileName.lastIndexOf(".txt") > 0) {
            return true;
        }else if (fileName.lastIndexOf(".xls") > 0) {
            return true;
        }else if (fileName.lastIndexOf(".doc") > 0) {
            return true;
        }
        return false;
    }
    
    public static void closeWriter() throws Exception {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }
    /**
     * 删除文件目录下的所有文件
     * @param file 要删除的文件目录
     * @return 如果成功，返回true.
     */
    public static boolean deleteDir(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(int i=0; i<files.length; i++){
                deleteDir(files[i]);
            }
        }
        file.delete();
        return true;
    }
    public static void main(String[] args){
    	Date date1 = new Date();
        File fileIndex = new File(INDEX_DIR);
        if(deleteDir(fileIndex)){
            fileIndex.mkdir();
        }else{
            fileIndex.mkdir();
        }
        
        createIndex(DATA_DIR);
        searchIndex("黑山洞");
        Date date2 = new Date();
        System.out.println("执行耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
    }
}