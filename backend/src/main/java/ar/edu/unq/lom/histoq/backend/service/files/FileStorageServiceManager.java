package ar.edu.unq.lom.histoq.backend.service.files;

import ar.edu.unq.lom.histoq.backend.model.storage.FileStorage;
import ar.edu.unq.lom.histoq.backend.repository.storage.FileStorageRepository;
import ar.edu.unq.lom.histoq.backend.service.files.exception.FileStoragesServiceException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;

@Service
public class FileStorageServiceManager {

    private final FileStorageRepository fileStorageRepository;

    public  FileStorageServiceManager(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
    }

    public FileStorageInfo uploadFile(String folderName, MultipartFile file) {
        FileStorage storage = getAvailableFileStorage();
        String idInStorage = getFileStorageService(storage).uploadFile(folderName,file);
        return new FileStorageInfo(storage, idInStorage);
    }

    public void downloadFile(FileStorage storage, String fileId, OutputStream outputStream) {
        getFileStorageService(storage).downloadFile(fileId, outputStream);
    }

    public void downloadLocalFile(String fileId, OutputStream outputStream) {
        getLocalFileStorageService().downloadFile(fileId, outputStream);
    }

    public void deleteFile(FileStorage storage, String fileId) {
        getFileStorageService(storage).deleteFile(fileId);
    }

    public void copyFileToLocalStorage(FileStorage storage, String remoteFileId,
                                       String localFolderName, String localFileName)
            throws Exception {

        // if file already exists locally, exit without copy...
        FileStorageService localFileStorageService = getLocalFileStorageService();
        if( Files.exists(localFileStorageService.getFullPath(localFolderName+"/"+localFileName) ))
            return;

        FileStorageService storageService = getFileStorageService(storage);
        if( !storageService.isLocal() ) { //if there is an active remote storage service...
            File localFileDir = new File(localFileStorageService.getFullPath(localFolderName).toString());
            localFileDir.mkdirs();
            File localFile = new File(localFileDir,localFileName);
            OutputStream outputStream = new FileOutputStream(localFile);
            storageService.downloadFile(remoteFileId, outputStream);
        }
    }

    public FileStorageInfo copyLocalFileToRemoteStorage(String folderName, String fileName) {
        String idInStorage = folderName + "/" + fileName;
        FileStorageInfo storageInfo = new FileStorageInfo(getAvailableFileStorage(), idInStorage);
        FileStorageService storageService = getFileStorageService(storageInfo.getStorage());

        if( !storageService.isLocal() ) {
            idInStorage = storageService.uploadFile(folderName, localFileToMultipartFile(folderName,fileName));
            storageInfo.setIdInStorage(idInStorage);
        }

        return storageInfo;
    }

    private MultipartFile localFileToMultipartFile(String folderName, String fileName) {
        try {
            File file = new File(getLocalFileStorageService()
                    .getFullPath(folderName + "/" + fileName).toString());

            FileItem fileItem = new DiskFileItemFactory().createItem(fileName,
                    Files.probeContentType(file.toPath()), false, file.getName());

            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = fileItem.getOutputStream();
            IOUtils.copy(inputStream, outputStream);

            return new CommonsMultipartFile(fileItem);
        }
        catch(Exception e) {
            throw new FileStoragesServiceException("image.file-processing-error",
                    new String[]{fileName, e.getMessage()});
        }
    }

    private FileStorageService getAvailableFileStorageService() {
        return getFileStorageService(getAvailableFileStorage());
    }

    private FileStorage getAvailableFileStorage() {
        return this.fileStorageRepository.findByEnabledTrue().get(0);
    }

    private FileStorageService getFileStorageService(FileStorage storage) {
        return FileStorageServiceFactory.getFileStorageService(storage);
    }

    private FileStorageService getLocalFileStorageService() {
        return FileStorageServiceFactory.getLocalFileStorageService();
    }

}
