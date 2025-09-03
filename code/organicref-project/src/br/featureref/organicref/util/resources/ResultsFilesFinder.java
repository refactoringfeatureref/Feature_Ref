package br.featureref.organicref.util.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import br.featureref.organicref.dataanalysis.AnalysisMode;

public class ResultsFilesFinder
{

	private static final String[] EXTENSIONS = { "json" };

	public Map<String, Collection<File>> findAll(String rootDirPath)
	{
		Map<String, Collection<File>> dirFilesMap = new HashMap<>();
		File rootDir = new File(rootDirPath);
		final File[] subDirs = rootDir.listFiles(File::isDirectory);

		if (subDirs == null || subDirs.length == 0) {
			putFilesInMap(rootDir, dirFilesMap);
		} else {
			Arrays.stream(subDirs).forEach(s -> {
				putFilesInMap(s, dirFilesMap);
			});
		}

		return dirFilesMap;
	}

	private void putFilesInMap(final File rootDir, final Map<String, Collection<File>> dirFilesMap)
	{
		Collection<File> tempFiles = FileUtils.listFiles(rootDir, EXTENSIONS, true);
		dirFilesMap.put(rootDir.getName(), tempFiles);
	}
}
