package ar.edu.unq.lom.histoq.backend.service.dataexport;

import ar.edu.unq.lom.histoq.backend.service.dataexport.exception.DataExportException;
import ar.edu.unq.lom.histoq.backend.model.image.Image;
import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.PrintWriter;
import java.util.List;

public class XMLDataExporter extends BaseDataExporter {

    @Override
    public void export(List<ImageBatch> imageBatches, PrintWriter writer) {

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element batchesElement = document.createElement(getTranslation("data-export.xml-batches-element"));

            document.appendChild(batchesElement);

            imageBatches.forEach( batch -> {
                Element batchElement = document.createElement(getTranslation("data-export.xml-batch-element"));
                batchesElement.appendChild(batchElement);

                appendChild( document, batchElement,
                        "data-export.xml-attribute-protocol",
                        batch.getIndividual().getGroup().getProtocol().getLabel());

                appendChild( document, batchElement,
                        "data-export.xml-attribute-experimental-group",
                        batch.getIndividual().getGroup().getLabel());

                appendChild( document, batchElement,
                        "data-export.xml-attribute-individual",
                        batch.getIndividual().getLabel());

                appendChild( document, batchElement,
                        "data-export.xml-attribute-date-time",
                        batch.getDate().toString());

                appendChild( document, batchElement,
                        "data-export.xml-attribute-image-batch",
                        batch.getId().toString());

                appendChild( document, batchElement,
                        "data-export.xml-attribute-user",
                    batch.getUser().getFirstName() + " " + batch.getUser().getLastName());

                Element imagesElement = document.createElement(getTranslation("data-export.xml-images-element"));
                batchElement.appendChild(imagesElement);

                batch.getImageFiles().forEach( file -> {
                    Image image = file.getImage();
                    if (image == null) return;

                    Integer imageScaleValue = image.getScaleValue()!=null?image.getScaleValue():0;
                    Integer imageScalePixels = image.getScalePixels()!=null?image.getScalePixels():0;

                    Element imageElement = document.createElement(getTranslation("data-export.xml-image-element"));
                    imagesElement.appendChild(imageElement);

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-image-file",
                            file.getName());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-image",
                            image.getName());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-measurement-units",
                            image.getMeasurementUnit());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-reference-scale-value",
                            imageScaleValue.toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-reference-scale-pixels",
                    imageScalePixels.toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-conversion-factor",
                            image.getMeasurementFactor().toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-total-area-pixels",
                            image.getTotalArea().toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-tissue-area-pixels",
                            image.getTotalTissueArea().toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-viable-tissue-area-pixels",
                            image.getViableTissueArea().toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-necrotic-tissue-area-pixels",
                            image.getNecroticTissueArea().toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-total-area-units",
                            Image.pixelsAreaToUnits(image.getTotalArea(),image.getMeasurementFactor()).toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-tissue-area-units",
                            Image.pixelsAreaToUnits(image.getTotalTissueArea(),image.getMeasurementFactor()).toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-viable-tissue-area-units",
                            Image.pixelsAreaToUnits(image.getViableTissueArea(),image.getMeasurementFactor()).toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-necrotic-tissue-area-units",
                            Image.pixelsAreaToUnits(image.getNecroticTissueArea(),image.getMeasurementFactor()).toString());

                    appendChild( document, imageElement,
                            "data-export.xml-attribute-necrotic-tissue-percentage",
                            String.valueOf((image.getNecroticTissueArea()*100.0)/(image.getViableTissueArea()+image.getNecroticTissueArea())));
                });
            });

            writeXMLFile(document,writer);

        }
        catch(Exception e) {
            throw new DataExportException("data-export-exception",new String[]{e.getMessage()});
        }
    }

    private void appendChild(Document document, Element parentElement, String textId, String value ) {
        Element element = document.createElement(getTranslation(textId));
        element.appendChild(document.createTextNode(value));
        parentElement.appendChild(element);
    }

    private void writeXMLFile(Document document, PrintWriter writer) throws Exception {
        // write the xml file...
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(writer);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(source, result);
    }
}
