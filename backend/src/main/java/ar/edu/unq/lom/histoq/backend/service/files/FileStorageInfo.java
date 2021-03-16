package ar.edu.unq.lom.histoq.backend.service.files;

import ar.edu.unq.lom.histoq.backend.model.storage.FileStorage;
import lombok.Data;

@Data
public class FileStorageInfo {
    private FileStorage storage;
    private String idInStorage;

    FileStorageInfo() {}

    FileStorageInfo(FileStorage storage, String idInStorage) {
        setStorage(storage);
        setIdInStorage(idInStorage);
    }
}
