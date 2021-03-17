package ar.edu.unq.lom.histoq.backend.service.files;

import ar.edu.unq.lom.histoq.backend.model.storage.FileStorage;
import ar.edu.unq.lom.histoq.backend.service.files.exception.FileStoragesServiceException;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileUploadException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleFileStorageService implements FileStorageService {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private final FileStorage storage;
    private JsonNode applicationParameters;
    private GoogleCredential googleCredential;

    public GoogleFileStorageService(FileStorage storage) throws FileStoragesServiceException {
        this.storage = storage;

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),true);
            this.applicationParameters = mapper.readTree(this.storage.getApplicationParameters());

            this.googleCredential = GoogleCredential
                    .fromStream(new ByteArrayInputStream(this.storage.getServiceParameters().getBytes()))
                    .createScoped(SCOPES);
        }
        catch(Exception e) {
            throw new FileStoragesServiceException("file-storage-service-initialization-exception",
                    new String[]{e.getMessage()});
        }
    }

    @Override
    public String uploadFile(String folderName, MultipartFile file) {
        try {
            Drive driveInstance = getDriveInstance();
            String folderId = findOrCreateFolder(getDriveRootFolderId(), folderName, driveInstance);
            File fileMetadata = new File();
            fileMetadata.setParents(Collections.singletonList(folderId));
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata.setMimeType("image/png");
            File uploadedFile = driveInstance
                .files()
                .create(fileMetadata, new InputStreamContent(
                    file.getContentType(),
                    new ByteArrayInputStream(file.getBytes()))
                )
                .setFields("id").execute();
            return uploadedFile.getId();
        }
        catch (Exception e) {
            throw new ImageFileUploadException("image.upload-file-error",
                    new String[]{ file.getOriginalFilename(), e.getMessage()});
        }
    }

    @Override
    public void downloadFile(String fileId, OutputStream outputStream) {
        try {
            getDriveInstance().files().get(fileId).executeMediaAndDownloadTo(outputStream);
        }
        catch (Exception e) {
            throw new ImageFileUploadException("image.download-file-error",
                    new String[]{fileId, e.getMessage()});
        }
    }

    @Override
    public void deleteFile(String fileId) {
        try {
            getDriveInstance().files().delete(fileId).execute();
        }
        catch(GoogleJsonResponseException e) {
            if( e.getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND )
                return; //ignore file not found errors...
            else
                throw new ImageFileUploadException("image.delete-file-error",
                        new String[]{fileId, e.getMessage()});
        }
        catch(Exception e) {
            throw new ImageFileUploadException("image.delete-file-error",
                    new String[]{fileId, e.getMessage()});
        }
    }

    @Override
    public Path getFullPath(String relativePath) {
        return Paths.get(relativePath);
    }

    private Drive getDriveInstance() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service...
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                            this.googleCredential)
                .setApplicationName(getApplicationName())
                .build();
        return service;
    }

    private String getApplicationName() {
        return getParameterValue("application_name");
    }

    private String getDriveRootFolderId() {
        return getParameterValue("drive_root_folder_id");
    }

    private String getParameterValue(String parameterName) {
        return this.applicationParameters.get(parameterName).textValue();
    }

    private String getFolderId(String path) throws Exception {
        String parentId = null;
        String[] folderNames = path.split("/");
        Drive driveInstance = getDriveInstance();
        for (String name : folderNames) {
            parentId = findOrCreateFolder(parentId, name, driveInstance);
        }
        return parentId;
    }

    private String findOrCreateFolder(String parentId, String folderName, Drive driveInstance) throws Exception {
        String folderId = searchFolderId(parentId, folderName, driveInstance);
        // Folder already exists, so return id...
        if (folderId != null) {
            return folderId;
        }
        //Folder dont exists, create it and return folderId
        File fileMetadata = new File();
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setName(folderName);

        if (parentId != null) {
            fileMetadata.setParents(Collections.singletonList(parentId));
        }
        return driveInstance.files().create(fileMetadata)
                .setFields("id")
                .execute()
                .getId();
    }

    private String searchFolderId(String folderName, Drive service) throws Exception {
        return searchFolderId(null, folderName, service);
    }

    private String searchFolderId(String parentId, String folderName, Drive service) throws Exception {
        String folderId = null;
        String pageToken = null;
        FileList result = null;
        File fileMetadata = new File();
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setName(folderName);
        do {
            String query = " mimeType = 'application/vnd.google-apps.folder' ";
            if (parentId == null) {
                query = query + " and 'root' in parents";
            } else {
                query = query + " and '" + parentId + "' in parents";
            }
            result = service.files().list().setQ(query)
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getFiles()) {
                if (file.getName().equalsIgnoreCase(folderName)) {
                    folderId = file.getId();
                }
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null && folderId == null);
        return folderId;
    }

}
