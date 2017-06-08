package org.cern.cms.dbloader.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.cern.cms.dbloader.manager.file.ArchiveFile;

import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;

public class FilesManager {

    private static final int BUFFER = 2048;
    private static final File TMP_FOLDER = new File(System.getProperty("java.io.tmpdir"));
    private static final Pattern XML_FILE = Pattern.compile("\\.xml$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ZIP_FILE = Pattern.compile("\\.zip$", Pattern.CASE_INSENSITIVE);
    private static final Pattern XMA_FILE = Pattern.compile("\\.xma$", Pattern.CASE_INSENSITIVE);

    public static Set<FileBase> getFiles(List<String> listOfFiles) throws Exception {
        Set<FileBase> files = new LinkedHashSet<>();

        for (String fileName : listOfFiles) {
            File f = new File(fileName);
            
            if (XML_FILE.matcher(f.getAbsolutePath()).find()) {
                files.add(new DataFile(new ArchiveFile(f), f));
            } else

            if (ZIP_FILE.matcher(f.getAbsolutePath()).find()) {
                files.add(new ArchiveFile(f));
            } else {
                throw new IllegalArgumentException(String.format("unknown file type (%s). Only .zip and .xml files accepted", fileName));
            }
        }

        return files;
    }

    public static List<DataFile> getDataFiles(ArchiveFile archive) throws Exception {
        ArrayList<DataFile> files = new ArrayList<>();
        FileTypeManager fm = new FileTypeManager();
        for (File f : extractZip(archive.getFile())) {
            if ((XML_FILE.matcher(f.getAbsolutePath()).find()) || XMA_FILE.matcher(f.getAbsolutePath()).find()) {
                files.add(new DataFile(archive, f));
            }
        }
        Collections.sort(files);
        return files;
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
