import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class InstallBundleTest {
    @Test
    public void installBundleTaskIsCreated() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'alfresco-dynamic-extension'

        Assert.assertNotNull(project.tasks.installBundle)

        project.useDynamicExtensionsVersion("M6-silly")
        Assert.assertEquals("M6-silly", project.dynamicExtensions.version)

        project.useAlfrescoVersion("6-silly")
        Assert.assertEquals("6-silly", project.alfresco.version)

        project.useSurfVersion("1.2-silly")
        Assert.assertEquals("1.2-silly", project.surf.version)
    }
}