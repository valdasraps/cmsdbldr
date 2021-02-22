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

import com.google.inject.Inject;
import java.io.FileInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.collections.list.TreeList;
import org.cern.cms.dbloader.manager.file.ArchiveFile;

import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;

import javax.inject.Singleton;

@Singleton
public class FilesManager {

    private static final int BUFFER = 2048;
    private static final File TMP_FOLDER = new File(System.getProperty("java.io.tmpdir"));
    private static final Pattern XML_FILE = Pattern.compile("\\.xml$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ZIP_FILE = Pattern.compile("\\.zip$", Pattern.CASE_INSENSITIVE);
    private static final Pattern XMA_FILE = Pattern.compile("\\.xma$", Pattern.CASE_INSENSITIVE);
    private static final Pattern JSON_FILE = Pattern.compile("\\.json$", Pattern.CASE_INSENSITIVE);

    @Inject
    private ResourceFactory rf;

    public Set<FileBase> getFiles(List<String> listOfFiles) throws Exception {
        Set<FileBase> files = new LinkedHashSet<>();

        for (String fileName : listOfFiles) {
            File f = new File(fileName);

            if (XML_FILE.matcher(f.getAbsolutePath()).find()) {
                files.add(rf.createDataFile(rf.createArchiveFile(f), f, DataFile.Type.XML));
            } else

            if (ZIP_FILE.matcher(f.getAbsolutePath()).find()) {
                files.add(rf.createArchiveFile(f));
            } else 
                
            if(JSON_FILE.matcher(f.getAbsolutePath()).find()) {
                files.add(rf.createDataFile(rf.createArchiveFile(f), f, DataFile.Type.JSON));
            } 
            
            else {
                throw new IllegalArgumentException(String.format("unknown file type (%s). Only .zip and .serial files accepted", fileName));
            }

        }

        return files;
    }

    public List<DataFile> getDataFiles(ArchiveFile archive) throws Exception {
        List<DataFile> files = new TreeList();
        for (File f : extractZip(archive.getFile())) {
            if ((XML_FILE.matcher(f.getAbsolutePath()).find()) || XMA_FILE.matcher(f.getAbsolutePath()).find()) {
                files.add(rf.createDataFile(archive, f, DataFile.Type.XML));
            } else if (JSON_FILE.matcher(f.getAbsolutePath()).find()) {
                files.add(rf.createDataFile(archive, f, DataFile.Type.JSON));
            }
        }
        return files;
    }

    public Set<File> extractZip(File zipFile) throws ZipException, IOException {
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
    
    public File createZip(File... files) throws ZipException, IOException {
        
        File zipFile = File.createTempFile("temp", ".zip", TMP_FOLDER);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        
        for (File f: files) {
            out.putNextEntry(new ZipEntry(f.getName()));
            try (FileInputStream in = new FileInputStream(f)) {
                byte[] data = in.readAllBytes();
                out.write(data, 0, data.length);
            }
            out.closeEntry();
        }

        out.close();
        
        return zipFile;
    }

}
