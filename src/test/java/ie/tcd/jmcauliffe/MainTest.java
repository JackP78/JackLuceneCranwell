package ie.tcd.jmcauliffe;

import junit.framework.TestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class MainTest extends TestCase {

    public void testStandardAnalyzerVSM() {
        Main.testCombination(new StandardAnalyzer(), new ClassicSimilarity(), "StandardAnalyzerVSM");
    }
}