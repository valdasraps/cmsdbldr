package org.cern.cms.dbloader.rest;

import com.google.common.collect.Maps;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

/**
 * Application configuration holder, singleton
 * @author valdo
 */
@Log4j
public class ApplicationConfig {

    private static class HOLDER {
        static ApplicationConfig instance = new ApplicationConfig();
    }
    
    private ApplicationConfig() { }
    
    public static ApplicationConfig getInstance() {
        return HOLDER.instance;
    }
    
    private static final Pattern FILE_PATTERN = Pattern.compile("^([a-zA-Z0-9]+)_([a-zA-Z0-9]+)\\.properties");
    
    @Getter
    private final Map<String, Map<String, LoaderApplicationExecutor>> props = Maps.newHashMap();

    public void load(final Path descriptorFolder, final Path logFolder) throws Exception {
        Iterator<Path> it = Files.newDirectoryStream(descriptorFolder).iterator();
        while (it.hasNext()) {
            Path p = it.next();
            if (Files.exists(p) && Files.isReadable(p) && Files.isRegularFile(p)) {
                Matcher m = FILE_PATTERN.matcher(p.getFileName().toString());
                if (m.matches()) {
                    
                    String det = m.group(1);
                    String db = m.group(2);
                    String fname = p.toAbsolutePath().toString();
                    
                    if(!props.containsKey(det)) {
                        props.put(det, Maps.newHashMap());
                    }
                    
                    LoaderApplicationExecutor exec = new LoaderApplicationExecutor(det, db, logFolder.toString(), fname);
                    props.get(det).put(db, exec);

                }
            }
        }

    }
    
}
