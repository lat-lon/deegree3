package org.deegree.workspace.standard;

import org.deegree.workspace.Resource;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.ResourceProvider;
import org.deegree.workspace.Workspace;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DefaultResourceLocationTest {

	@TempDir
	public static Path folder;

	private static Path resourceFileInSubfolder;

	@BeforeAll
	public static void initFolder() {
		resourceFileInSubfolder = folder.resolve("test").resolve("test.xml");
	}

	@Test
	public void testActivate() {
		DefaultResourceLocation<Resource> resourceDefaultResourceLocation = new DefaultResourceLocation<>(
				resourceFileInSubfolder.toFile(), createIdentifier("test"));

		File resourceFile = resourceDefaultResourceLocation.getAsFile();
		assertEquals(resourceFile, resourceFileInSubfolder.toFile());
		resourceDefaultResourceLocation.activate();

		File activatedResourceFile = resourceDefaultResourceLocation.getAsFile();
		assertEquals(activatedResourceFile, resourceFileInSubfolder.toFile());
	}

	@Test
	public void testActivate_InSubDirectory() {
		DefaultResourceLocation<Resource> resourceDefaultResourceLocation = new DefaultResourceLocation<>(
				resourceFileInSubfolder.toFile(), createIdentifier("test/test"));

		File resourceFile = resourceDefaultResourceLocation.getAsFile();
		assertEquals(resourceFile, resourceFileInSubfolder.toFile());
		resourceDefaultResourceLocation.activate();

		File activatedResourceFile = resourceDefaultResourceLocation.getAsFile();
		assertEquals(activatedResourceFile, resourceFileInSubfolder.toFile());
	}

	private DefaultResourceIdentifier<Resource> createIdentifier(String identifier) {
		return new DefaultResourceIdentifier<>((Class<? extends ResourceProvider<Resource>>) providerClass().getClass(),
				identifier);
	}

	private ResourceProvider<Resource> providerClass() {
		return new ResourceProvider<Resource>() {

			@Override
			public String getNamespace() {
				return null;
			}

			@Override
			public ResourceMetadata<Resource> read(Workspace workspace, ResourceLocation<Resource> location) {
				return null;
			}

			@Override
			public List<ResourceMetadata<Resource>> getAdditionalResources(Workspace workspace) {
				return null;
			}
		};
	}

}
