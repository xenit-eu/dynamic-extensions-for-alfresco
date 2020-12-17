package com.github.dynamicextensionsalfresco.gradle.integrationTest;

import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractIntegrationTest {
    protected static void assertPath(Predicate<Path> check, Path path) {
        assertTrue(path.toString(), check.test(path));
    }

   @Parameters(name = "Gradle v{0}")
   public static Collection<Object[]> testData() {
       return Arrays.asList(new Object[][]{
               {"6.7.1"},
               {"6.6.1"},
               {"6.5.1"},
               {"6.4.1"},
               {"6.3"},
               {"6.1.1"},
               {"6.0.1"},
               {"5.6.4"},
               {"5.4.1"},
               {"5.3.1"},
               {"5.2.1"},
               {"5.1.1"},
               {"5.0"},
               {"4.10.3"},
       });
    }

    @Parameter(0)
    public String gradleVersion;

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    protected BuildResult buildProject(Path projectFolder, String task) throws IOException {
        FileUtils.copyDirectory(projectFolder.toFile(), testProjectDir.getRoot());
        return GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withPluginClasspath()
                .withGradleVersion(gradleVersion)
                .withArguments(task, "--stacktrace", "--rerun-tasks")
                .withDebug(true)
                .forwardOutput()
                .build();
    }

}
