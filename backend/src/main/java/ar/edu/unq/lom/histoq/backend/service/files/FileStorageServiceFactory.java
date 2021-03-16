package ar.edu.unq.lom.histoq.backend.service.files;

import ar.edu.unq.lom.histoq.backend.model.storage.FileStorage;
import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import ar.edu.unq.lom.histoq.backend.service.context.HistoQAppContext;
import ar.edu.unq.lom.histoq.backend.service.files.exception.UnknownFileStorageServiceTypeException;
import java.util.Dictionary;
import java.util.Hashtable;

public class FileStorageServiceFactory {

    private static LocalFileStorageService localFileStorageService;
    private static Dictionary<Long,FileStorageService> fileFileStorageServices = new Hashtable<>();

    public static FileStorageService getFileStorageService(FileStorage storage) {
        FileStorageService service = getFileStorageServiceInstance(storage);
        if( service == null )
            service = buildFileStorageServiceInstance(storage);
        return service;
    }

    public static FileStorageService getLocalFileStorageService() {
        if( FileStorageServiceFactory.localFileStorageService == null )
            FileStorageServiceFactory.localFileStorageService = new LocalFileStorageService(new FileSystemWrapper(),
                HistoQAppContext.getBean(ApplicationConfigProperties.class));
        return FileStorageServiceFactory.localFileStorageService;
    }

    private static FileStorageService getFileStorageServiceInstance(FileStorage storage) {
        return FileStorageServiceFactory.fileFileStorageServices.get(storage.getId());
    }

    private static FileStorageService buildFileStorageServiceInstance(FileStorage storage) {
        FileStorageService service;

        switch(storage.getType().getName()) {
            case "LOCAL":
                service = getLocalFileStorageService();
                break;
            case "GOOGLE":
                service = new GoogleFileStorageService(storage);
                break;
            default:
                throw new UnknownFileStorageServiceTypeException("unknown-file-storage-service-exception",
                        new String[]{storage.getType().getName()});
        }

        FileStorageServiceFactory.fileFileStorageServices.put(storage.getId(),service);
        return service;
    }
}
