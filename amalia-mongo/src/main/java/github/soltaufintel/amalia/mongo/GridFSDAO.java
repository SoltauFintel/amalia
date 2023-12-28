package github.soltaufintel.amalia.mongo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bson.types.ObjectId;

import com.mongodb.MongoGridFSException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;

import github.soltaufintel.amalia.web.image.BinaryData;

/**
 * Save/load files into MongoDB GridFS. The files can be quite large and binary.
 */
public class GridFSDAO {
    private final GridFSBucket bucket;

    /**
     * @param db         Mongo Database
     * @param collection base name of the GridFS Collection
     */
    public GridFSDAO(MongoDatabase db, String collection) {
        bucket = GridFSBuckets.create(db, collection);
    }

    /**
     * Save file into database
     * 
     * @param in file from file system as File object
     * @param dn file name in database
     * @return id
     * @throws IOException
     */
    public ObjectId save(File in, String dn) throws IOException {
        try (FileInputStream fis = new FileInputStream(in)) {
            return bucket.uploadFromStream(dn, fis);
        }
    }

    /**
     * Save file into database
     * 
     * @param in file from file system as InputStream
     * @param dn file name in database
     * @return id
     * @throws IOException
     */
    public ObjectId save(InputStream in, String dn) throws IOException {
        return bucket.uploadFromStream(dn, in);
    }

    /**
     * Load file from database into memory
     * 
     * @param id Id des GridFS Objekts
     * @return data and filename. data is null if file not found.
     * @throws IOException
     */
    public BinaryData load(ObjectId id) throws IOException {
        ByteArrayOutputStream saveTo = new ByteArrayOutputStream();
        GridFSDownloadStream downloadStream;
        try {
            downloadStream = bucket.openDownloadStream(id);
        } catch (MongoGridFSException e) {
            return new BinaryData(null, null);
        }
        String dn = downloadStream.getGridFSFile().getFilename();
        downloadStream.close();
        // Wäre gut, wenn Morphia das in einer Aktion könnte. Ich starte erneut DownloadStream.
        loadToOutputStream(id, saveTo);
        return new BinaryData(saveTo, dn);
    }

    public void loadToOutputStream(ObjectId id, OutputStream saveTo) throws IOException {
        bucket.downloadToStream(id, saveTo);
    }

    /**
     * Remove file from database
     * 
     * @param id Id des GridFS Objekts
     */
    public void delete(ObjectId id) {
        bucket.delete(id);
    }
}
