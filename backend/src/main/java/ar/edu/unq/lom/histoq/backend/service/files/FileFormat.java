package ar.edu.unq.lom.histoq.backend.service.files;

import lombok.Data;

@Data
public class FileFormat {
    private Integer id;
    private String  name;
    private String  extension;
    private String  contentType;

    public FileFormat(){}

    public FileFormat(Integer id, String contentType, String name, String extension) {
        setId(id);
        setContentType(contentType);
        setName(name);
        setExtension(extension);
    }
}
