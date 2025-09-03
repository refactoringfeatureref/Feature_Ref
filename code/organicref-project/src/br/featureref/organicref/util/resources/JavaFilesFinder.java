package br.featureref.organicref.util.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class JavaFilesFinder {

	private static final String[] EXTENSIONS = { "java" };

	private List<String> directories;
	//TODO read from parameter or config
	private List<String> foldersToSkip = Arrays.asList("package-info.java", "test", "dubbo-demo");

	public JavaFilesFinder() {
		this.directories = new ArrayList<>();
	}

	public JavaFilesFinder(String sourcePath) {
		this.directories = Arrays.asList(sourcePath);
	}

	public JavaFilesFinder(List<String> sourcePaths) {
		this.directories = sourcePaths;
	}

	public void addDir(String directory) {
		this.directories.add(directory);
	}

	public String[] getSourcePaths() {
		String[] sourcePathsArray = new String[this.directories.size()];
		this.directories.toArray(sourcePathsArray);
		return sourcePathsArray;
	}

	public List<File> findAll() {
		List<File> files = new ArrayList<>();
		for (String dir : this.directories) {
			Collection<File> tempFiles = FileUtils.listFiles(new File(dir), EXTENSIONS, true);
			files.addAll(filterFolders(tempFiles));
		}
		return files;
	}

	private Collection<? extends File> filterFolders(Collection<File> tempFiles) {
		HashSet<File> selectedFiles = new HashSet<>();
		
		for (File file : tempFiles) {
			boolean shouldSkip = false;
			for (String fs : foldersToSkip) {
				if (file.getAbsolutePath().contains(fs)) {
					shouldSkip = true;
					break;
				}
			}
			if (!shouldSkip) {
				selectedFiles.add(file);
			} else {
				System.out.println("skiped: " + file);
			}
		}
		
		return selectedFiles;
	}

}
