package org.broadinstitute.hellbender.tools.walkers.fasta;

import org.broadinstitute.hellbender.CommandLineProgramTest;
import org.broadinstitute.hellbender.testutils.ArgumentsBuilder;
import org.testng.annotations.Test;

import java.io.File;

public class CountBasesInReferenceIntegrationTest extends CommandLineProgramTest {

    @Test
    public void testExampleReferenceWalker(){
        final ArgumentsBuilder args = new ArgumentsBuilder();
        args.addReference(new File(hg19MiniReference))
                .addArgument("L", "1:5000-6000");
        runCommandLine(args);
    }

    @Test
    public void testExampleReferenceWalkerFull(){
        final ArgumentsBuilder args = new ArgumentsBuilder();
        args.addReference(new File(hg38Reference));
        runCommandLine(args);
    }
}