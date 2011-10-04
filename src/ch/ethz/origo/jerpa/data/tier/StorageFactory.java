package ch.ethz.origo.jerpa.data.tier;

public class StorageFactory {

	public static Storage getStorage() throws StorageException {
		return new DbStorage();
	}

}
