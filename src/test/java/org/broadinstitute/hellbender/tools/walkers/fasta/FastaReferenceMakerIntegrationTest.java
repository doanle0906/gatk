package org.broadinstitute.hellbender.tools.walkers.fasta;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import org.broadinstitute.hellbender.CommandLineProgramTest;
import org.broadinstitute.hellbender.testutils.ArgumentsBuilder;
import org.broadinstitute.hellbender.testutils.BaseTest;
import org.broadinstitute.hellbender.testutils.FastaTestUtils;
import org.broadinstitute.hellbender.utils.fasta.CachingIndexedFastaSequenceFile;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastaReferenceMakerIntegrationTest extends CommandLineProgramTest {

    @DataProvider
    public Object[][] getFastaParameters(){
        return new Object[][]{
                {Arrays.asList("1:10,000,100-10,000,500", "1:10,100,000-10,101,000", "1:10,900,000-10,900,001"), getTestFile("reference_only.fasta")},
                {Arrays.asList("1:10,000,100-10,000,200", "1:10,000,201-10,000,301"), getTestFile("reference_only_contiguous_same_contig.fasta")},
                {Arrays.asList("1:10,000,100-10,000,200", "2:10,000,201-10,000,301"), getTestFile("reference_only_contiguous_diff_contigs.fasta")}
        };
    }

    @Test(dataProvider = "getFastaParameters")
    public void runMakeFastaTest(List<String> intervals, File expected) throws IOException {
        ArgumentsBuilder args = new ArgumentsBuilder();
        final File out = BaseTest.createTempFile("subset", ".fasta");
        args.addReference(new File(b37Reference))
                .addOutput(out);
        intervals.forEach(interval -> args.addArgument("L", interval));
        runCommandLine(args);

        final SAMSequenceDictionary expectedDict;
        final Map<String, byte[]> sequenceBases = new HashMap<>();
        final Map<String, Integer> basesPerLine = new HashMap<>();
        try(final ReferenceSequenceFile expectedRef = new CachingIndexedFastaSequenceFile(expected.toPath())){
            expectedDict = expectedRef.getSequenceDictionary();
            for(SAMSequenceRecord sequence: expectedDict.getSequences()){
                final ReferenceSequence refSequence = expectedRef.getSequence(sequence.getSequenceName());
                sequenceBases.put(sequence.getSequenceName(), refSequence.getBases());
                basesPerLine.put(sequence.getSequenceName(), -1);
            }
        }
        FastaTestUtils.assertFastaFilesMatch(out.toPath(), expected.toPath());
    }

    //todo add tests for options...
}