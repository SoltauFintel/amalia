package github.soltaufintel.amalia.mongo;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.image.BinaryData;
import github.soltaufintel.amalia.web.image.IBinaryDataLoader;

public class BinaryDataLoader implements IBinaryDataLoader {
	private final GridFSDAO gridFS;
	private final ObjectId id;

	public BinaryDataLoader(GridFSDAO gridFS, String id) {
		this(gridFS, new ObjectId(id));
	}

	public BinaryDataLoader(GridFSDAO gridFS, ObjectId id) {
		this.gridFS = gridFS;
		this.id = id;
	}

	@Override
	public BinaryData load() {
		try {
			return gridFS.load(id);
		} catch (IOException ex) {
			Logger.error(ex);
			return null;
		}
	}
}
