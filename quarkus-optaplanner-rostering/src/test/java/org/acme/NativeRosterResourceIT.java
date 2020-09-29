package org.acme;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeRosterResourceIT extends RosterResourceTest {

    // Execute the same tests but in native mode.
}