package org.cern.cms.dbloader.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.cern.cms.dbloader.manager.file.DataFile;

public class FilesManager {

    private static final int BUFFER = 2048;
    private static final File TMP_FOLDER = new File(System.getProperty("java.io.tmpdir"));
    private static final String XML_FILE = ".xml";
    private static final String ZIP_FILE = ".zip";

    public static Set<DataFile> getFiles(List<String> listOfFiles) throws Exception {
        Set<DataFile> files = new HashSet<>();

        for (String fileName : listOfFiles) {
            File f = new File(fileName);
            if (!processFile(f, f, files)) {
                throw new IllegalArgumentException(String.format("unknown file type (%s). Only .zip and .xml files accepted", fileName));
            }
        }

        return files;
    }

    private static boolean processFile(File archive, File data, Set<DataFile> files) throws IOException {

        if (data.getAbsolutePath().toLowerCase().endsWith(XML_FILE)) {
            files.add(new DataFile(archive, data));
            return true;
        }

        if (data.getAbsolutePath().toLowerCase().endsWith(ZIP_FILE)) {
            for (File zf : extractZip(data)) {
                processFile(data, zf, files);
            }
            return true;
        }

        return false;
    }

    private static Set<File> extractZip(File zipFile) throws ZipException, IOException {
        Set<File> files = new HashSet<>();
        try (ZipFile zip = new ZipFile(zipFile)) {

            // Create temporary folder
            File unzipFolder = File.createTempFile("temp", "", TMP_FOLDER);
            unzipFolder.delete();
            unzipFolder.mkdir();

            Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
            while (zipFileEntries.hasMoreElements()) {
                ZipEntry entry = zipFileEntries.nextElement();
                File destFile = new File(unzipFolder, entry.getName());
                File destinationParent = destFile.getParentFile();
                destinationParent.mkdirs();
                if (!entry.isDirectory()) {
                    try (BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry))) {
                        int currentByte;
                        byte data[] = new byte[BUFFER];
                        FileOutputStream fos = new FileOutputStream(destFile);
                        try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
                            while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                                dest.write(data, 0, currentByte);
                            }
                            dest.flush();
                        }
                    }

                    files.add(destFile);
                }
            }

        }
        return files;
    }

}
