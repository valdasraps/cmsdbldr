package org.cern.cms.dbloader.dao;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.config.*;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Configuration;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import javax.management.modelmbean.XMLParseException;
import java.util.*;
import org.cern.cms.dbloader.util.OperatorAuth;

/**
 * Created by aisi0860 on 5/30/17.
 */

@Log4j
public class ConfigDao extends DaoBase {

    @Inject
    private ResourceFactory rf;

    @Inject
    public ConfigDao(@Assisted SessionManager sm, @Assisted OperatorAuth auth) throws Exception {
        super(sm, auth);
    }


    public void saveVersionAliases(Collection<VersionAlias> versionAliases, AuditLog alog) throws Exception {

        PartDao partDao = rf.createPartDao(sm, auth);

        VersionAlias dbVersionAlias;
        Dataset dbDataset;
        Part dbPart;
        KindOfCondition dbKoc;
        Set<VersionAliasDataset> dbVerAlDataset = new HashSet<>();

        for (VersionAlias xmlVersionAlias : versionAliases) {

            dbVersionAlias = resolveVersionAlias(xmlVersionAlias);

            if (dbVersionAlias == null){
                dbVersionAlias = xmlVersionAlias;
            }

            if (xmlVersionAlias.getConfig().isEmpty()) {
                throw new XMLParseException(String.format("Configuration not defined for: %s", xmlVersionAlias.getName()));
            }

            for (Configuration xmlConfig : xmlVersionAlias.getConfig()) {
                
                dbKoc = resolveKOC(xmlConfig.getKoc());
                dbDataset = resolveDataset(xmlConfig.getDataset());
                dbPart = resolvePart(xmlConfig.getPart(), true);

                if (dbKoc.equals(dbDataset.getKindOfCondition()) && dbPart.equals(dbDataset.getPart())) {

                    VersionAliasDataset verAlDataset = new VersionAliasDataset();
                    verAlDataset.setDataset(dbDataset);
                    verAlDataset.setVersionAlias(xmlVersionAlias);
                    dbVerAlDataset.add(verAlDataset);
                    dbVersionAlias.setInsertUser(auth.getOperatorValue());
                    session.save(dbVersionAlias);
                    
                    // Set operator value
                    if (dbPart.getId() == null) {
                        verAlDataset.setInsertUser(auth.getOperatorValue());
                    }

                    session.save(verAlDataset);

                } else {
                    throw new XMLParseException(String.format("Dataset not equal with KOC and Parts: %s", xmlConfig));
                }

            }
        }

        // Loop version aliases
        //   Resolve VersionAlias from DB
        //   Resolve Dataset from Configuration
        //   if not found - Exception
        // save
//TODO: if VersionAlias already exist - update or exception?
    }

    public void saveKey(Collection<Key> keys, AuditLog auditLog) throws Exception {

        PartDao partDao = rf.createPartDao(sm, auth);

        Dataset dbDataset =  null;
        Dataset subverDs = null;
        Stack<Dataset> datasetStack = new Stack<>();

        KindOfCondition dbKoc = null;
        Stack<KindOfCondition> kocStack = new Stack<>();

        Part dbPart = null;
        Stack<Part> partStack = new Stack<>();

        for (Key key : keys){

            Key dbKey = resolveKey(key);

            if (dbKey != null ) {
                throw new XMLParseException(String.format("KEY already exist in DB %s", key.getName()));
            }

            KeyType dbKeyType = resolveKeyType(key.getKeyType());

            key.setKeyType(dbKeyType);
            for (Configuration config: key.getConfig()) {

                dbDataset = resolveDataset(config.getDataset());
                dbKoc = resolveKOC(config.getKoc());
                dbPart = resolvePart(config.getPart(), true);

                if (dbDataset.getKindOfCondition().equals(dbKoc) && dbDataset.getPart().equals(dbPart)) {

                    if (key.getInsertUser() == null) { 
                        key.setInsertUser(auth.getOperatorValue());
                    }
                    session.save(key);

                    KeyTypeKOCPart keyTypeKOCPart = new KeyTypeKOCPart();
                    keyTypeKOCPart.setKoc(dbKoc);
                    keyTypeKOCPart.setKeyType(dbKeyType);
                    keyTypeKOCPart.setPart(dbPart);
                    keyTypeKOCPart.setInsertUser(auth.getOperatorValue());
                    session.save(keyTypeKOCPart);

                    KeyDataset keyDataset = new KeyDataset();
                    keyDataset.setDataset(dbDataset);
                    keyDataset.setKey(key);
                    keyDataset.setKeyTypeKOCPart(keyTypeKOCPart);
                    keyDataset.setSubDataset(dbDataset);
                    keyDataset.setInsertUser(auth.getOperatorValue());
                    session.save(keyDataset);
                }

                dbDataset.getKindOfCondition().getName();
            }

        }
        // Resolve KeyType, default = 'default'
        // Resolve Key (lookup in DB)
        // if exists - Exception
        // Resolve Datasets from Configuration(s)
        // if not found - Exception
        // check full match KeyType KOC/Part == Dataset KOC/Part
        // if not - Exception
        // save

    }

    public void saveKeyAlias(Collection<KeyAlias> keyAliases, AuditLog auditLog) throws Exception {

        Key dbKey;
        Dataset dbDataset;
        KindOfCondition dbKoc;
        Part dbPart;
        VersionAlias dbVerAlias;
        KeyAliasKey dbKeyAliasKey = new KeyAliasKey();

        for (KeyAlias keyAlias : keyAliases){

            resolveKeyAlias(keyAlias);
            if (keyAlias.getInsertUser() == null) { 
                keyAlias.setInsertUser(auth.getOperatorValue());
            }
            session.save(keyAlias);
            for (Key key : keyAlias.getKey()){

                dbKey = resolveKey(key);
                if (dbKey == null) {
                    throw new XMLParseException(String.format("KEY doesn't exist in DB %s", key.getName()));
                }


                for (Configuration config : key.getConfig()){
                    dbDataset = resolveDataset(config.getDataset());
                    dbKoc = resolveKOC(config.getKoc());
                    dbPart = resolvePart(config.getPart(), true);

                    if (dbDataset.getKindOfCondition().equals(dbKoc) && dbDataset.getPart().equals(dbPart)) {
                        log.info(String.format("Resolved Configuration for Key: %s", key.getName()));
                    } else throw new XMLParseException(String.format("Couldn't match koc and part with dataset, key name: %s", key.getName()));

                }

                dbKeyAliasKey.setKey(dbKey);
                dbKeyAliasKey.setKeyAlias(keyAlias);
                if (dbKeyAliasKey.getInsertUser() == null) { 
                    dbKeyAliasKey.setInsertUser(auth.getOperatorValue());
                }
                session.save(dbKeyAliasKey);

                for (VersionAlias verAlias : key.getVersionAlias()){
                    KeyAliasVersionAlias keyAlVerAl = new KeyAliasVersionAlias();

                    dbVerAlias = resolveVersionAlias(verAlias);
                    dbKoc = resolveKOC(verAlias.getKoc());

                    checkKOCinDataset(dbKoc, dbVerAlias);

                    keyAlVerAl.setKeyAlias(keyAlias);
                    keyAlVerAl.setVersionAlias(dbVerAlias);
                    keyAlVerAl.setKoc(dbKoc);

                    if (keyAlVerAl.getInsertUser() == null) { 
                        keyAlVerAl.setInsertUser(auth.getOperatorValue());
                    }
                    session.save(keyAlVerAl);
                }



            }
        }
        // Resolve KeyAlias
        //  If found - exception
        //  Resolve key
        //    If not found - exception
        //  Loop Configuration(s)
        //  Loop Version aliases
        //    resolve versionAlias
        //    if not found - exception
        //    resolve KOC
        //    if not found exception
        //    check if VersionAlias has Dataset of KOC
        //    if not - exception
        // save

    }

    private void checkKOCinDataset(KindOfCondition dbKoc, VersionAlias dbVerAlias) throws XMLParseException {

        int numb = 0;

        List<VersionAliasDataset> verAlDatasets = session.createCriteria(VersionAliasDataset.class)
                .add(Restrictions.eq("id", dbVerAlias.getId()))
                .list();

        if (verAlDatasets.size() == 0) {
            throw new XMLParseException(String.format("Couldn't find VersionAlias in VersionAliasMaps table %s", dbVerAlias.getName()));
        }

        for (VersionAliasDataset verAlDs : verAlDatasets){
            if (verAlDs.getDataset().getKindOfCondition().equals(dbKoc)){
                numb++;
            }
        }
        if (numb != 1){
            throw new XMLParseException(String.format("Found %s Dataset for this KindOfCondition: %s", numb, dbKoc.getName() ));
        }


    }

    private void resolveKeyAlias(KeyAlias keyAlias) throws XMLParseException {

        if (keyAlias.getName() == null ) {
            throw new XMLParseException(String.format("Couldn't resolve Key Alias because name is not defined for this key %s", keyAlias));
        }

        KeyAlias dbKey = (KeyAlias) session.createCriteria(KeyAlias.class)
                .add(Restrictions.eq("name", keyAlias.getName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

        if (dbKey != null ) {
            throw new XMLParseException(String.format("KEY already exist in DB %s", keyAlias.getName()));
        }
    }


    private KindOfCondition resolveKOC(KindOfCondition koc) throws XMLParseException {

        KindOfCondition dbKoc = (KindOfCondition) session.createCriteria(KindOfCondition.class)
                .add(Restrictions.eq("name", koc.getName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Restrictions.eq("extensionTable", koc.getExtensionTable()))
                .uniqueResult();

        if (dbKoc == null) {
            throw new XMLParseException(String.format("Not resolved: %s", koc));
        }

        log.info(String.format("Resolved: %s", dbKoc));

        return dbKoc;
    }


    private Key resolveKey(Key key) throws XMLParseException {

        if (key.getName() == null ) {
            throw new XMLParseException(String.format("Couldn't resolve because name is not defined for this key: %s", key));
        }

        Key dbKey = (Key) session.createCriteria(Key.class)
                .add(Restrictions.eq("name", key.getName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

        return dbKey;
    }


    private KeyType resolveKeyType(KeyType keyType) throws XMLParseException {

        KeyType dbKeyType = new KeyType();
        if (keyType == null) {
            dbKeyType.setName("default");
            log.info(String.format("Name not defined for keytype. Will be set default"));
        }

        dbKeyType = (KeyType) session.createCriteria(KeyType.class)
                .add(Restrictions.eq("name", dbKeyType.getName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

        if (dbKeyType == null) {
            throw new XMLParseException(String.format("Couldn't resolve KeyType by this name %s", keyType.getName()));
        }

        return dbKeyType;
    }


    private VersionAlias resolveVersionAlias(VersionAlias versionAlias) throws XMLParseException {

        if (versionAlias.getName() == null) {
            throw new XMLParseException(String.format("Name not defined for VersionAlias "));
        }

        VersionAlias verAliasDB = (VersionAlias) session.createCriteria(VersionAlias.class)
                .add(Restrictions.eq("name", versionAlias.getName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

        if (verAliasDB == null){
            log.info(String.format("Couldn't resolve VersionAlias in DB to %s", versionAlias.getName()));
        }

        return verAliasDB;
    }


    private Dataset resolveDataset(Dataset dataset) throws XMLParseException {

        Criteria c = session.createCriteria(Dataset.class)
                .add(Restrictions.eq("version", dataset.getVersion()))
                .add(Restrictions.eq("deleted", Boolean.FALSE));

        if (dataset.getKindOfCondition() != null ){
            c.add(Restrictions.eq("kindOfCondition", dataset.getKindOfCondition()));
        }
        if (dataset.getExtensionTable() != null ){
            c.add(Restrictions.eq("extensionTable", dataset.getExtensionTable()));
        }


        Dataset dbDs = (Dataset) c.uniqueResult();

        if (dbDs == null){
            throw new XMLParseException(String.format("Dataset not found in DB. %s", dataset.getVersion()));
        }

        return dbDs;
    }

}