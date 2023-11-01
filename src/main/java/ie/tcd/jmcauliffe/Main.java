package ie.tcd.jmcauliffe;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;


public class Main {

    public static String INDEX_DIRECTORY = "index/";

    public static String RESULTS_DIRECTORY = "results/";


    static void testCombination(Analyzer analyzer, Similarity similarity, String scoringApproach) {
        System.out.println("Creating Index");
        Indexer createIndexes = new Indexer(analyzer);
        System.out.println("Running Queries");
        QueryEngine makeQueries = new QueryEngine(analyzer, similarity, scoringApproach);
    }

    public static void main( String[] args )
    {
        testCombination(new StandardAnalyzer(), new ClassicSimilarity(), "StandardAnalyzerVSM");
        testCombination(new WhitespaceAnalyzer(), new ClassicSimilarity(), "WhitespaceAnalyzerVSM");
        testCombination(new EnglishAnalyzer(), new ClassicSimilarity(), "EnglishAnalyzerVSM");

        testCombination(new StandardAnalyzer(), new BM25Similarity(), "StandardAnalyzerBM25");
        testCombination(new WhitespaceAnalyzer(), new BM25Similarity(), "WhitespaceAnalyzerBM25");
        testCombination(new EnglishAnalyzer(), new BM25Similarity(), "EnglishAnalyzerBM25");

    }
}