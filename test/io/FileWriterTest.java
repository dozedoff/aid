package io;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.activity.InvalidActivityException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import filter.Filter;
import gui.BlockListDataModel;


public class FileWriterTest {
	ConnectionPoolaid mockConnectionPoolaid = mock(ConnectionPoolaid.class);
	ThumbnailLoader mockThumbnailLoader = mock(ThumbnailLoader.class);
	Filter mockFilter = mock(Filter.class);
	
	FileWriter fileWriter;
	File testDir;
	byte[] testData = {12,45,6,12,99};
	File[] testFilesRelative = {new File("a\\test1.txt"),new File("a\\test2.txt"),new File("b\\test1.txt"),new File("c\\test1.txt"),new File("c\\test2.txt")};
	ArrayList<File> testFiles;
	BlockListDataModel bldm;

	/**
	 * Create a new Filewriter for the test.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		fileWriter = new FileWriter(mockFilter);
		testDir = Files.createTempDirectory("fileWriterTest").toFile();
		testFiles = new ArrayList<>();
		
		for(File file : testFilesRelative){
			testFiles.add(new File(testDir,file.toString()));
		}
		
		testDir = new File(testDir.toString()+"dir");	// create a temp directory based on the obtained path
		testDir.mkdirs();
		testDir.deleteOnExit();
	}
	/**
	 * Shutdown the Filewriter and delete files created during the test.
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		fileWriter.shutdown();

		for(File f : testFiles)
			f.delete();

		new File(testDir,"\\a").delete();
		new File(testDir,"\\b").delete();
		new File(testDir,"\\c").delete();

		testDir.delete();
	}
	/**
	 * Check that files are written to disk, and also check that buffer flushing works.
	 * @throws InvalidActivityException
	 * @throws InterruptedException
	 */
	@Test
	public void testAdd() throws InvalidActivityException, InterruptedException {
		for(File f : testFiles)
			fileWriter.add(f, testData);


		Thread.sleep(6000);// wait for buffer to clear

		for(File f : testFiles)
			assertTrue("File "+f.toString()+" not found",f.exists());
	}
	
	/**
	 * Check that shutdown triggers a buffer flush.
	 * @throws InvalidActivityException
	 */
	@Test
	public void testShutdown() throws InvalidActivityException{
		for(File f : testFiles)
			fileWriter.add(f, testData);

		fileWriter.shutdown();

		for(File f : testFiles)
			assertTrue("File "+f.toString()+" not found",f.exists());
	}

	@Test
	public void testGetPendingWrites() throws InvalidActivityException {
		for(File f : testFiles)
			fileWriter.add(f, testData);
		
		assertThat(fileWriter.getPendingWrites(), is(5));
		fileWriter.shutdown();
		assertThat(fileWriter.getPendingWrites(), is(0));
	}

	@Test
	public void testGetBytesSaved() throws InvalidActivityException {
		for(File f : testFiles)
			fileWriter.add(f, testData);
		
		fileWriter.shutdown();
		
		assertThat(fileWriter.getBytesSaved(), is(25L));
	}

	@Test
	public void testGetBytesDiscarded() throws InvalidActivityException {
		fileWriter = new FileWriter(mockFilter);
		when(mockFilter.exists(anyString())).thenReturn(true);
		
		for(File f : testFiles)
			fileWriter.add(f, testData);
		
		fileWriter.shutdown();
		
		assertThat(fileWriter.getBytesDiscarded(), is(25L));
	}
	
	@Test
	public void testInvalidFileName() throws Exception{
		fileWriter.add(new File(testDir,"ooops+%�!<>.txt"), testData);
		Thread.sleep(10000);
		
		ArrayList<String> filenames = new ArrayList<>();
		
		for(File file : testDir.listFiles()){
			filenames.add(file.getName());
		}
		
		assertThat(filenames,hasItem(both(containsString("renamed_")).and(containsString(".txt"))));
	}
}