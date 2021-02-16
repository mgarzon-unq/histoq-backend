package ar.edu.unq.lom.histoq.backend.service.dataexport;

import ar.edu.unq.lom.histoq.backend.service.dataexport.exception.DataExportException;
import ar.edu.unq.lom.histoq.backend.service.files.FileFormat;
import java.util.Arrays;
import java.util.List;

public class DataExporterFactory {
    private static List<FileFormat> supportedDataExportFileFormats = Arrays.asList(
            new FileFormat[]{
                    new FileFormat(1, "text/csv", "CSV", "csv"),
                    new FileFormat(2,"text/xml","XML","xml"),
                    new FileFormat(3,"text/json","JSON","json")}
    );

    public static DataExporter getDataExporter(FileFormat fileFormat) {
        switch(fileFormat.getId()) {
            case 1: return new CSVDataExporter();
            case 2: return new XMLDataExporter();
            case 3: return new JSONDataExporter();
            default: throw new DataExportException("data-export-exception",
                    new String[]{"Unsupported data exporter "+fileFormat.getName()});
        }
    }

    public static List<FileFormat> getSupportedDataExportFileFormats() {
        return supportedDataExportFileFormats;
    }
}
